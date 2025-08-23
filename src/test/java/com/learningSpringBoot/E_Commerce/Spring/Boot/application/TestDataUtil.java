package com.learningSpringBoot.E_Commerce.Spring.Boot.application;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CategoryDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TestDataUtil {

    /*********************  Category Data Utility Test  *********************/
    public static CategoryEntity createTestCategoryEntityA() {
        return CategoryEntity.builder()
                .name("Sports")
                .description("Sports equipment and accessories")
                .productEntities(null)
                .build();
    }

    public static CategoryEntity createTestCategoryEntityB() {
        return CategoryEntity.builder()
                .name("Clothing")
                .description("Apparel and fashion items")
                .build();
    }

    public static CategoryEntity createTestCategoryEntityC() {
        return CategoryEntity.builder()
                .name("Books")
                .description("Printed and digital books")
                .build();
    }

    public static CategoryDto createTestCategoryDtoA() {
        return CategoryDto.builder()
                .name("Home & Kitchen")
                .description("Household appliances and kitchenware")
                .build();
    }
    public static CategoryDto createTestCategoryDtoB() {
        return CategoryDto.builder()
                .name("Books")
                .description("Digital books")
                .build();
    }


}
