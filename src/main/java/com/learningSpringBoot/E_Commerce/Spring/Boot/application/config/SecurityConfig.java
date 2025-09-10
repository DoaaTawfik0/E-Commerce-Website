package com.learningSpringBoot.E_Commerce.Spring.Boot.application.config;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.filters.JWTAuthenticationFilter;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl.CustomUserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtFilter;
    private final CustomUserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        /* Auth endpoints */
                        .requestMatchers("/auth/**").permitAll()
                        /* Swagger endpoints */
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        /* Categories endpoints */
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers("/categories").hasRole("ADMIN")
                        /*Products endpoints */
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers("/products/**").hasRole("ADMIN")
                        /* Cart endpoints */
                        .requestMatchers("/cart/**").hasRole("USER")
                        /* Users endpoints */
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/**").hasAnyRole("USER","ADMIN")
                        /* Orders endpoints */
                        .requestMatchers(HttpMethod.POST, "/orders/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("USER","ADMIN")
                        /* Any other request*/
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
