package com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(String productName) {
        super("Product " + productName + " is out of STOCK.");
    }
}
