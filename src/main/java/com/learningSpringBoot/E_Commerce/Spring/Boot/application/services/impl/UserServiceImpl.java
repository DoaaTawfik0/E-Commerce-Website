package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.UpdateUserRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.EmailAlreadyExistsException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.NotFoundException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.repositories.UserRepository;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
        userRepository.findByEmail(userEntity.getEmail())
                .ifPresent(u -> {
                    throw new EmailAlreadyExistsException(userEntity.getEmail());
                });
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " does not exist"));
    }

    @Override
    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }


    @Override
    @Transactional
    public UserEntity updateUser(Integer userId, UpdateUserRequestDto request) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.findByEmail(request.getEmail()).isPresent())
                throw new EmailAlreadyExistsException(request.getEmail());
            user.setEmail(request.getEmail());
        }

        return userRepository.save(user);
    }

}
