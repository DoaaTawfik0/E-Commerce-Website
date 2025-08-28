package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.OrderEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @EmbeddedId
    private OrderItemId orderItemId;

    // Owning side of relation with (Order)
    @ManyToOne
    @MapsId("orderId") // maps composite PK orderId
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    // Owning side of relation with (Product)
    @ManyToOne
    @MapsId("productId") // maps composite PK productId
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Double priceAtPurchase;
}
