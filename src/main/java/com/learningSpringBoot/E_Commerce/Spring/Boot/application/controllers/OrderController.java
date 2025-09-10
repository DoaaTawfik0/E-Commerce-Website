package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderDetailsResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.OrderEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl.OrderMapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/{userId}/checkout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDetailsResponseDto> checkout(
            @PathVariable Integer userId) {

        OrderEntity order = orderService.checkout(userId);
        return ResponseEntity.ok(orderMapper.mapToDetail(order));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(@PathVariable Integer userId) {
        List<OrderEntity> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orderMapper.mapToSummaryList(orders));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDetailsResponseDto> getOrderDetails(@PathVariable Integer orderId) {
        OrderEntity order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderMapper.mapToDetail(order));
    }
}