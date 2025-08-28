package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cart_items")
public class CartItemEntity {

    @EmbeddedId
    private CartItemId cartItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity;


    // Owning side of CartItem <-> Cart relation
    @ManyToOne
    @MapsId("cartId") // FK part of composite key
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    // Owning side of CartItem <-> Product relation
    @ManyToOne
    @MapsId("productId") // FK part of composite key
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
