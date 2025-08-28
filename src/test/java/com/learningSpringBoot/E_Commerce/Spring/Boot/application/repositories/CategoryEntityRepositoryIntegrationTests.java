package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;


import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryEntityRepositoryIntegrationTests {

    private final CategoryRepository underTest;


    @Autowired
    public CategoryEntityRepositoryIntegrationTests(CategoryRepository underTest) {
        this.underTest = underTest;
    }


    @Test
    @Transactional
    void testThatCategoryCanBeCreatedAndRecalled() {
        CategoryEntity savedCategory = underTest.save(TestDataUtil.createTestCategoryEntityA());

        Optional<CategoryEntity> result = underTest.findById(savedCategory.getCategoryId());


        log.info("saved Category : {} , {}, {}"
                , savedCategory.getCategoryId()
                , savedCategory.getName()
                , savedCategory.getDescription()
        );

        result.ifPresent(categoryEntity -> log.info("result Category : {} , {}, {}"
                , categoryEntity.getCategoryId()
                , categoryEntity.getName()
                , categoryEntity.getDescription()
        ));

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedCategory);
    }

    @Test
    @Transactional
    public void testThatMultipleCategoriesCanBeCreatedAndRecalled() {
        CategoryEntity savedCategoryA = underTest.save(TestDataUtil.createTestCategoryEntityA());
        CategoryEntity savedCategoryB = underTest.save(TestDataUtil.createTestCategoryEntityB());
        CategoryEntity savedCategoryC = underTest.save(TestDataUtil.createTestCategoryEntityC());

        Iterable<CategoryEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(3)
                .containsExactly(savedCategoryA, savedCategoryB, savedCategoryC);
    }

}
