package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.CustomUserDetails;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.UpdateUserRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        testUser = userRepository.save(TestDataUtil.createTestUserEntityA());

        // Set up authentication context
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testGetCurrentUser_ReturnsAuthenticatedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/me"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testUser.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(testUser.getRole()));
    }

    @Test
    public void testUpdateUser_WithValidData_ReturnsUpdatedUser() throws Exception {
        UpdateUserRequestDto updateRequest = TestDataUtil.createUpdateUserReqDtoA();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("updated name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    public void testUpdateUser_WithPartialData_UpdatesOnlyProvidedFields() throws Exception {
        // Update only name
        UpdateUserRequestDto updateRequest = TestDataUtil.createUpdateUserReqDtoPartialData();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Name Only"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(testUser.getEmail()));

        // Verify in database
        UserEntity updatedUser = userRepository.findById(testUser.getUserId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("New Name Only");
        assertThat(updatedUser.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testUpdateUser_WithNonExistentUserId_ReturnsNotFound() throws Exception {
        UpdateUserRequestDto updateRequest = TestDataUtil.createUpdateUserReqDtoA();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdateUser_WithExistingEmail_ReturnsBadRequest() throws Exception {
        // Create another user with a different email
        UserEntity anotherUser = TestDataUtil.createTestUserEntityB();
        userRepository.save(anotherUser);

        // Try to update test user with the same email
        UpdateUserRequestDto updateRequest = TestDataUtil.createUpdateUserReqDtoA();
        updateRequest.setEmail("jane.smith@example.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void testUpdateUser_WithInvalidEmailFormat_ReturnsBadRequest() throws Exception {
        UpdateUserRequestDto updateRequest = TestDataUtil.createUpdateUserReqDto_WithInvalidMail();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateUser_WithEmptyName_ReturnsBadRequest() throws Exception {
        UpdateUserRequestDto updateRequest = TestDataUtil.createUpdateUserReqDtoA();
        updateRequest.setName("");
        updateRequest.setEmail("valid@example.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateUser_PreservesOtherFields() throws Exception {
        // Store original values
        String originalPassword = testUser.getPasswordHash();
        String originalRole = testUser.getRole();
        Boolean originalEnabled = testUser.isEnabled();

        UpdateUserRequestDto updateRequest = TestDataUtil.createUpdateUserReqDtoA();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify other fields are preserved
        UserEntity updatedUser = userRepository.findById(testUser.getUserId()).orElseThrow();
        assertThat(updatedUser.getPasswordHash()).isEqualTo(originalPassword);
        assertThat(updatedUser.getRole()).isEqualTo(originalRole);
        assertThat(updatedUser.isEnabled()).isEqualTo(originalEnabled);
    }
}