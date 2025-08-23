package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private Integer categoryId;
    private String name;
    private String description;
    private List<ProductDto> products;
}