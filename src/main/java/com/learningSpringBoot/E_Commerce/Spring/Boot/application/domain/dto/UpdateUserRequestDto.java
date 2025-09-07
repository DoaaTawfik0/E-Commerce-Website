package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequestDto {
    @Size(min = 2, max = 50, message = "Name must be between 2–50 characters")
    private String name;
    @Email(message = "Invalid email format")
    private String email;
}
