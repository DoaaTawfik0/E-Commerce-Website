package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.ProductDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest
public class ProductControllerIntegrationTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductControllerIntegrationTests(MockMvc mockMvc,
                                             ObjectMapper objectMapper,
                                             CategoryRepository categoryRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.categoryRepository = categoryRepository;
    }

    @Test
    public void testCreateProduct_Returns201() throws Exception {
        // First create a category
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductDto testProductDto = createTestProductDtoA(category.getCategoryId());

        String productJson = objectMapper.writeValueAsString(testProductDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testCreateProduct_ReturnsSavedProduct() throws Exception {
        // First create a category
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductDto testProductDto = createTestProductDtoA(category.getCategoryId());

        String productJson = objectMapper.writeValueAsString(testProductDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.productId").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value("Laptop")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.description").value("High-performance gaming laptop")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.price").value(1299.99)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.categoryId").value(category.getCategoryId())
        );
    }

    @Test
    public void testFindAllProducts_ReturnsRightProducts() throws Exception {
        // Create categories first
        CategoryEntity categoryA = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        CategoryEntity categoryB = categoryRepository.save(TestDataUtil.createTestCategoryEntityB());

        ProductDto productDtoA = createTestProductDtoA(categoryA.getCategoryId());
        ProductDto productDtoB = createTestProductDtoB(categoryB.getCategoryId());

        // Save first product
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDtoA)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Save second product
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDtoB)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Laptop"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Smartphone"));
    }

    @Test
    public void testFindAllProducts_WithCategoryFilter() throws Exception {
        // Create categories
        CategoryEntity electronics = categoryRepository.save(TestDataUtil.createTestCategoryEntityD());
        CategoryEntity clothing = categoryRepository.save(TestDataUtil.createTestCategoryEntityB());

        ProductDto laptopDto = createTestProductDtoA(electronics.getCategoryId());
        ProductDto smartphoneDto = createTestProductDtoB(electronics.getCategoryId());
        ProductDto shirtDto = createTestProductDtoC(clothing.getCategoryId());

        // Save products
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(laptopDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(smartphoneDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shirtDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Filter by electronics category
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("category", "Electronics")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Laptop"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Smartphone"));
    }

    @Test
    public void testFindProductById_ReturnsCorrectProduct() throws Exception {
        // Create category and product
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductDto productDto = createTestProductDtoA(category.getCategoryId());

        String createResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto))
        ).andReturn().getResponse().getContentAsString();

        ProductDto createdProduct = objectMapper.readValue(createResponse, ProductDto.class);
        int productId = createdProduct.getProductId();

        // Get product by ID
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Laptop"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(1299.99));
    }

    @Test
    public void testDeleteProductById_ReturnsSuccessMessage() throws Exception {
        // Create category and product
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductDto productDto = createTestProductDtoA(category.getCategoryId());

        String createResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto))
        ).andReturn().getResponse().getContentAsString();

        ProductDto createdProduct = objectMapper.readValue(createResponse, ProductDto.class);
        int productId = createdProduct.getProductId();

        // Delete product
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Product with id " + productId + " is deleted successfully.."));

        // Verify product is deleted
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testFindAllProducts_WithPagination() throws Exception {
        // Create category
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());

        // Create multiple products
        for (int i = 1; i <= 15; i++) {
            ProductDto productDto = ProductDto.builder()
                    .name("Product " + i)
                    .description("Test product " + i)
                    .price(100.00 + i)
                    .categoryId(category.getCategoryId())
                    .stockQuantity(10)
                    .imageUrl("https://example.com/product" + i + ".jpg")
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productDto)))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        // Test pagination - first page with 10 items
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10));

        // Test pagination - second page with remaining 5 items
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(5));
    }

    // Helper methods to create ProductDto with category ID
    private ProductDto createTestProductDtoA(int categoryId) {
        return ProductDto.builder()
                .name("Laptop")
                .description("High-performance gaming laptop")
                .price(1299.99)
                .categoryId(categoryId)
                .stockQuantity(50)
                .imageUrl("https://example.com/laptop.jpg")
                .build();
    }

    private ProductDto createTestProductDtoB(int categoryId) {
        return ProductDto.builder()
                .name("Smartphone")
                .description("Latest smartphone with advanced features")
                .price(899.99)
                .categoryId(categoryId)
                .stockQuantity(100)
                .imageUrl("https://example.com/smartphone.jpg")
                .build();
    }

    private ProductDto createTestProductDtoC(int categoryId) {
        return ProductDto.builder()
                .name("T-Shirt")
                .description("Cotton t-shirt with premium quality")
                .price(29.99)
                .categoryId(categoryId)
                .stockQuantity(200)
                .imageUrl("https://example.com/tshirt.jpg")
                .build();
    }
}