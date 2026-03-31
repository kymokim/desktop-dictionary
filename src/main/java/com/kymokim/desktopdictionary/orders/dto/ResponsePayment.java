package com.kymokim.desktopdictionary.orders.dto;

import lombok.Builder;
import lombok.Data;

public class ResponsePayment {

    @Builder
    @Data
    public static class verifyPaymentDto{

        private String merchantUid;
        private boolean isVerified;
        private int amount;
    }
}
