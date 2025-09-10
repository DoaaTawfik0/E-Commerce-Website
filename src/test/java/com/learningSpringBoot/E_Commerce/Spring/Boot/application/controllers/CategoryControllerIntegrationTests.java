package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CategoryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest
public class CategoryControllerIntegrationTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public CategoryControllerIntegrationTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateCategory_Returns201() throws Exception {
        CategoryDto testCategoryDto = TestDataUtil.createTestCategoryDtoA();
        String categoryJson = objectMapper.writeValueAsString(testCategoryDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateCategory_ReturnsSavedCategory() throws Exception {
        CategoryDto testCategoryDto = TestDataUtil.createTestCategoryDtoA();
        String categoryJson = objectMapper.writeValueAsString(testCategoryDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.categoryId").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value("Home & Kitchen")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.description").value("Household appliances and kitchenware")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testFindAllCategories_ReturnsRightCategories() throws Exception {
        CategoryDto testCategoryDtoA = TestDataUtil.createTestCategoryDtoA();
        CategoryDto testCategoryDtoB = TestDataUtil.createTestCategoryDtoB();

        //Saved first category into DB
        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategoryDtoA)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        //Saved second category into DB
        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategoryDtoB)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.recordCount").value(2))
                // make sure the name matches
                .andExpect(MockMvcResultMatchers.jsonPath("$.response[0].name").value("Home & Kitchen"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response[1].name").value("Books"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void testThatCategoryCanBeUpdated() throws Exception {
        // Create a category to update using a DTO (the API contract)
        CategoryDto createDto = TestDataUtil.createTestCategoryDtoA();
        String createJson = objectMapper.writeValueAsString(createDto);

        String createResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
        ).andReturn().getResponse().getContentAsString();

        // Reading CategoryDto from Api as it returns this type
        CategoryDto createdCategoryDto = objectMapper.readValue(createResponse, CategoryDto.class);
        int categoryId = createdCategoryDto.getCategoryId();

        // Prepare the UPDATE data as a DTO
        CategoryDto updateDto = TestDataUtil.createTestCategoryDtoB();
        updateDto.setCategoryId(null); // Ensure ID is not in the body
        String updateJson = objectMapper.writeValueAsString(updateDto);

        // Perform the update request and verify
        mockMvc.perform(
                MockMvcRequestBuilders.put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect( // Verify the response contains the updated data from the DTO
                MockMvcResultMatchers.jsonPath("$.name").value(updateDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.description").value(updateDto.getDescription())
        );
    }
}
