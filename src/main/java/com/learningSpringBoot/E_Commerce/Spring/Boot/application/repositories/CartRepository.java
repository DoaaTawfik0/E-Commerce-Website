package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Integer> {
    Optional<CartEntity> findByUser_userId(Integer id);

    @Query("SELECT c FROM CartEntity c LEFT JOIN FETCH c.cartItems WHERE c.cartId = :id")
    Optional<CartEntity> findByIdWithItems(@Param("id") Integer id);
}
