package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;


import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface CategoryRepository extends JpaRepository<CategoryEntity,Integer> {


}
