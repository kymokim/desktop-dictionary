package com.kymokim.desktopdictionary.orders.dto;

import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.orders.entity.Orders;
import com.kymokim.desktopdictionary.orders.entity.OrdersStatus;
import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class ResponseOrders {

    @Data
    @Builder
    public static class CreateOrdersRsDto{
        private String pg;
        private String pay_method;
        private String merchant_uid;
        private String name;
        private int amount;
        private String buyer_email;
        private String buyer_name;
        private String buyer_tel;
        private String buyer_addr;
        private String buyer_postcode;

        public static CreateOrdersRsDto toDto(String pg, String pay_method, String merchant_uid, int amount, Auth buyer){
            return CreateOrdersRsDto.builder()
                    .pg(pg)
                    .pay_method(pay_method)
                    .merchant_uid(merchant_uid)
                    .name("Desktop Dictionary - Used Trade")
                    .amount(amount)
                    .buyer_email(buyer.getEmail())
                    .buyer_name(buyer.getName())
                    .buyer_tel(buyer.getTel())
                    .buyer_addr(buyer.getAddr())
                    .buyer_postcode(buyer.getPostcode())
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetUserOrdersDto{
        private Long id;
        private String merchantUid;
        private OrdersStatus ordersStatus;
        private Long usedPostId;
        private String productName;
        private int productPrice;
        private String firstImgUrl;
        private LocalDateTime createdAt;

        public static GetUserOrdersDto toDto(Orders orders){
            return GetUserOrdersDto.builder()
                    .id(orders.getId())
                    .merchantUid(orders.getMerchantUid())
                    .ordersStatus(orders.getOrdersStatus())
                    .usedPostId(orders.getProductId())
                    .productName(orders.getProductName())
                    .productPrice(orders.getAmount())
                    .firstImgUrl(orders.getFirstImgUrl())
                    .createdAt(orders.getCreatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetAdminOrdersDto{
        private Long id;
        private String merchantUid;
        private OrdersStatus ordersStatus;
        private Long usedPostId;
        private String productName;
        private int productPrice;
        private String firstImgUrl;
        private LocalDateTime createdAt;
        private String sellerBankInfo;

        public static GetAdminOrdersDto toDto(Orders orders){
            return GetAdminOrdersDto.builder()
                    .id(orders.getId())
                    .merchantUid(orders.getMerchantUid())
                    .ordersStatus(orders.getOrdersStatus())
                    .usedPostId(orders.getProductId())
                    .productName(orders.getProductName())
                    .productPrice(orders.getAmount())
                    .firstImgUrl(orders.getFirstImgUrl())
                    .createdAt(orders.getCreatedAt())
                    .sellerBankInfo(orders.getSellerBankInfo())
                    .build();
        }
    }
}
