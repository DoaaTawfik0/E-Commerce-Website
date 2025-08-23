package com.learningSpringBoot.E_Commerce.Spring.Boot.application.domain.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Size(min = 3)
    private String name;

    @Size(min = 10)
    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "categoryEntity",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ProductEntity> productEntities = new ArrayList<>();

}
