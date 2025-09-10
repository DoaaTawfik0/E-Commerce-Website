package com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.TestDataUtil;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserEntityRepositoryIntegrationTests {

    private final UserRepository underTest;

    @Autowired
    public UserEntityRepositoryIntegrationTests(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    @Transactional
    void testThatUserCanBeCreatedAndRecalled() {
        UserEntity savedUser = underTest.save(TestDataUtil.createTestUserEntityA());

        Optional<UserEntity> result = underTest.findById(savedUser.getUserId());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedUser);
    }

    @Test
    @Transactional
    public void testThatMultipleUsersCanBeCreatedAndRecalled() {
        UserEntity savedUserA = underTest.save(TestDataUtil.createTestUserEntityA());
        UserEntity savedUserB = underTest.save(TestDataUtil.createTestUserEntityB());

        List<UserEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(savedUserA, savedUserB);
    }

    @Test
    @Transactional
    public void testThatUserCanBeUpdated() {
        UserEntity savedUser = underTest.save(TestDataUtil.createTestUserEntityA());

        savedUser.setName("Updated User Name");
        savedUser.setEmail("updated.email@example.com");
        UserEntity updatedUser = underTest.save(savedUser);

        Optional<UserEntity> result = underTest.findById(updatedUser.getUserId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Updated User Name");
        assertThat(result.get().getEmail()).isEqualTo("updated.email@example.com");
    }

    @Test
    @Transactional
    public void testThatUserCanBeDeleted() {
        UserEntity savedUser = underTest.save(TestDataUtil.createTestUserEntityA());

        underTest.deleteById(savedUser.getUserId());

        Optional<UserEntity> result = underTest.findById(savedUser.getUserId());
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    public void testThatUserCanBeFoundByEmail() {
        UserEntity savedUser = underTest.save(TestDataUtil.createTestUserEntityA());

        Optional<UserEntity> result = underTest.findByEmail(savedUser.getEmail());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    @Transactional
    public void testThatNonExistentEmailReturnsEmpty() {
        Optional<UserEntity> result = underTest.findByEmail("nonexistent@example.com");
        assertThat(result).isEmpty();
    }
}