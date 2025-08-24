package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.CategoryEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.ProductEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductEntityRepositoryIntegrationTests {

    private final ProductRepository underTest;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductEntityRepositoryIntegrationTests(ProductRepository underTest,
                                                   CategoryRepository categoryRepository) {
        this.underTest = underTest;
        this.categoryRepository = categoryRepository;
    }

    @Test
    @Transactional
    void testThatProductCanBeCreatedAndRecalled() {
        // First create and save a category
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());

        // Create product with the category
        ProductEntity savedProduct = underTest.save(TestDataUtil.createTestProductEntityA(category));

        Optional<ProductEntity> result = underTest.findById(savedProduct.getProductId());

        log.info("saved Product : {}, {}, {}, {}, {}",
                savedProduct.getProductId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice(),
                savedProduct.getCategoryEntity().getName()
        );

        result.ifPresent(product -> log.info("result Product : {}, {}, {}, {}, {}",
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategoryEntity().getName()
        ));

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedProduct);
    }

    @Test
    @Transactional
    public void testThatMultipleProductsCanBeCreatedAndRecalled() {
        // Create categories first
        CategoryEntity categoryA = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        CategoryEntity categoryB = categoryRepository.save(TestDataUtil.createTestCategoryEntityB());

        ProductEntity savedProductA = underTest.save(TestDataUtil.createTestProductEntityA(categoryA));
        ProductEntity savedProductB = underTest.save(TestDataUtil.createTestProductEntityB(categoryB));
        ProductEntity savedProductC = underTest.save(TestDataUtil.createTestProductEntityC(categoryA));

        Iterable<ProductEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(3)
                .containsExactlyInAnyOrder(savedProductA, savedProductB, savedProductC);
    }

    @Test
    @Transactional
    public void testThatProductCanBeUpdated() {
        // Create category and product
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity savedProduct = underTest.save(TestDataUtil.createTestProductEntityA(category));

        // Update product
        savedProduct.setName("Updated Product Name");
        savedProduct.setPrice(99.99);
        ProductEntity updatedProduct = underTest.save(savedProduct);

        Optional<ProductEntity> result = underTest.findById(updatedProduct.getProductId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Updated Product Name");
        assertThat(result.get().getPrice()).isEqualTo(99.99);
    }

    @Test
    @Transactional
    public void testThatProductCanBeDeleted() {
        // Create category and product
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA());
        ProductEntity savedProduct = underTest.save(TestDataUtil.createTestProductEntityA(category));

        underTest.deleteById(savedProduct.getProductId());

        Optional<ProductEntity> result = underTest.findById(savedProduct.getProductId());
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    public void testThatProductsCanBeFoundByCategory() {
        // Create categories
        CategoryEntity electronics = categoryRepository.save(TestDataUtil.createTestCategoryEntityD());
        CategoryEntity clothing = categoryRepository.save(TestDataUtil.createTestCategoryEntityB());

        // Create products
        ProductEntity laptop = underTest.save(TestDataUtil.createTestProductEntityA(electronics));
        ProductEntity smartphone = underTest.save(TestDataUtil.createTestProductEntityB(electronics));
        ProductEntity shirt = underTest.save(TestDataUtil.createTestProductEntityC(clothing));

        // Find products by electronics category
        Pageable pageable = PageRequest.of(0, 5);
        Page<ProductEntity> electronicsProducts = underTest.findByCategoryEntity_Name("Electronics", pageable);

        assertThat(electronicsProducts)
                .hasSize(2)
                .containsExactlyInAnyOrder(laptop, smartphone);
    }

}