package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private Integer categoryId;
    private String categoryName;
    private Integer productId;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String imageUrl;
    private LocalDateTime createdAt;
}