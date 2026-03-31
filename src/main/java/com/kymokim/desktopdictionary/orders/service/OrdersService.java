package com.kymokim.desktopdictionary.orders.service;

import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.common.exception.error.NotFoundUserException;
import com.kymokim.desktopdictionary.common.service.RedisUtil;
import com.kymokim.desktopdictionary.orders.dto.RequestOrders;
import com.kymokim.desktopdictionary.orders.dto.ResponseOrders;
import com.kymokim.desktopdictionary.orders.dto.ResponsePayment;
import com.kymokim.desktopdictionary.orders.entity.Orders;
import com.kymokim.desktopdictionary.orders.entity.OrdersStatus;
import com.kymokim.desktopdictionary.orders.repository.OrdersRepository;
import com.kymokim.desktopdictionary.orders.util.PaymentGateway;
import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import com.kymokim.desktopdictionary.usedPost.repository.UsedPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private UsedPostRepository usedPostRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PaymentGateway paymentGateway;

    @Transactional
    public ResponseOrders.CreateOrdersRsDto createOrders(RequestOrders.CreateOrdersRqDto createOrdersRqDto, Optional<String> token) {

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth buyer = authRepository.findByEmail(email);
        if (buyer == null) {
            logger.error("Buyer not found with email: {}", email);
            throw new NotFoundUserException();
        }

        UsedPost product = usedPostRepository.findById(createOrdersRqDto.getUsedPostId()).orElseThrow(() -> {
            logger.error("Product not found with productId: {}", createOrdersRqDto.getUsedPostId());
            return new IllegalStateException("Product not found");
        });

        String merchantUid = product.getId() + "_" + System.currentTimeMillis();
        redisUtil.setDataExpire(merchantUid, String.valueOf(product.getProductPrice()),60 * 30L);
        logger.info("Order saved in Redis for 30 minute. merchantUid : {}, Amount : {}", merchantUid, product.getProductPrice());

        return ResponseOrders.CreateOrdersRsDto.toDto(createOrdersRqDto.getPg(), "card", merchantUid, product.getProductPrice(), buyer);
    }

    @Transactional
    public boolean checkOrders(String impUid, Optional<String> token){

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth buyer = authRepository.findByEmail(email);
        if (buyer == null) {
            logger.error("Buyer not found with email: {}", email);
            throw new NotFoundUserException();
        }

        ResponsePayment.verifyPaymentDto verifyPaymentDto = paymentGateway.verifyPayment(impUid);

        if (verifyPaymentDto.isVerified()){
            String merchantUid = verifyPaymentDto.getMerchantUid();
            Long productId = Long.parseLong(merchantUid.split("_")[0]);  // ex: "123_160000000" -> productId = 123
            UsedPost usedPost = usedPostRepository.findById(productId).get();
            usedPost.sold();
            usedPostRepository.save(usedPost);

            String productName = usedPost.getProductName();
            Auth seller = authRepository.findByEmail(usedPost.getWriterEmail());
            String sellerBankInfo = seller.getBankName() + " " + seller.getBankAccount();

            String firstImgUrl = null;

            if(!usedPost.getImgUrlList().isEmpty())
                firstImgUrl = usedPost.getImgUrlList().getFirst().getUrl();

            Orders orders = Orders.builder()
                    .merchantUid(merchantUid)
                    .ordersStatus(OrdersStatus.PAID)
                    .productName(productName)
                    .amount(verifyPaymentDto.getAmount())
                    .buyerId(buyer.getId())
                    .sellerId(seller.getId())
                    .productId(productId)
                    .firstImgUrl(firstImgUrl)
                    .sellerBankInfo(sellerBankInfo)
                    .build();

            ordersRepository.save(orders);
            logger.info("Order {} is checked and saved.", merchantUid);
            return true;
        }
        return false;
    }

    @Transactional
    public void confirmOrders(Optional<String> token, String merchantUid){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth buyer = authRepository.findByEmail(email);
        if (buyer == null) {
            logger.error("Buyer not found with email: {}", email);
            throw new NotFoundUserException();
        }
        Orders orders = ordersRepository.findByMerchantUid(merchantUid);

        if (!orders.getOrdersStatus().equals(OrdersStatus.PAID)) {
            logger.warn("Order is not in a confirmable state. Current status: {}", orders.getOrdersStatus());
            throw new RuntimeException("Order status needs to be PAID.");
        }

        if (Objects.equals(orders.getBuyerId(), buyer.getId())) {
            orders.setOrdersStatus(OrdersStatus.CONFIRMED);
            ordersRepository.save(orders);
        }
    }

    @Transactional
    public void settleOrders(Optional<String> token, String merchantUid){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(email);
        if (admin == null) {
            logger.error("admin not found with email: {}", email);
            throw new NotFoundUserException();
        }
        Orders orders = ordersRepository.findByMerchantUid(merchantUid);

        if (!orders.getOrdersStatus().equals(OrdersStatus.CONFIRMED)) {
            logger.warn("Order is not in a confirmable state. Current status: {}", orders.getOrdersStatus());
            throw new RuntimeException("Order status needs to be CONFIRMED.");
        }

        if (admin.getRole().equals(Role.ADMIN)) {
            orders.setOrdersStatus(OrdersStatus.SETTLED);
            ordersRepository.save(orders);
        }
    }

    @Transactional
    public List<ResponseOrders.GetUserOrdersDto> getBuyerOrders(Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth buyer = authRepository.findByEmail(email);
        if (buyer == null) {
            logger.error("Buyer not found with email: {}", email);
            throw new NotFoundUserException();
        }
        

        List<Orders> ordersList = ordersRepository.findAllByBuyerId(buyer.getId());
        return ordersList.stream()
                .map(ResponseOrders.GetUserOrdersDto::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ResponseOrders.GetUserOrdersDto> getSellerOrders(Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth seller = authRepository.findByEmail(email);
        if (seller == null) {
            logger.error("Seller not found with email: {}", email);
            throw new NotFoundUserException();
        }


        List<Orders> ordersList = ordersRepository.findAllBySellerId(seller.getId());
        return ordersList.stream()
                .map(ResponseOrders.GetUserOrdersDto::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ResponseOrders.GetAdminOrdersDto> getAdminOrders(Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(email);
        if (admin == null) {
            logger.error("Admin not found with email: {}", email);
            throw new NotFoundUserException();
        }
        if (!admin.getRole().equals(Role.ADMIN)){
            logger.error("Not admin");
            throw new RuntimeException("No permission.");
        }


        List<Orders> ordersList = ordersRepository.findAll();
        return ordersList.stream()
                .map(ResponseOrders.GetAdminOrdersDto::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void requestCancelOrders(Optional<String> token, String merchantUid){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth buyer = authRepository.findByEmail(email);
        if (buyer == null) {
            logger.error("Buyer not found with email: {}", email);
            throw new NotFoundUserException();
        }
        Orders orders = ordersRepository.findByMerchantUid(merchantUid);

        if (!orders.getOrdersStatus().equals(OrdersStatus.PAID)) {
            logger.warn("Order is not in a confirmable state. Current status: {}", orders.getOrdersStatus());
            throw new RuntimeException("Order status needs to be PAID.");
        }

        if (Objects.equals(orders.getBuyerId(), buyer.getId())) {
            orders.setOrdersStatus(OrdersStatus.CANCEL_REQUESTED);
            ordersRepository.save(orders);
        }
    }

    @Transactional
    public boolean cancelOrders(String merchantUid, Optional<String> token) {
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new NotFoundUserException();
        }

        Orders orders = ordersRepository.findByMerchantUid(merchantUid);

        if (!(user.getRole().equals(Role.ADMIN) || user.getId().equals(orders.getSellerId()))){
            logger.error("Not admin or seller.");
            throw new RuntimeException("No permission.");
        }

        UsedPost usedPost = usedPostRepository.findById(orders.getProductId()).get();
        usedPost.unSold();
        usedPostRepository.save(usedPost);


        if (orders == null)
            throw new IllegalStateException("Order not found with merchantUid: " + merchantUid);

        if (!orders.getOrdersStatus().equals(OrdersStatus.CANCEL_REQUESTED)) {
            logger.warn("Order is not in a cancellable state. Current status: {}", orders.getOrdersStatus());
            return false;
        }

        boolean isCancelled = paymentGateway.cancelPayment(merchantUid);

        if (isCancelled) {
            orders.setOrdersStatus(OrdersStatus.CANCELLED);
            ordersRepository.save(orders);
            logger.info("Order {} has been cancelled", merchantUid);
            return true;
        }

        logger.error("Failed to cancel order with merchantUid: {}", merchantUid);
        return false;
    }
}
