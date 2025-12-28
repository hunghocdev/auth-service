package com.example.authdemo.security.oauth2;

import com.example.authdemo.module.user.entity.User;
import com.example.authdemo.module.user.entity.Role;
import com.example.authdemo.module.user.repository.RoleRepository;
import com.example.authdemo.module.user.repository.UserRepository;
import com.example.authdemo.security.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Class chuyên trách xử lý sau khi đăng nhập Google thành công.
 * Đây là cách làm chuẩn trong các dự án thực tế để tách biệt logic nghiệp vụ khỏi cấu hình.
 */
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2SuccessHandler(JwtService jwtService, UserRepository userRepository, RoleRepository roleRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 1. Lấy thông tin định danh từ Google
        String googleSub = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        System.out.println(">>> [SOCIAL AUTH] Processing Google User: " + email);

        // 2. Tìm hoặc Tạo mới User dựa trên 'sub' của Google
        User targetUser = userRepository.findByUsername(googleSub)
                .orElseGet(() -> {
                    System.out.println(">>> [SOCIAL AUTH] Registering new user with sub: " + googleSub);

                    User newUser = new User();
                    newUser.setUsername(googleSub);
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setPasswordHash("{noop}" + UUID.randomUUID().toString());

                    Role userRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("Chưa cấu hình ROLE_USER trong hệ thống!"));
                    newUser.getRoles().add(userRole);

                    return userRepository.save(newUser);
                });

        // 3. Tạo JWT cho phiên đăng nhập mới
        String accessToken = jwtService.createAccessToken(targetUser);

        // 4. Chuyển hướng người dùng về trang giao diện (Swagger) kèm Token
        response.sendRedirect("/swagger-ui.html?token=" + accessToken);
    }
}