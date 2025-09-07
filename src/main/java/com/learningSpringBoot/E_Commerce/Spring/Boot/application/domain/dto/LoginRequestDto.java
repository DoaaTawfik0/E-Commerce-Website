package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {
    private String email;
    private String password;
}
