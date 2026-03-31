package com.kymokim.desktopdictionary.orders.util;

import com.kymokim.desktopdictionary.common.service.RedisUtil;
import com.kymokim.desktopdictionary.orders.dto.ResponsePayment;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(PaymentGateway.class);

    @Value("${iamport.api_key}")
    private String apiKey;

    @Value("${iamport.api_secret}")
    private String apiSecret;

    private IamportClient iamportClient;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void initIamportClient() {
        this.iamportClient = new IamportClient(apiKey, apiSecret);
    }

    @Transactional
    public ResponsePayment.verifyPaymentDto verifyPayment(String impUid) {
        logger.info("impUid : {}", impUid);

        IamportResponse<Payment> response = null;
        try {
            response = iamportClient.paymentByImpUid(impUid);
        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException(e);
        }

        if (response != null && response.getResponse() != null) {
            Payment paymentData = response.getResponse();
            String merchantUid = paymentData.getMerchantUid();

            String storedAmount = redisUtil.getData(merchantUid);
            if (storedAmount != null && storedAmount.equals(String.valueOf(paymentData.getAmount().intValue()))){

                return ResponsePayment.verifyPaymentDto.builder()
                        .merchantUid(merchantUid)
                        .isVerified(true)
                        .amount(paymentData.getAmount().intValue())
                        .build();
            }
        }
        return ResponsePayment.verifyPaymentDto.builder()
                .isVerified(false)
                .build();
    }

    public boolean cancelPayment(String merchantUid) {
        try {
            // 결제 취소 요청을 위한 설정
            CancelData cancelData = new CancelData(merchantUid, false); // merchantUid를 기준으로 전액 취소
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);

            // 결제 취소가 성공했는지 확인
            if (response != null && response.getResponse() != null && "cancelled".equals(response.getResponse().getStatus())) {
                logger.info("Payment cancelled successfully for merchantUid: {}", merchantUid);
                return true;
            }
        } catch (IamportResponseException | IOException e) {
            logger.error("Failed to cancel payment with merchantUid: {}. Error: {}", merchantUid, e.getMessage());
        }
        return false;
    }
}
