package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String name;
    private String email;
    private String password;
}