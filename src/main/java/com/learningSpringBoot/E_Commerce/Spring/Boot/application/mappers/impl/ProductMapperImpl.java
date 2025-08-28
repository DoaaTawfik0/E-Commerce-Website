package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.ProductDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Component
public class ProductMapperImpl implements Mapper<ProductEntity, ProductDto> {

    private final ModelMapper modelMapper;

    @Override
    public ProductDto mapTo(ProductEntity productEntity) {
        ProductDto product = modelMapper.map(productEntity, ProductDto.class);

        if (productEntity.getCategory() != null) {
            product.setCategoryId(productEntity.getCategory().getCategoryId());
            product.setCategoryName(productEntity.getCategory().getName());
        }
        return product;
    }

    @Override
    public ProductEntity mapFrom(ProductDto productDto) {
        return modelMapper.map(productDto, ProductEntity.class);
    }
}
