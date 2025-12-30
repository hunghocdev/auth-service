package com.example.authdemo.config;

import com.example.authdemo.module.user.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    /**
     * Logic để lấy ID của người dùng đang đăng nhập.
     * Giá trị này sẽ được tự động điền vào trường 'createdBy' và 'updatedBy' trong BaseEntity.
     */
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.empty();
            }

            // Lấy đối tượng User từ SecurityContext (đã được nạp bởi JwtFilter)
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return Optional.of(((User) principal).getId());
            }

            return Optional.empty();
        };
    }
}