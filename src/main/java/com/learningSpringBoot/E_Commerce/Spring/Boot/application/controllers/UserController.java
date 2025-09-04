package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.CustomUserDetails;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.UpdateUserRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.UserResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.NotFoundException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    Mapper<UserEntity, UserResponseDto> userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserResponseDto responseDto = userMapper.mapTo(userDetails.user());
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer userId, @RequestBody UpdateUserRequestDto request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return ResponseEntity.ok(userMapper.mapTo(userRepository.save(user)));
    }
}

