package com.learningSpringBoot.E_Commerce.Spring.Boot.application.controllers;


import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.ApiResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.CategoryDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final Mapper<CategoryEntity, CategoryDto> mapper;

    public CategoryController(CategoryService categoryService, Mapper<CategoryEntity, CategoryDto> mapper) {
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponseDto<List<CategoryDto>>> findAllCategories() {
        List<CategoryEntity> categories = categoryService.findAllCategories();
        List<CategoryDto> listedCategoriesDto = categories.stream()
                .map(mapper::mapTo)
                .toList();

        ApiResponseDto<List<CategoryDto>> response = new ApiResponseDto<>(
                listedCategoriesDto.size(),  // recordCount
                listedCategoriesDto          // response
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> findOneCategory(@PathVariable int id) {
        CategoryEntity category = categoryService.findCategoryById(id);
        CategoryDto existingCategoryDto = mapper.mapTo(category);

        return new ResponseEntity<>(existingCategoryDto, HttpStatus.OK);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createNewCategory(@RequestBody CategoryDto category) {
        CategoryEntity categoryEntity = mapper.mapFrom(category);
        // Call the service method for creation
        CategoryDto savedCategoryDto = mapper.mapTo(categoryService.createCategory(categoryEntity));
        return new ResponseEntity<>(savedCategoryDto, HttpStatus.CREATED);
    }

    // Add this new endpoint for updates
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateExistingCategory(
            @PathVariable("id") Integer id,
            @RequestBody CategoryDto category) {

        CategoryEntity categoryEntity = mapper.mapFrom(category);
        // Call the  service method for update
        CategoryDto updatedCategoryDto = mapper.mapTo(categoryService.updateCategory(id, categoryEntity));
        return new ResponseEntity<>(updatedCategoryDto, HttpStatus.OK);
    }
}
