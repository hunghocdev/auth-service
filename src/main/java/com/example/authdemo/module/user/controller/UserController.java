package com.example.authdemo.module.user.controller;

import com.example.authdemo.module.user.entity.User;
import com.example.authdemo.module.user.service.UserService;
import com.example.authdemo.security.JwtService;
import com.example.authdemo.module.token.repository.RefreshTokenRepository;
import com.example.authdemo.module.token.model.RefreshToken;
import com.example.authdemo.module.token.TokenUtil;

import com.example.authdemo.module.auth.dto.AuthResponse;
import com.example.authdemo.module.auth.dto.LoginResponse;
import com.example.authdemo.module.auth.dto.RegisterRequest;
import com.example.authdemo.module.user.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import này rất quan trọng
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserController(UserService userService, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req);
        return ResponseEntity.ok(new AuthResponse(true, "Đăng ký thành công"));
    }

    @PutMapping("/update-profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')") // Sử dụng hasAnyRole cho ngắn gọn
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody UpdateProfileRequest req,
            @AuthenticationPrincipal User currentUser) { // Sửa từ Principal sang User trực tiếp

        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Không tìm thấy thông tin đăng nhập"));
        }

        try {
            // 1. Cập nhật thông tin qua Service
            userService.updateProfile(currentUser.getUsername(), req);

            // 2. Tạo Token mới (Vì profile thay đổi có thể cần cập nhật thông tin trong Token)
            String newAccess = jwtService.createAccessToken(currentUser);
            String newRefreshRaw = jwtService.createRefreshTokenString();
            String newRefreshHash = TokenUtil.sha256Hex(newRefreshRaw);
            OffsetDateTime newExpires = Instant.now().plus(Duration.ofDays(30)).atOffset(ZoneOffset.UTC);

            // 3. Xử lý Refresh Token (Thu hồi cũ, lưu mới)
            var existingTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(currentUser.getId());
            if (!existingTokens.isEmpty()) {
                existingTokens.forEach(t -> t.setRevoked(true));
                refreshTokenRepository.saveAll(existingTokens);
            }
            refreshTokenRepository.save(new RefreshToken(currentUser.getId(), newRefreshHash, newExpires));

            return ResponseEntity.ok(new LoginResponse(newAccess, newRefreshRaw));

        } catch (Exception e) {
            // Trả về lỗi chi tiết thay vì lỗi 500 chung chung
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Lỗi cập nhật: " + e.getMessage()
            ));
        }
    }
}