package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderDetailsResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderItemResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.OrderEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderResponseDto mapToSummary(OrderEntity entity) {
        if (entity == null) return null;

        return OrderResponseDto.builder()
                .orderId(entity.getOrderId())
                .createdAt(entity.getOrderDate())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus().name())
                .build();
    }

    public OrderDetailsResponseDto mapToDetail(OrderEntity entity) {
        if (entity == null) return null;

        List<OrderItemResponseDto> itemDtos = null;
        if (entity.getOrderItems() != null) {
            itemDtos = entity.getOrderItems()
                    .stream()
                    .map(orderItemMapper::mapTo)
                    .collect(Collectors.toList());
        }

        return OrderDetailsResponseDto.builder()
                .orderId(entity.getOrderId())
                .createdAt(entity.getOrderDate())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus().name())
                .items(itemDtos)
                .build();
    }

    public List<OrderResponseDto> mapToSummaryList(List<OrderEntity> entities) {
        return entities.stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }
}