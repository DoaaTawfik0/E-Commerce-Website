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
public class CartResponseDto {
    private Integer cartId;
    private Integer userId;
    private List<CartItemResponseDto> cartItems;
    private Double totalAmount;
    private LocalDateTime updatedAt;
}
