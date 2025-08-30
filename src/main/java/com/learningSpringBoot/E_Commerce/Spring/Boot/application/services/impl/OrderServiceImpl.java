package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.OrderEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.cart.CartItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderItemEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderItemId;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.order.OrderStatus;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.ProductOutOfStockException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.OrderRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.CartService;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.OrderService;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderEntity checkout(Integer userId) {
        /* Fetch cart with its items from*/
        CartEntity cart = cartService.getCartByIdWithItems(
                cartService.createCartOrGetExisted(userService.findUserById(userId)).getCartId()
        );

        /* Check if cart has items to do checkout -> else exception will be thrown*/
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }

        /* Validate Stock quantity */
        validateStockAvailability(cart);

        /* Create order without items first */
        OrderEntity order = createOrderFromCart(cart);

        /* Save order to generate ID */
        OrderEntity savedOrder = orderRepository.save(order);

        /* Convert cart items with the saved order*/
        convertCartItemsToOrderItems(cart, savedOrder);

        deductStockFromCartItems(cart);
        /*Clear cart after making Order*/
        cartService.clearCart(userId);

        /*Change status from PENDING to PROCESSING*/
        savedOrder.setStatus(OrderStatus.PROCESSING);

        // Return the order with items
        return orderRepository.save(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> getUserOrders(Integer userId) {
        return orderRepository.findByUser_UserIdOrderByOrderDateDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderEntity getOrderById(Integer orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }

    /**
     * Map cartItems to orderItems and save them inside order
     */
    private void convertCartItemsToOrderItems(CartEntity cart, OrderEntity order) {
        List<OrderItemEntity> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItemEntity orderItem = OrderItemEntity.builder()
                            .order(order)
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .priceAtPurchase(cartItem.getProduct().getPrice())
                            .build();

                    // MANUALLY SET THE COMPOSITE KEY
                    orderItem.setOrderItemId(new OrderItemId(order.getOrderId(), cartItem.getProduct().getProductId()));

                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
    }

    /**
     * Validate Stock -> has required quantity
     */
    private void validateStockAvailability(CartEntity cart) {
        for (CartItemEntity cartItem : cart.getCartItems()) {
            ProductEntity product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new ProductOutOfStockException(
                        "Product " + product.getName() + " has insufficient stock. " +
                                "Available: " + product.getStockQuantity() +
                                ", Requested: " + cartItem.getQuantity()
                );
            }
        }
    }

    /**
     * Update Stock of product after checking it out for the order
     */
    private void deductStockFromCartItems(CartEntity cart) {
        for (CartItemEntity cartItem : cart.getCartItems()) {
            ProductEntity product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
        }
    }

    /**
     * Create orderEntity using user's Cart =>=>
     * Assign Order to user who owns the cart and set status to PENDING,set total amount for this order.
     */
    private OrderEntity createOrderFromCart(CartEntity cart) {
        return OrderEntity.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .totalAmount(calculateCartTotal(cart))
                .build();
    }

    /**
     * Calculate totalPrice through streaming over cartItems (quantity * productPrice)
     */
    private Double calculateCartTotal(CartEntity cart) {
        return cart.getCartItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();
    }
}