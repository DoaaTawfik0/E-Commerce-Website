package com.learningSpringBoot.E_Commerce.Spring.Boot.application.mappers;

public interface Mapper<A, B> {
    B mapTo(A a); // map to dto

    A mapFrom(B b); //map from dto
}
