package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services;


import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;

import java.util.List;


public interface CategoryService {

    CategoryEntity createCategory(CategoryEntity categoryEntity);
    CategoryEntity updateCategory(Integer id, CategoryEntity categoryEntity);
    List<CategoryEntity> findAllCategories();
    CategoryEntity findCategoryById(int id);
}
