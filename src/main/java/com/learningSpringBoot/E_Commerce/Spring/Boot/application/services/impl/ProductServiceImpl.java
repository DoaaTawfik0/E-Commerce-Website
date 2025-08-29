package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.ProductDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.NotFoundException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.ProductRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.CategoryService;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@AllArgsConstructor
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final Mapper<ProductEntity, ProductDto> mapper;

    @Override
    public ProductEntity saveProduct(ProductEntity productEntity) {
        ProductDto productDto = mapper.mapTo(productEntity);
        int categoryId = productDto.getCategoryId();

        CategoryEntity categoryEntity = categoryService.findCategoryById(categoryId);

        productEntity = mapper.mapFrom(productDto);
        productEntity.setCategory(categoryEntity);

        productRepository.save(productEntity);

        return productEntity;
    }

    @Override
    public List<ProductEntity> findAll(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductEntity> productPage;
        if (category != null && !category.isEmpty()) {
            productPage = productRepository.findByCategory_Name(category, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.toList();
    }

    @Override
    public ProductEntity findById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with id " + id + " does not exist"));
    }

    @Override
    public void deleteById(int id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product with id " + id + " does not exist");
        }
        productRepository.deleteById(id);
    }

}