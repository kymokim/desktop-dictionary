package com.kymokim.desktopdictionary.orders.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "orders")
@Entity
@Data
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantUid;  // 고유 결제 ID (외부 시스템과 연동)
    private OrdersStatus ordersStatus;
    private String productName;
    private int amount;

    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private String firstImgUrl;
    private String sellerBankInfo;

    private LocalDateTime createdAt;

    @Builder
    public Orders(String merchantUid, OrdersStatus ordersStatus, String productName, int amount, Long buyerId, Long sellerId, Long productId, String firstImgUrl, String sellerBankInfo) {
        this.merchantUid = merchantUid;
        this.ordersStatus = ordersStatus;
        this.productName = productName;
        this.amount = amount;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.firstImgUrl = firstImgUrl;
        this.sellerBankInfo = sellerBankInfo;
        this.createdAt = LocalDateTime.now();
    }
}