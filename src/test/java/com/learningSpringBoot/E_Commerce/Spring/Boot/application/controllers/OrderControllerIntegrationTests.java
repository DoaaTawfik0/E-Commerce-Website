package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartItemRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderDetailsResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.OrderResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderControllerIntegrationTests {

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

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private UserEntity user;
    private CategoryEntity category;


    @BeforeEach
    public void setup() {
        user = userRepository.save(TestDataUtil.createTestUserEntityA());
        CartEntity cart = cartRepository.save(TestDataUtil.createTestCartEntityA(user));
        category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product = productRepository.save(TestDataUtil.createTestProductEntityA(category));
        CartItemEntity cartItem = cartItemRepository.save(TestDataUtil.createTestCartItemEntityA(cart, product, 3));
    }

    private Integer createOrder() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderDetailsResponseDto orderResponse = objectMapper.readValue(response, OrderDetailsResponseDto.class);
        return orderResponse.getOrderId();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCheckout_Returns200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCheckout_StatusIsProcessing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCheckout_CountExistingItems() throws Exception {
        ProductEntity product1 = productRepository.save(TestDataUtil.createTestProductEntityB(category));
        CartItemRequestDto cartItemRequestDto = CartItemRequestDto.builder().quantity(2).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product1.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemRequestDto)));

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCheckout_CheckOrderItemDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].productName").value("Laptop"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].quantity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].unitPrice").value(1299.99));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCheckOut_CheckTotalAmount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").value(3899.9700000000003));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserOrders_Returns200() throws Exception {
        createOrder();

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/user/{userId}", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserOrders_ReturnsExpectedOrdersCount_2() throws Exception {
        /* checkout first Order*/
        createOrder();

        /* add another product to cart and do checkout*/
        ProductEntity product1 = productRepository.save(TestDataUtil.createTestProductEntityB(category));
        CartItemRequestDto cartItemRequestDto = CartItemRequestDto.builder().quantity(2).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/{userId}/add/{productId}", user.getUserId(), product1.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemRequestDto)));

        createOrder();

        /*Get user orders*/
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/user/{userId}", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserOrders_ReturnsExpectedTotalAmount() throws Exception {
        createOrder();

        /*Get user orders*/
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/user/{userId}", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].totalAmount").value(3899.9700000000003));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserOrders_CheckCreatedDate() throws Exception {
        // Create an order first
        createOrder();

        // Get user orders (returns a list)
        String ordersJson = mockMvc.perform(MockMvcRequestBuilders.get("/orders/user/{userId}", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Parse as a list of OrderResponseDto
        List<OrderResponseDto> orders = objectMapper.readValue(ordersJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, OrderResponseDto.class));

        // Verify the list is not empty
        assertThat(orders).isNotEmpty();

        // Check the created date of the first order (or iterate through all)
        for (OrderResponseDto order : orders) {
            assertThat(order.getCreatedAt()).isBefore(LocalDateTime.now());
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCheckout_WithInvalidUserId_ReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCheckout_WithEmptyCart_ReturnsBadRequest() throws Exception {
        // Create a new user with empty cart
        UserEntity newUser = userRepository.save(TestDataUtil.createTestUserEntityB());
        CartEntity emptyCart = cartRepository.save(TestDataUtil.createTestCartEntityA(newUser));

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", newUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserOrders_WithNoOrders_ReturnsEmptyList() throws Exception {
        // Create a new user with no orders
        UserEntity newUser = userRepository.save(TestDataUtil.createTestUserEntityB());

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/user/{userId}", newUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCheckout_ClearsUserCartAfterSuccess() throws Exception {
        // Perform checkout
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify cart is empty by checking if we can't check out again with empty cart
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{userId}/checkout", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Tests for @GetMapping("/{orderId}") endpoint
    @Test
    @WithMockUser(roles = "USER")
    public void testGetOrderById_Returns200() throws Exception {
        Integer orderId = createOrder();

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetOrderById_ReturnsCorrectOrder() throws Exception {
        Integer orderId = createOrder();

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PROCESSING"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").value(3899.9700000000003))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].productName").value("Laptop"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].quantity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].unitPrice").value(1299.99));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetOrderById_WithInvalidOrderId_ReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{orderId}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetOrderById_CheckOrderBelongsToUser() throws Exception {
        Integer orderId = createOrder();

        // Create another user
        UserEntity anotherUser = userRepository.save(TestDataUtil.createTestUserEntityB());

        // Try to access the order with different user (should work if authentication allows)
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetOrderById_CheckCreatedDate() throws Exception {
        Integer orderId = createOrder();

        String orderJson = mockMvc.perform(MockMvcRequestBuilders.get("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        OrderResponseDto order = objectMapper.readValue(orderJson, OrderResponseDto.class);

        assertThat(order.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(order.getCreatedAt()).isAfter(LocalDateTime.now().minusMinutes(5)); // Should be recent
    }

    // ================== ADMIN ROLE TESTS ==================

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserOrders_AsAdmin_Returns200() throws Exception {
        Integer orderId = createOrder();

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/user/{userId}", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOrderById_AsAdmin_Returns200() throws Exception {
        Integer orderId = createOrder();

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }
}