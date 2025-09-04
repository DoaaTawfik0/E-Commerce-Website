package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity saveUser(UserEntity userEntity);

    UserEntity findUserById(Integer id);

    List<UserEntity> findAllUsers();

    UserEntity findUserByEmail(String email);
}
