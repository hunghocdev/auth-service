package com.example.authdemo.config;

import com.example.authdemo.security.JwtAuthenticationFilter;
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

// IMPORT MỚI CHO OAUTH2
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
@EnableWebSecurity   // Kích hoạt bảo mật Web
@EnableMethodSecurity // Cho phép sử dụng @PreAuthorize trong Controller
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
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
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                )
                // Thiết lập cơ chế Stateless (không dùng Session)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Thêm Filter kiểm tra JWT trước bước xác thực cơ bản
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    /**
     * Xử lý sau khi người dùng đăng nhập Google thành công.
     */
    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            // Lấy thông tin từ Google
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            System.out.println(">>> [OAUTH2 SUCCESS] Email: " + email + " | Name: " + name);

            // TODO: Tại đây bạn sẽ gọi UserService để:
            // 1. Kiểm tra User có trong DB chưa, nếu chưa thì tạo mới + gán ROLE_USER.
            // 2. Tạo chuỗi JWT Token từ User này.

            String mockToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."; // Thay bằng token thật của bạn

            // Redirect về Swagger kèm theo Token để tiện kiểm thử
            response.sendRedirect("/swagger-ui.html?token=" + mockToken);
        };
    }
}