package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartItemResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CartMapperImpl implements Mapper<CartEntity, CartResponseDto> {

    private final ModelMapper modelMapper;
    private final CartItemMapperImpl cartItemMapper;

    @PostConstruct
    void setupMappings() {
        modelMapper.typeMap(CartEntity.class, CartResponseDto.class)
                .addMapping(CartEntity::getCartId, CartResponseDto::setCartId)
                .addMapping(src -> src.getUser().getUserId(), CartResponseDto::setUserId)
                .addMapping(CartEntity::getUpdatedAt, CartResponseDto::setUpdatedAt);
    }

    @Override
    public CartResponseDto mapTo(CartEntity entity) {
        CartResponseDto dto = new CartResponseDto();
        dto.setCartId(entity.getCartId());
        dto.setUserId(entity.getUser().getUserId());
        dto.setUpdatedAt(entity.getUpdatedAt());

        List<CartItemResponseDto> itemDtos = new ArrayList<>();
        if (entity.getCartItems() != null && !entity.getCartItems().isEmpty()) {
            itemDtos = entity.getCartItems().stream()
                    .map(cartItemMapper::mapTo)
                    .collect(Collectors.toList());
        }

        dto.setCartItems(itemDtos);

        double total = itemDtos.stream()
                .mapToDouble(CartItemResponseDto::getSubtotal)
                .sum();
        dto.setTotalAmount(total);

        return dto;
    }

    @Override
    public CartEntity mapFrom(CartResponseDto dto) {
        CartEntity entity = modelMapper.map(dto, CartEntity.class);
        if (dto.getCartItems() != null) {
            List<CartItemEntity> items = dto.getCartItems().stream()
                    .map(cartItemMapper::mapFrom)
                    .toList();
            entity.setCartItems(items);
        }
        return entity;
    }
}
