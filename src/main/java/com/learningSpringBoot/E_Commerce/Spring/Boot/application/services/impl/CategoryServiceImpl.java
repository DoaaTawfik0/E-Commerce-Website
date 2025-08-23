package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.NotFoundException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.CategoryRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        // Ensure we are creating a new entity by ignoring any provided ID
        category.setCategoryId(null);
        return categoryRepository.save(category);
    }


    @Override
    public CategoryEntity updateCategory(Integer id, CategoryEntity category) {
        // Find the existing category first
        CategoryEntity existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with Id " + id + " does not exist"));

        // Update the fields of the existing category with the new data
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        // Save and return the updated entity
        return categoryRepository.save(existingCategory);
    }

    @Override
    public List<CategoryEntity> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryEntity findCategoryById(int id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with id " + id + " does not exist"));
    }

}
