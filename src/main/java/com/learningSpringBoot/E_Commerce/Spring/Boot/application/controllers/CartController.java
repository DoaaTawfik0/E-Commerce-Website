package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartItemRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartItemResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CartResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.CartService;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cart")
@AllArgsConstructor
public class CartController {

    private final UserService userService;
    private final CartService cartService;
    private final Mapper<CartEntity, CartResponseDto> cartMapper;
    private final Mapper<CartItemEntity, CartItemResponseDto> cartItemMapper;


    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<CartItemResponseDto> addProductToCart(@PathVariable Integer userId,
                                                                @PathVariable Integer productId,
                                                                @RequestBody CartItemRequestDto cartItemRequestDto) {

        // Add product to cart
        CartEntity updatedCart = cartService.addProductToCart(userId, productId, cartItemRequestDto.getQuantity());

        Optional<CartItemEntity> cartItemOpt = updatedCart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        // Map to DTO
        CartItemResponseDto responseDto = null;
        if (cartItemOpt.isPresent()) {
            responseDto = cartItemMapper.mapTo(cartItemOpt.get());
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> getCartItems(@PathVariable Integer userId) {
        /* Get user from DB by ID if exists*/
        UserEntity user = userService.findUserById(userId);

        CartEntity cart = cartService.createCartOrGetExisted(user);

        CartResponseDto cartResponse = cartMapper.mapTo(cartService.getCartByIdWithItems(cart.getCartId()));

        return new ResponseEntity<>(cartMapper.mapTo(cart), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<CartResponseDto> deleteProductFromCart(@PathVariable Integer userId,
                                                                 @PathVariable Integer productId) {
        // Add product to cart

        CartEntity updatedCart = cartService.deleteProductFromCart(userId, productId);
        ;

        // Map to DTO
        CartResponseDto responseDto = cartMapper.mapTo(updatedCart);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
