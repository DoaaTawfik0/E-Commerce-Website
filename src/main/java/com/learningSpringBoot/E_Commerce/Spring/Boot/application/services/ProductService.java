package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductDto saveProduct(ProductDto productDto);

    List<ProductDto> findAll(int page, int size, String category);

    ProductDto findById(int id);

    void deleteById(int id);

}
