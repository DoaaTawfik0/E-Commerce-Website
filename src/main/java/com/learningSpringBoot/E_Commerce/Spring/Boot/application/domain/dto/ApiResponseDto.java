package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto<T> {
    private int recordCount;
    private T response;
}
