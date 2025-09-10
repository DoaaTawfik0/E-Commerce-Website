package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.CustomUserDetails;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.UpdateUserRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.UserResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    Mapper<UserEntity, UserResponseDto> userMapper;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        UserEntity user;

        if (principal instanceof CustomUserDetails customUser) {
            user = customUser.user();
        } else {
            String email = authentication.getName();
            user = userService.findUserByEmail(email);
        }

        return ResponseEntity.ok(userMapper.mapTo(user));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer userId,
                                                      @Valid @RequestBody UpdateUserRequestDto request) {
        UserEntity user = userService.updateUser(userId, request);

        return ResponseEntity.ok(userMapper.mapTo(user));
    }
}

