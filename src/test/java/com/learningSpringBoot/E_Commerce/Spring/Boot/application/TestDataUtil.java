package com.learningSpringBoot.E_Commerce.Spring.Boot.application;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.*;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.*;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemId;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderItemId;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@NoArgsConstructor
public class TestDataUtil {

    /*  Category Data Utility Test  */
    public static CategoryEntity createTestCategoryEntityA() {
        return CategoryEntity.builder()
                .name("Sports")
                .description("Sports equipment and accessories")
                .products(null)
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

    /* Product Data Utility Test */
    public static ProductEntity createTestProductEntityA(CategoryEntity category) {
        return ProductEntity.builder()
                .name("Laptop")
                .description("High-performance gaming laptop")
                .price(1299.99)
                .category(category)
                .stockQuantity(50)
                .imageUrl("https://example.com/laptop.jpg")
                .build();
    }

    public static ProductEntity createTestProductEntityB(CategoryEntity category) {
        return ProductEntity.builder()
                .name("Smartphone")
                .description("Latest smartphone with advanced features")
                .price(899.99)
                .category(category)
                .stockQuantity(100)
                .imageUrl("https://example.com/smartphone.jpg")
                .build();
    }

    public static ProductEntity createTestProductEntityC(CategoryEntity category) {
        return ProductEntity.builder()
                .name("T-Shirt")
                .description("Cotton t-shirt with premium quality")
                .price(29.99)
                .category(category)
                .stockQuantity(200)
                .imageUrl("https://example.com/tshirt.jpg")
                .build();
    }

    /*  Cart DTO Data Utility Test  */
    public static CartItemRequestDto createTestCartItemRequestDto() {
        return CartItemRequestDto.builder()
                .quantity(2)
                .build();
    }

    public static CartItemResponseDto createTestCartItemResponseDto(Integer productId) {
        return CartItemResponseDto.builder()
                .productId(productId)
                .productName("Test Product")
                .price(99.99)
                .quantity(2)
                .subtotal(199.98)
                .build();
    }

    public static CartResponseDto createTestCartResponseDto(Integer cartId, Integer userId) {
        return CartResponseDto.builder()
                .cartId(cartId)
                .userId(userId)
                .cartItems(new ArrayList<>())
                .totalAmount(0.0)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /* Cart Data Utility Test  */
    public static CartEntity createTestCartEntityA(UserEntity user) {
        return CartEntity.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
    }

    public static CartEntity createTestCartEntityB(UserEntity user) {
        return CartEntity.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
    }

    /*  CartItem Data Utility Test  */
    public static CartItemEntity createTestCartItemEntityA(CartEntity cart, ProductEntity product, int quantity) {
        return CartItemEntity.builder()
                .cartItemId(new CartItemId(cart.getCartId(), product.getProductId()))
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .subtotal(quantity * product.getPrice())
                .build();
    }

    public static CartItemEntity createTestCartItemEntityB(CartEntity cart, ProductEntity product, int quantity) {
        return CartItemEntity.builder()
                .cartItemId(new CartItemId(cart.getCartId(), product.getProductId()))
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .subtotal(quantity * product.getPrice())
                .build();
    }

    /* User Data Utility Test  */
    public static UserEntity createTestUserEntityA() {
        return UserEntity.builder()
                .name("john_doe")
                .email("john.doe@example.com")
                .passwordHash("password123")
                .role("USER")
                .build();
    }

    public static UserEntity createTestUserEntityB() {
        return UserEntity.builder()
                .name("jane_smith")
                .email("jane.smith@example.com")
                .passwordHash("password456")
                .role("USER")
                .build();
    }

    public static RegisterRequestDto createRegisterReqDtoA() {
        return RegisterRequestDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();
    }

    public static LoginRequestDto createLoginReqDtoA() {
        return LoginRequestDto.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();
    }


    public static UpdateUserRequestDto createUpdateUserReqDtoA() {
        return UpdateUserRequestDto.builder()
                .name("updated name")
                .email("updated@example.com")
                .build();
    }

    public static UpdateUserRequestDto createUpdateUserReqDtoPartialData() {
        return UpdateUserRequestDto.builder()
                .name("New Name Only")
                .build();
    }

    public static UpdateUserRequestDto createUpdateUserReqDto_WithInvalidMail() {
        return UpdateUserRequestDto.builder()
                .name("updated name")
                .email("updated Email")
                .build();
    }

    public static UserEntity createTestUserEntityC() {
        return UserEntity.builder()
                .name("bob_wilson")
                .email("bob.wilson@example.com")
                .passwordHash("password789")
                .role("ADMIN")
                .build();
    }

    /* OrderItem Data Utility Test - Enhanced */
    public static OrderItemEntity createTestOrderItemEntityA(ProductEntity product, OrderEntity order) {
        OrderItemEntity item = OrderItemEntity.builder()
                .product(product)
                .order(order)
                .quantity(2)
                .priceAtPurchase(100.10)
                .build();

        // Set the composite key manually
        if (order != null && order.getOrderId() != null) {
            item.setOrderItemId(new OrderItemId(order.getOrderId(), product.getProductId()));
        }
        return item;
    }

    public static OrderItemEntity createTestOrderItemEntityB(ProductEntity product, OrderEntity order) {
        OrderItemEntity item = OrderItemEntity.builder()
                .product(product)
                .order(order)
                .quantity(1)
                .priceAtPurchase(140.0)
                .build();

        if (order != null && order.getOrderId() != null) {
            item.setOrderItemId(new OrderItemId(order.getOrderId(), product.getProductId()));
        }
        return item;
    }
}
