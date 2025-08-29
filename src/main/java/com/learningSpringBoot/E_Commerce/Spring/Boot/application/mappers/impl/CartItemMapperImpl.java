package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartItemResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapperImpl implements Mapper<CartItemEntity, CartItemResponseDto> {

    public CartItemResponseDto mapTo(CartItemEntity entity) {
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setProductId(entity.getProduct().getProductId());
        dto.setProductName(entity.getProduct().getName());
        dto.setPrice(entity.getProduct().getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setSubtotal(entity.getSubtotal());
        return dto;
    }

    public CartItemEntity mapFrom(CartItemResponseDto dto) {
        CartItemEntity entity = new CartItemEntity();
        entity.setQuantity(dto.getQuantity());
        // product and cart must be set in service
        return entity;
    }
}
