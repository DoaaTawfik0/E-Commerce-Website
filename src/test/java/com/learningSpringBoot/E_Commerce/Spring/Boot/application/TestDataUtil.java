package com.learningSpringBoot.E_Commerce.Spring.Boot.application;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CategoryDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
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

    public static CategoryEntity createTestCategoryEntityD() {
        return CategoryEntity.builder()
                .name("Electronics")
                .description("Electronic devices and gadgets")
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

    // Product Entity methods
    public static ProductEntity createTestProductEntityA(CategoryEntity category) {
        return ProductEntity.builder()
                .name("Laptop")
                .description("High-performance gaming laptop")
                .price(1299.99)
                .categoryEntity(category)
                .stockQuantity(50)
                .imageUrl("https://example.com/laptop.jpg")
                .build();
    }

    public static ProductEntity createTestProductEntityB(CategoryEntity category) {
        return ProductEntity.builder()
                .name("Smartphone")
                .description("Latest smartphone with advanced features")
                .price(899.99)
                .categoryEntity(category)
                .stockQuantity(100)
                .imageUrl("https://example.com/smartphone.jpg")
                .build();
    }

    public static ProductEntity createTestProductEntityC(CategoryEntity category) {
        return ProductEntity.builder()
                .name("T-Shirt")
                .description("Cotton t-shirt with premium quality")
                .price(29.99)
                .categoryEntity(category)
                .stockQuantity(200)
                .imageUrl("https://example.com/tshirt.jpg")
                .build();
    }


}
