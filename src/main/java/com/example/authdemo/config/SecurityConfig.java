package com.example.authdemo.config;

import com.example.authdemo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Import cho HttpSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// Import cho AbstractHttpConfigurer (để tắt CSRF)
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// Import cho SessionCreationPolicy (Quản lý session)
import org.springframework.security.config.http.SessionCreationPolicy;
// Import cho SecurityFilterChain
import org.springframework.security.web.SecurityFilterChain;
// Import cho UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép các endpoint Auth của bạn
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Mở khóa Swagger UI và API Docs
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        // 3. Các request còn lại mới cần login
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}