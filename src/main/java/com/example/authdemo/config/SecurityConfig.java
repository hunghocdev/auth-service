package com.example.authdemo.config;

import com.example.authdemo.module.user.repository.RoleRepository;
import com.example.authdemo.module.user.repository.UserRepository;
import com.example.authdemo.security.JwtAuthenticationFilter;
import com.example.authdemo.security.JwtService;
import com.example.authdemo.security.oauth2.CustomOAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 1. IMPORT CHO CÁC ANNOTATION BẢO MẬT
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

// 2. IMPORT CHO CÁC THÀNH PHẦN CẤU HÌNH HTTP
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity   // Kích hoạt bảo mật Web
@EnableMethodSecurity // Cho phép sử dụng @PreAuthorize trong Controller
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF vì hệ thống dùng JWT (Stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Cấu hình phân quyền yêu cầu
                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép các endpoint xác thực công khai và OAuth2 callback
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()

                        // 2. Mở khóa tài nguyên của Swagger UI và API Docs
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        // Tất cả các request còn lại (bao gồm /api/auth/me) phải được xác thực
                        .anyRequest().authenticated()
                )
                // --- KÍCH HOẠT ĐĂNG NHẬP GOOGLE VỚI HANDLER RIÊNG ---
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customOAuth2SuccessHandler)
                )
                        // Thiết lập cơ chế Stateless (không dùng Session)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Thêm Filter kiểm tra JWT trước bước xác thực cơ bản
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}