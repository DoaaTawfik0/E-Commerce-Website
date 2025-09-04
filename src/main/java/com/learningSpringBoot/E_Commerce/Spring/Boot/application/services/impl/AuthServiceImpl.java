package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.AuthResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.LoginRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.RegisterRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CartEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.VerificationTokenEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.EmailAlreadyExistsException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.InvalidEmailOrPasswordException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.InvalidTokenException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.TokenExpiredException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.CartRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.UserRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.VerificationTokenRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.UserService;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.utilities.JWTUtility;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtility jwtUtil;
    private EmailServiceImpl emailService;
    private final UserService userService;

    public String register(RegisterRequestDto request) {
        UserEntity foundedUser = userService.findUserByEmail(request.getEmail());

        if (foundedUser != null) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .enabled(false)
                .build();

        userRepository.save(user);

        // Create cart
        CartEntity cart = CartEntity.builder().user(user).build();
        cartRepository.save(cart);

        // Create verification token
        String token = UUID.randomUUID().toString();
        VerificationTokenEntity verificationToken = VerificationTokenEntity.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        tokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), token);

        return "Registration successful! Please check your email to verify account.";
    }

    public String verifyEmail(String token) {
        VerificationTokenEntity verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(InvalidTokenException::new);

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        /* tell-> Email is verified*/
        UserEntity user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        return "Email verified successfully!";
    }

    public AuthResponseDto login(LoginRequestDto request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidEmailOrPasswordException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidEmailOrPasswordException();
        }

        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateToken(user.getEmail()); // could be separate

        return new AuthResponseDto(accessToken, refreshToken);
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username);
        return new AuthResponseDto(newAccessToken, refreshToken);
    }
}
