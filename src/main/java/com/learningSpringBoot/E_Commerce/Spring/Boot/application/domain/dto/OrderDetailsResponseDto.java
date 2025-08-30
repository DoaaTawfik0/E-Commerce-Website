package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsResponseDto {
    private Integer orderId;
    private LocalDateTime createdAt;
    private Double totalAmount;
    private String status;
    private List<OrderItemResponseDto> items;
}
