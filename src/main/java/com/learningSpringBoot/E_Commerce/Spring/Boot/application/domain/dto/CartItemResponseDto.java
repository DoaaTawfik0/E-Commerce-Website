package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponseDto {
    private Integer productId;
    private String productName;
    private Double price;
    private int quantity;
    private Double subtotal;
}

