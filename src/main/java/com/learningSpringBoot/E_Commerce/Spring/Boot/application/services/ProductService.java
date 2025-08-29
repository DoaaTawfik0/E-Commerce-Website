package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;

import java.util.List;

public interface ProductService {
    ProductEntity saveProduct(ProductEntity product);

    List<ProductEntity> findAll(int page, int size, String category);

    ProductEntity findById(int id);

    void deleteById(int id);

}
