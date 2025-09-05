package com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("Can't checkout:: Cart is empty..");
    }
}
