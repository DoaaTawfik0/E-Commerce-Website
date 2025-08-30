package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services;


import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;

public interface CartService {
    CartEntity addProductToCart(Integer userId, Integer productId, Integer quantity);

    CartEntity getCartByIdWithItems(Integer cartId);

    public CartEntity createCartOrGetExisted(UserEntity user);

    CartEntity deleteProductFromCart(Integer userId, Integer productId);

    void clearCart(Integer userId);

    void clearCartItems(Integer cartId);
}
