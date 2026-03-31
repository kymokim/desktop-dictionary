package com.kymokim.desktopdictionary.orders.dto;

import com.kymokim.desktopdictionary.orders.entity.Orders;
import lombok.Builder;
import lombok.Data;

public class RequestOrders {

    @Builder
    @Data
    public static class CreateOrdersRqDto {
        private Long usedPostId;
        private String pg;
    }
}
