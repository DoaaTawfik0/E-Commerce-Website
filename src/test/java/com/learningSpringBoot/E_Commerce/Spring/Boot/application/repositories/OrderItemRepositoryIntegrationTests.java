package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.OrderEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderItemId;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderItemRepositoryIntegrationTests {

    @Autowired
    private OrderItemRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private UserEntity user;
    private CategoryEntity category;
    private ProductEntity product;
    private OrderItemEntity orderItem;
    private OrderItemId orderItemId;

    @BeforeEach
    @Transactional
    public void setup() {
        // Setup test data for each test
        user = userRepository.save(TestDataUtil.createTestUserEntityA());
        category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        product = productRepository.save(TestDataUtil.createTestProductEntityA(category));

        // Create a minimal order first
        OrderEntity order = OrderEntity.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(0.0)
                .build();
        OrderEntity savedOrder = orderRepository.save(order);

        // Create order item with the saved order
        orderItem = TestDataUtil.createTestOrderItemEntityA(product, savedOrder);
        OrderItemEntity savedOrderItem = underTest.save(orderItem);
        orderItemId = savedOrderItem.getOrderItemId();
    }

    @Test
    @Transactional
    void testThatOrderItemCanBeCreatedAndRecalled() {
        // Act - Retrieve the order item using composite key
        Optional<OrderItemEntity> result = underTest.findById(orderItemId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getProduct().getProductId()).isEqualTo(product.getProductId());
        assertThat(result.get().getQuantity()).isEqualTo(2);
        assertThat(result.get().getPriceAtPurchase()).isEqualTo(100.10);

        // Logging
        result.ifPresent(item -> log.info("OrderItem found: OrderID={}, ProductID={}, Quantity={}, Price={}",
                item.getOrderItemId().getOrderId(),
                item.getOrderItemId().getProductId(),
                item.getQuantity(),
                item.getPriceAtPurchase()
        ));
    }

    @Test
    @Transactional
    public void testThatOrderItemCanBeUpdated() {
        // Act - Update quantity and price
        orderItem.setQuantity(5);
        orderItem.setPriceAtPurchase(99.99);
        OrderItemEntity updatedOrderItem = underTest.save(orderItem);

        // Assert
        Optional<OrderItemEntity> result = underTest.findById(updatedOrderItem.getOrderItemId());
        assertThat(result).isPresent();
        assertThat(result.get().getQuantity()).isEqualTo(5);
        assertThat(result.get().getPriceAtPurchase()).isEqualTo(99.99);
    }

    @Test
    @Transactional
    public void testThatOrderItemCanBeDeleted() {
        // Act - Delete the order item using composite key
        underTest.deleteById(orderItemId);

        // Assert
        Optional<OrderItemEntity> result = underTest.findById(orderItemId);
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    public void testThatMultipleOrderItemsCanBeCreated() {
        // Arrange - Create another product and order item
        ProductEntity product2 = productRepository.save(TestDataUtil.createTestProductEntityB(category));
        OrderItemEntity orderItem2 = TestDataUtil.createTestOrderItemEntityB(product2, orderItem.getOrder());
        underTest.save(orderItem2);

        // Act - Find all order items
        List<OrderItemEntity> result = underTest.findAll();

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    @Transactional
    public void testThatOrderItemCompositeKeyWorksCorrectly() {
        // Act - Retrieve using composite key
        Optional<OrderItemEntity> result = underTest.findById(orderItemId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getOrderItemId().getOrderId()).isEqualTo(orderItemId.getOrderId());
        assertThat(result.get().getOrderItemId().getProductId()).isEqualTo(orderItemId.getProductId());
    }

    @Test
    @Transactional
    public void testThatOrderItemHasCorrectProductAssociation() {
        // Act
        Optional<OrderItemEntity> result = underTest.findById(orderItemId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getProduct().getProductId()).isEqualTo(product.getProductId());
        assertThat(result.get().getProduct().getName()).isEqualTo("Laptop");
    }

    @Test
    @Transactional
    public void testThatOrderItemHasCorrectOrderAssociation() {
        // Act
        Optional<OrderItemEntity> result = underTest.findById(orderItemId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getOrder()).isNotNull();
        assertThat(result.get().getOrder().getUser().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @Transactional
    public void testThatOrderItemSubtotalIsCalculatedCorrectly() {
        // Act
        Optional<OrderItemEntity> result = underTest.findById(orderItemId);

        // Assert
        assertThat(result).isPresent();
        double expectedSubtotal = result.get().getQuantity() * result.get().getPriceAtPurchase();
        assertThat(expectedSubtotal).isEqualTo(200.20); // 2 * 100.10
    }

    @Test
    @Transactional
    public void testThatNonExistentOrderItemReturnsEmpty() {
        // Arrange - Create a non-existent composite key
        OrderItemId nonExistentId = new OrderItemId(9999, 9999);

        // Act
        Optional<OrderItemEntity> result = underTest.findById(nonExistentId);

        // Assert
        assertThat(result).isEmpty();
    }
}