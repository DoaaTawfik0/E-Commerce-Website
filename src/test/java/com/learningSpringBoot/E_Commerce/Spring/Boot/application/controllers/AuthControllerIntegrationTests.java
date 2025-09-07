package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.AuthResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.LoginRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.RegisterRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.VerificationTokenEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.UserRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        // Clean up before each test
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testRegister_ReturnsSuccessMessage() throws Exception {
        RegisterRequestDto registerRequest = TestDataUtil.createRegisterReqDtoA();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Registration successful! Please check your email to verify account."));
    }

    @Test
    public void testRegister_WithExistingEmail_ReturnsConflict() throws Exception {
        // Create a user first
        userRepository.save(TestDataUtil.createTestUserEntityA());

        RegisterRequestDto registerRequest = TestDataUtil.createRegisterReqDtoA();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void testVerifyEmail_WithValidToken_ReturnsSuccess() throws Exception {
        // Create a user and verification token
        UserEntity user = TestDataUtil.createTestUserEntityA();
        user = userRepository.save(user);

        VerificationTokenEntity token = VerificationTokenEntity.builder()
                .token("valid-token")
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        verificationTokenRepository.save(token);

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/verify-email")
                        .param("token", "valid-token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Email verified successfully!"));
    }

    @Test
    public void testVerifyEmail_WithExpiredToken_ReturnsUnAuthorized() throws Exception {
        // Create a user and expired verification token
        UserEntity user = TestDataUtil.createTestUserEntityA();
        user = userRepository.save(user);

        VerificationTokenEntity token = VerificationTokenEntity.builder()
                .token("expired-token")
                .user(user)
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();
        verificationTokenRepository.save(token);

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/verify-email")
                        .param("token", "expired-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testVerifyEmail_WithInvalidToken_ReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/verify-email")
                        .param("token", "invalid-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid token..."));
    }

    @Test
    public void testLogin_WithValidCredentials_ReturnsAuthTokens() throws Exception {
        // Create a verified user
        UserEntity user = UserEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .enabled(true)
                .passwordHash(passwordEncoder.encode("password123"))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

        LoginRequestDto loginRequest = TestDataUtil.createLoginReqDtoA();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").exists());
    }

    @Test
    public void testLogin_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequestDto loginRequest = TestDataUtil.createLoginReqDtoA();
        loginRequest.setEmail("wrong@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testLogin_WithUnverifiedAccount_ReturnsUnauthorized() throws Exception {
        // Create an unverified user
        userRepository.save(TestDataUtil.createTestUserEntityA());

        LoginRequestDto loginRequest = TestDataUtil.createLoginReqDtoA();
        loginRequest.setEmail("unverified@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testRefreshToken_WithValidToken_ReturnsNewTokens() throws Exception {
        // First login to get a refresh token
        UserEntity user = UserEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .enabled(true)
                .passwordHash(passwordEncoder.encode("password123"))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        LoginRequestDto loginRequest = TestDataUtil.createLoginReqDtoA();

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        AuthResponseDto authResponse = objectMapper.readValue(response, AuthResponseDto.class);

        // Use refresh token
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh-token")
                        .param("refreshToken", authResponse.getRefreshToken()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").exists());
    }

    @Test
    public void testRefreshToken_WithInvalidToken_ReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh-token")
                        .param("refreshToken", "invalid-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid token..."));
    }
}