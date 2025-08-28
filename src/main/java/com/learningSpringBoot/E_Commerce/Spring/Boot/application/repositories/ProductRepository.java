package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    Page<ProductEntity> findByCategory_Name(String category, Pageable pageable);
}
