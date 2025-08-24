package com.learningSpringBoot.E_Commerce.Spring.Boot.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ECommerceSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceSpringBootApplication.class, args);
	}

}
