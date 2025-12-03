package com.example.authdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF để test Postman
                .authorizeHttpRequests(auth -> auth
                        // QUAN TRỌNG: Cho phép ai cũng vào được các link bắt đầu bằng /api/auth/
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated() // Các link khác phải đăng nhập mới được vào
                );

        return http.build();
    }
}