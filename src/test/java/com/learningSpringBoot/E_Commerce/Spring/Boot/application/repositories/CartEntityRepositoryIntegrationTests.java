package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
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
public class CartEntityRepositoryIntegrationTests {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @Transactional
    void testThatCartCanBeCreatedAndRecalled() {
        // Create and save user
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());

        // Create cart
        CartEntity cart = TestDataUtil.createTestCartEntityA(user);
        CartEntity savedCart = cartRepository.save(cart);

        Optional<CartEntity> result = cartRepository.findById(savedCart.getCartId());

        log.info("Saved Cart: ID={}, User={}, Items={}",
                savedCart.getCartId(),
                savedCart.getUser().getName(),
                savedCart.getCartItems().size());

        result.ifPresent(foundCart -> log.info("Found Cart: ID={}, User={}, Items={}",
                foundCart.getCartId(),
                foundCart.getUser().getName(),
                foundCart.getCartItems().size()));

        assertThat(result).isPresent();
        assertThat(result.get().getCartId()).isEqualTo(savedCart.getCartId());
        assertThat(result.get().getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(result.get().getCartItems()).isEmpty();
    }

    @Test
    @Transactional
    void testThatCartCanBeFoundByUserId() {
        // Create and save user
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());

        // Create and save cart
        CartEntity cart = TestDataUtil.createTestCartEntityA(user);
        cartRepository.save(cart);

        // Find cart by user ID
        Optional<CartEntity> result = cartRepository.findByUser_userId(user.getUserId());

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(result.get().getCartItems()).isEmpty();
    }

    @Test
    @Transactional
    void testThatCartWithItemsCanBeFoundByIdWithItems() {
        // Create and save user
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());

        // Create and save category and product
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product = productRepository.save(TestDataUtil.createTestProductEntityA(category));

        // Create and save cart
        CartEntity cart = TestDataUtil.createTestCartEntityA(user);
        CartEntity savedCart = cartRepository.save(cart);

        // Create and save cart item
        CartItemEntity cartItem = TestDataUtil.createTestCartItemEntityA(savedCart, product, 2);
        cartItemRepository.save(cartItem);

        // Add cart item to cart
        savedCart.getCartItems().add(cartItem);
        cartRepository.save(savedCart);

        // Find cart with items using custom query
        Optional<CartEntity> result = cartRepository.findByIdWithItems(savedCart.getCartId());

        assertThat(result).isPresent();
        assertThat(result.get().getCartItems()).hasSize(1);
        assertThat(result.get().getCartItems().getFirst().getProduct().getProductId())
                .isEqualTo(product.getProductId());
        assertThat(result.get().getCartItems().getFirst().getQuantity()).isEqualTo(2);
    }

    @Test
    @Transactional
    void testThatMultipleCartsCanBeCreatedAndRecalled() {
        // Create and save users
        UserEntity userA = userRepository.save(TestDataUtil.createTestUserEntityA());
        UserEntity userB = userRepository.save(TestDataUtil.createTestUserEntityB());
        UserEntity userC = userRepository.save(TestDataUtil.createTestUserEntityC());

        // Create and save carts
        CartEntity cartA = cartRepository.save(TestDataUtil.createTestCartEntityA(userA));
        CartEntity cartB = cartRepository.save(TestDataUtil.createTestCartEntityB(userB));
        CartEntity cartC = cartRepository.save(TestDataUtil.createTestCartEntityA(userC));

        // Find all carts
        List<CartEntity> result = cartRepository.findAll();

        assertThat(result)
                .hasSize(3)
                .extracting(CartEntity::getCartId)
                .containsExactlyInAnyOrder(cartA.getCartId(), cartB.getCartId(), cartC.getCartId());
    }

    @Test
    @Transactional
    void testThatCartCanBeDeleted() {
        // Create and save user
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());

        // Create and save cart
        CartEntity cart = cartRepository.save(TestDataUtil.createTestCartEntityA(user));

        // Delete cart
        cartRepository.deleteById(cart.getCartId());

        Optional<CartEntity> result = cartRepository.findById(cart.getCartId());
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void testThatCartWithItemsCanBeDeletedCascading() {
        // Create and save user
        UserEntity user = userRepository.save(TestDataUtil.createTestUserEntityA());

        // Create and save category and product
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity product = productRepository.save(TestDataUtil.createTestProductEntityA(category));

        // Create and save cart
        CartEntity cart = cartRepository.save(TestDataUtil.createTestCartEntityA(user));

        // Create and save cart item
        CartItemEntity cartItem = TestDataUtil.createTestCartItemEntityA(cart, product, 2);
        cartItemRepository.save(cartItem);

        // Add cart item to cart
        cart.getCartItems().add(cartItem);
        cartRepository.save(cart);

        // Verify cart item exists
        assertThat(cartItemRepository.findById(cartItem.getCartItemId())).isPresent();

        // Delete cart (should cascade delete cart items due to orphanRemoval = true)
        cartRepository.deleteById(cart.getCartId());

        // Verify cart is deleted
        assertThat(cartRepository.findById(cart.getCartId())).isEmpty();

        // Verify cart item is also deleted (cascading)
        assertThat(cartItemRepository.findById(cartItem.getCartItemId())).isEmpty();
    }

    @Test
    @Transactional
    void testThatNonExistentCartReturnsEmptyOptional() {
        Optional<CartEntity> result = cartRepository.findById(9999);
        assertThat(result).isEmpty();

        Optional<CartEntity> resultByUser = cartRepository.findByUser_userId(9999);
        assertThat(resultByUser).isEmpty();
    }

}