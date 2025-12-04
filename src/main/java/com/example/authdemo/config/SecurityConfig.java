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
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Cho phép truy cập public
                        .anyRequest().authenticated() // Còn lại phải đăng nhập
                )
                // Cấu hình không lưu Session (Stateless) vì dùng JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Thêm filter JWT vào trước filter đăng nhập mặc định
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}