package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl;


import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CategoryDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapperImpl implements Mapper<CategoryEntity, CategoryDto> {

    private final ModelMapper modelMapper;

    public CategoryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto mapTo(CategoryEntity categoryEntity) {
        return modelMapper.map(categoryEntity, CategoryDto.class);
    }

    @Override
    public CategoryEntity mapFrom(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, CategoryEntity.class);
    }
}
