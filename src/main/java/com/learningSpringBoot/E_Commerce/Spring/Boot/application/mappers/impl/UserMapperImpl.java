package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.impl;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.UserRequestDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto.UserResponseDto;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities.UserEntity;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapperImpl implements Mapper<UserEntity, UserResponseDto> {
    private final ModelMapper modelMapper;

    @Override
    public UserResponseDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserResponseDto.class);
    }

    @Override
    public UserEntity mapFrom(UserResponseDto userResponseDto) {
        return modelMapper.map(userResponseDto, UserEntity.class);
    }

    // Optional: method to map from UserRequestDto to UserEntity
    public UserEntity mapFromRequest(UserRequestDto dto) {
        UserEntity entity = modelMapper.map(dto, UserEntity.class);
        entity.setPasswordHash(dto.getPassword()); // Hash password later if needed
        return entity;
    }
}
