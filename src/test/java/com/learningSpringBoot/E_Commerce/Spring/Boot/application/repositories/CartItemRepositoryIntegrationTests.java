package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CartItemRepositoryIntegrationTests {

    @Autowired
    private CartItemRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private CartEntity cart;
    private CategoryEntity category;
    private ProductEntity product;
    private CartItemEntity cartItem;

    @BeforeEach
    @Transactional
    public void setup() {
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());
        cart = cartRepository.save(TestDataUtil.createTestCartEntityA(user));
        category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        product = productRepository.save(TestDataUtil.createTestProductEntityA(category));
        cartItem = underTest.save(TestDataUtil.createTestCartItemEntityA(cart, product, 2));
    }

    @Test
    @Transactional
    void testThatCartItemCanBeCreatedAndRecalled() {
        //Act
        Optional<CartItemEntity> result = underTest.findById(cartItem.getCartItemId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getCartItemId()).isEqualTo(cartItem.getCartItemId());
        assertThat(result.get().getQuantity()).isEqualTo(2);
        assertThat(result.get().getSubtotal()).isEqualTo(2 * product.getPrice());

        // Logging
        result.ifPresent(item -> log.info("result CartItem: CartID={}, ProductID={}, Quantity={}, Subtotal={}",
                item.getCartItemId().getCartId(),
                item.getCartItemId().getProductId(),
                item.getQuantity(),
                item.getSubtotal()
        ));
    }

    @Test
    @Transactional
    public void testThatCartItemCanBeUpdated() {
        // Act - Update quantity and subtotal
        cartItem.setQuantity(5);
        cartItem.setSubtotal(5 * product.getPrice());
        CartItemEntity updatedCartItem = underTest.save(cartItem);

        // Assert
        Optional<CartItemEntity> result = underTest.findById(updatedCartItem.getCartItemId());
        assertThat(result).isPresent();
        assertThat(result.get().getQuantity()).isEqualTo(5);
        assertThat(result.get().getSubtotal()).isEqualTo(5 * product.getPrice());
    }

    @Test
    @Transactional
    public void testThatCartItemCanBeDeleted() {
        // Act - Delete the cart item
        underTest.deleteById(cartItem.getCartItemId());

        // Assert
        Optional<CartItemEntity> result = underTest.findById(cartItem.getCartItemId());
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    public void testThatMultipleCartItemsCanBeCreated() {
        // Arrange - Create another product and cart item
        ProductEntity product2 = productRepository.save(TestDataUtil.createTestProductEntityB(category));
        CartItemEntity cartItem2 = underTest.save(TestDataUtil.createTestCartItemEntityB(cart, product2, 1));

        // Act - Count all cart items
        long count = underTest.count();

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Transactional
    public void testThatCartItemCanBeFoundByCompositeKey() {
        // Act - Find by the composite key
        Optional<CartItemEntity> result = underTest.findById(cartItem.getCartItemId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getCartItemId().getCartId()).isEqualTo(cart.getCartId());
        assertThat(result.get().getCartItemId().getProductId()).isEqualTo(product.getProductId());
    }

    @Test
    @Transactional
    public void testThatNonExistentCartItemReturnsEmpty() {
        // Arrange - Create a non-existent composite key
        CartItemEntity nonExistentCartItem = TestDataUtil.createTestCartItemEntityA(cart,
                TestDataUtil.createTestProductEntityC(category), 999);

        // Act
        Optional<CartItemEntity> result = underTest.findById(nonExistentCartItem.getCartItemId());

        // Assert
        assertThat(result).isEmpty();
    }
}