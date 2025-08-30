package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartItemRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.CategoryRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.ProductRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CartControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testAddProductToCart_Returns200() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product = productRepository.save(TestDataUtil.createTestProductEntityA(category));

        CartItemRequestDto requestDto = TestDataUtil.createTestCartItemRequestDto();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(product.getProductId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(requestDto.getQuantity()));
    }

    @Test
    public void testAddProductToCart_UpdatesExistingItemQuantity() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product = productRepository.save(TestDataUtil.createTestProductEntityA(category));

        CartItemRequestDto firstRequest = CartItemRequestDto.builder().quantity(2).build();
        CartItemRequestDto secondRequest = CartItemRequestDto.builder().quantity(3).build();

        // Add product first time
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Add same product again - should update quantity
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(5)); // 2 + 3 = 5
    }

    @Test
    public void testGetCartItems_ReturnsCartWithItems() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product1 = productRepository.save(TestDataUtil.createTestProductEntityA(category));
        ProductEntity product2 = productRepository.save(TestDataUtil.createTestProductEntityB(category));

        // Add products to cart
        CartItemRequestDto requestDto = TestDataUtil.createTestCartItemRequestDto();
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product1.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product2.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // When & Then - Get cart items
        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(user.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").isNumber());
    }

    @Test
    public void testGetCartItems_ReturnsEmptyCartForNewUser() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(user.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems.length()").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").value(0.0));
    }

    @Test
    public void testDeleteProductFromCart_ReturnsUpdatedCart() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product1 = productRepository.save(TestDataUtil.createTestProductEntityA(category));
        ProductEntity product2 = productRepository.save(TestDataUtil.createTestProductEntityB(category));

        // Add two products to cart
        CartItemRequestDto requestDto = TestDataUtil.createTestCartItemRequestDto();
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product1.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product2.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // When & Then - Remove one product
        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/{userId}/remove/{productId}", user.getUserId(), product1.getProductId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(user.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems[0].productId").value(product2.getProductId()));
    }

    @Test
    public void testDeleteProductFromCart_NonExistentProductReturnsError() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());

        // When & Then - Try to remove product that was never added
        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/{userId}/remove/{productId}", user.getUserId(), 999))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    public void testCartTotalAmount_CalculatedCorrectly() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product1 = productRepository.save(TestDataUtil.createTestProductEntityA(category)); // $1299.99
        ProductEntity product2 = productRepository.save(TestDataUtil.createTestProductEntityC(category)); // $29.99

        // Add products with different quantities
        CartItemRequestDto request1 = CartItemRequestDto.builder().quantity(1).build();
        CartItemRequestDto request2 = CartItemRequestDto.builder().quantity(2).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product1.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product2.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        // When & Then - Check total amount calculation
        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").value(1299.99 + (29.99 * 2))); // product1 + (product2 * 2)
    }

    @Test
    public void testAddProductToCart_OutOfStockReturnsError() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());

        // Create product with very low stock
        ProductEntity lowStockProduct = TestDataUtil.createTestProductEntityA(category);
        lowStockProduct.setStockQuantity(1);
        ProductEntity savedProduct = productRepository.save(lowStockProduct);

        CartItemRequestDto requestDto = CartItemRequestDto.builder().quantity(5).build(); // Request more than available

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), savedProduct.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(
                        "Product " + savedProduct.getName() + " is out of STOCK."));
    }

    @Test
    public void testCartTimestamps_UpdatedOnModifications() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product = productRepository.save(TestDataUtil.createTestProductEntityA(category));

        // Get initial cart state
        String initialCartJson = mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user.getUserId()))
                .andReturn().getResponse().getContentAsString();
        CartResponseDto initialCart = objectMapper.readValue(initialCartJson, CartResponseDto.class);

        // Add product to cart
        CartItemRequestDto requestDto = TestDataUtil.createTestCartItemRequestDto();
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Get updated cart state
        String updatedCartJson = mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user.getUserId()))
                .andReturn().getResponse().getContentAsString();
        CartResponseDto updatedCart = objectMapper.readValue(updatedCartJson, CartResponseDto.class);

        // Verify timestamp was updated
        assertThat(updatedCart.getUpdatedAt()).isAfter(initialCart.getUpdatedAt());
    }

    @Test
    public void testMultipleUsers_CartsAreIndependent() throws Exception {
        // Given
        UserEntity user1 = userRepository.save(TestDataUtil.createTestUserEntityA());
        UserEntity user2 = userRepository.save(TestDataUtil.createTestUserEntityB());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product = productRepository.save(TestDataUtil.createTestProductEntityA(category));

        // Add product to user1's cart
        CartItemRequestDto requestDto = TestDataUtil.createTestCartItemRequestDto();
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user1.getUserId(), product.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Verify user1 has the product
        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user1.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems.length()").value(1));

        // Verify user2 has empty cart
        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user2.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems.length()").value(0));
    }

    @Test
    public void testClearCart_RemovesAllItems() throws Exception {
        // Given
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product1 = productRepository.save(TestDataUtil.createTestProductEntityA(category));
        ProductEntity product2 = productRepository.save(TestDataUtil.createTestProductEntityB(category));

        // Add multiple products
        CartItemRequestDto requestDto = TestDataUtil.createTestCartItemRequestDto();
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product1.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product2.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Verify cart has items
        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems.length()").value(2));

        // Clear cart (you might need to add a clear endpoint to your controller)
        // For now, we'll test by removing items individually
        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/{userId}/remove/{productId}", user.getUserId(), product1.getProductId()));
        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/{userId}/remove/{productId}", user.getUserId(), product2.getProductId()));

        // Verify cart is empty
        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", user.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems.length()").value(0));
    }
}