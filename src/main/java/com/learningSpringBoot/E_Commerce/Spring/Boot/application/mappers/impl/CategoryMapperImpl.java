package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl;


import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CategoryDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.ProductDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class CategoryMapperImpl implements Mapper<CategoryEntity, CategoryDto> {

    private final ModelMapper modelMapper;
    private final Mapper<ProductEntity, ProductDto> productMapper;


    @Override
    public CategoryDto mapTo(CategoryEntity categoryEntity) {
        CategoryDto dto = modelMapper.map(categoryEntity, CategoryDto.class);

        if (categoryEntity.getProductEntities() != null) {
            List<ProductDto> products = categoryEntity.getProductEntities()
                    .stream()
                    .map(productMapper::mapTo)   // uses ProductMapperImpl
                    .toList();

            dto.setProducts(products);
        }
        return dto;
    }

    @Override
    public CategoryEntity mapFrom(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, CategoryEntity.class);
    }
}
