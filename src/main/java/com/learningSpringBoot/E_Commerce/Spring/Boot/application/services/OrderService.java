package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.OrderEntity;

import java.util.List;

public interface OrderService {
    OrderEntity checkout(Integer userId);

    List<OrderEntity> getUserOrders(Integer userId);

    OrderEntity getOrderById(Integer orderId);
}
