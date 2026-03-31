package com.kymokim.desktopdictionary.orders.controller;

import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import com.kymokim.desktopdictionary.orders.dto.RequestOrders;
import com.kymokim.desktopdictionary.orders.dto.ResponseOrders;
import com.kymokim.desktopdictionary.orders.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrdersController {

    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createOrders(@RequestBody RequestOrders.CreateOrdersRqDto createOrdersRqDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ResponseOrders.CreateOrdersRsDto createOrdersRsDto = ordersService.createOrders(createOrdersRqDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Order created successfully.")
                .data(createOrdersRsDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/check")
    public ResponseEntity<ResponseDto> checkOrders(@RequestBody String impUid, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        boolean isChecked = ordersService.checkOrders(impUid, token);

        ResponseDto responseDto = ResponseDto.builder()
                .message(isChecked ? "Order checked and saved successfully." : "Order check failed.")
                .build();
        return ResponseEntity.status(isChecked ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @GetMapping("/get/buyer")
    public ResponseEntity<ResponseDto> getBuyerOrders(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseOrders.GetUserOrdersDto> getAllOrdersDtoList = ordersService.getBuyerOrders(token);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Buyer orders list retrieved successfully.")
                .data(getAllOrdersDtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/seller")
    public ResponseEntity<ResponseDto> getSellerOrders(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseOrders.GetUserOrdersDto> getAllOrdersDtoList = ordersService.getSellerOrders(token);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Seller orders list retrieved successfully.")
                .data(getAllOrdersDtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/admin")
    public ResponseEntity<ResponseDto> getAdminOrders(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseOrders.GetAdminOrdersDto> getAllOrdersDtoList = ordersService.getAdminOrders(token);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Orders list retrieved successfully.")
                .data(getAllOrdersDtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/confirm/{merchantUid}")
    public ResponseEntity<ResponseDto> confirmOrders(HttpServletRequest request, @PathVariable String merchantUid){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ordersService.confirmOrders(token, merchantUid);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Orders confirmed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/settle/{merchantUid}")
    public ResponseEntity<ResponseDto> settleOrders(HttpServletRequest request, @PathVariable String merchantUid){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ordersService.settleOrders(token, merchantUid);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Orders settled successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/requestCancel/{merchantUid}")
    public ResponseEntity<ResponseDto> requestCancelOrders(HttpServletRequest request, @PathVariable String merchantUid){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ordersService.requestCancelOrders(token, merchantUid);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Orders cancellation requested successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 결제 취소 API
    @PostMapping("/cancel/{merchantUid}")
    public ResponseEntity<ResponseDto> cancelOrders(@PathVariable String merchantUid, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        boolean isCancelled = ordersService.cancelOrders(merchantUid, token);

        ResponseDto responseDto = ResponseDto.builder()
                .message(isCancelled ? "Order cancelled successfully." : "Order cancellation failed.")
                .build();
        return ResponseEntity.status(isCancelled ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(responseDto);
    }
}
