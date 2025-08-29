package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemId;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.ProductOutOfStockException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.CartItemRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.CartRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.CartService;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.ProductService;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    @Override
    @Transactional
    public CartEntity addProductToCart(Integer userId, Integer productId, Integer quantity) {

        /* Find user from DB if exists */
        UserEntity user = userService.findUserById(userId);

        /* Find cart by userId if exists -> if not a cart will be created for this user */
        CartEntity cart = createCartOrGetExisted(user);

        /* Find product from DB if exists*/
        ProductEntity product = productService.findById(productId);

        /* Check if Product exists already in Cart*/
        Optional<CartItemEntity> existingItemOpt = getCartItemEntity(productId, cart);

        /* If Product exists in cart -> quantity && total price will be updated*/
        if (existingItemOpt.isPresent()) {
            updateExistingCartItem(quantity, existingItemOpt.get());
        } else {
            saveNewCartItem(quantity, cart, product);
        }

        /* Update productQuantity*/
        checkAndUpdateStockQuantity(quantity, product);
        /* Save cart into DB*/
        cartRepository.save(cart);

        /* Reload cart with items */
        return getCartByIdWithItems(cart.getCartId());
    }

    @Override
    public CartEntity createCartOrGetExisted(UserEntity user) {
        return cartRepository.findByUser_userId(user.getUserId())
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CartEntity getCartByIdWithItems(Integer cartId) {
        return cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id " + cartId));
    }

    @Override
    @Transactional
    public CartEntity deleteProductFromCart(Integer userId, Integer productId) {
        /* Find user */
        UserEntity user = userService.findUserById(userId);

        /* Find cart by user */
        CartEntity cart = createCartOrGetExisted(user);

        /* Find product in cart */
        Optional<CartItemEntity> cartItemOpt = findCartItemByProductId(cart, productId);

        if (cartItemOpt.isEmpty()) {
            // Product exists in DB but not in this cart
            throw new IllegalStateException(
                    "Product with id " + productId + " is not in user " + userId + "'s cart."
            );
        }

        CartItemEntity cartItem = cartItemOpt.get();
        int quantity = cartItem.getQuantity();

        // Delete cartItem
        cartItemRepository.delete(cartItem);

        // Restore stock
        ProductEntity product = productService.findById(productId);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productService.saveProduct(product);

        // Update cart timestamp
        cartRepository.save(cart);

        return getCartByIdWithItems(cart.getCartId());
    }


    public Optional<CartItemEntity> findCartItemByProductId(CartEntity cart, Integer productId) {
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();
    }

    private void checkAndUpdateStockQuantity(Integer quantity, ProductEntity product) {
        if (product.getStockQuantity() < quantity) {
            throw new ProductOutOfStockException(product.getName());
        } else {
            product.setStockQuantity(product.getStockQuantity() - quantity);
        }
    }

    private void saveNewCartItem(Integer quantity, CartEntity cart, ProductEntity product) {
        CartItemEntity newItem = CartItemEntity.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .subtotal(quantity * product.getPrice())
                .cartItemId(new CartItemId(cart.getCartId(), product.getProductId())) // set composite key
                .build();
        /* Add newItem to cartItemList*/
        cart.getCartItems().add(newItem);

        cartItemRepository.save(newItem);
    }

    private void updateExistingCartItem(Integer quantity, CartItemEntity existingItem) {

        /* (oldQuantity + newQuantity) */
        existingItem.setQuantity(existingItem.getQuantity() + quantity);

        /* (updatedQuantity * productPrice) */
        existingItem.setSubtotal(existingItem.getQuantity() * existingItem.getProduct().getPrice());

        cartItemRepository.save(existingItem);
    }

    private Optional<CartItemEntity> getCartItemEntity(Integer productId, CartEntity cart) {
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();
    }

}
