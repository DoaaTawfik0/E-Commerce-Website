package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;


import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.ProductDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final Mapper<ProductEntity, ProductDto> mapper;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        ProductEntity savedProduct = productService.saveProduct(mapper.mapFrom(productDto));
        return new ResponseEntity<>(mapper.mapTo(savedProduct), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<ProductDto>> findAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {

        List<ProductDto> productDtos = new ArrayList<>();
        for (ProductEntity product : productService.findAll(page, size, category)) {
            productDtos.add(mapper.mapTo(product));
        }

        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable int id) {
        ProductDto productDto = mapper.mapTo(productService.findById(id));
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProductById(@PathVariable int id) {
        productService.deleteById(id);

        return new ResponseEntity<>("Product with id " + id + " is deleted successfully..", HttpStatus.OK);
    }

}
