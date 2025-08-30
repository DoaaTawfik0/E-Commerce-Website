package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderItemResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.OrderEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderItemMapper implements Mapper<OrderItemEntity, OrderItemResponseDto> {

    private final ModelMapper modelMapper;

    @Override
    public OrderItemResponseDto mapTo(OrderItemEntity entity) {
        if (entity == null) return null;

        OrderItemResponseDto dto = modelMapper.map(entity, OrderItemResponseDto.class);

        ProductEntity product = entity.getProduct();
        if (product != null) {
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getName());
        }

        // Explicit mappings
        dto.setUnitPrice(entity.getPriceAtPurchase());
        dto.setSubtotal(entity.getQuantity() * entity.getPriceAtPurchase());

        return dto;
    }

    @Override
    public OrderItemEntity mapFrom(OrderItemResponseDto dto) {
        if (dto == null) return null;

        OrderItemEntity entity = modelMapper.map(dto, OrderItemEntity.class);

        // Service will inject Product + Order
        entity.setProduct(null);
        entity.setOrder(null);

        // Explicit mappings
        entity.setPriceAtPurchase(dto.getUnitPrice());
        entity.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0);

        return entity;
    }
}
