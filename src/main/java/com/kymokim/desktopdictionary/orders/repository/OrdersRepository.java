package com.kymokim.desktopdictionary.orders.repository;

import com.kymokim.desktopdictionary.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Orders findByMerchantUid(String merchantUid);

    List<Orders> findAllByBuyerId(Long buyerId);
    List<Orders> findAllBySellerId(Long sellerId);
}
