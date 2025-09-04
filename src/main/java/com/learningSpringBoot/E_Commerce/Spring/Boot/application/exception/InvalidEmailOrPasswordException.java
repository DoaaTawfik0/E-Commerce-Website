package com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidEmailOrPasswordException extends RuntimeException {
    public InvalidEmailOrPasswordException() {
        super("Invalid Email or Password exception...");
    }
}
