package com.example.authdemo.module.user.controller;

import com.example.authdemo.module.user.model.User;
import com.example.authdemo.module.user.service.UserService;
import com.example.authdemo.security.JwtService;
import com.example.authdemo.module.token.repository.RefreshTokenRepository;
import com.example.authdemo.module.token.model.RefreshToken;
import com.example.authdemo.module.token.TokenUtil;

import com.example.authdemo.module.auth.dto.AuthResponse;
import com.example.authdemo.module.auth.dto.LoginResponse;
import com.example.authdemo.module.auth.dto.RegisterRequest;
import com.example.authdemo.module.user.dto.UpdateProfileRequest;
// Import cho HttpStatus và ResponseEntity
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.NoSuchElementException;

@RestController // the method inside will return JSON instead of return HTML.
@RequestMapping("/api/auth")    // all endpoint will start by: /api/auth/register or /api/auth/login
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserController(UserService userService, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // API register account
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req);
        return ResponseEntity.ok(new AuthResponse(true, "Đăng ký thành công"));
    }

    // API update profile
    @PutMapping("/update-profile")
    public ResponseEntity<LoginResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest req, Principal principal) {
        // Update Profile
        userService.updateProfile(principal.getName(), req);

        // Find User (If not found -> throw error -> GlobalHandler returns 404)
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Logic for reissuing a new Token
        String newAccess = jwtService.createAccessToken(user.getUsername());
        String newRefreshRaw = jwtService.createRefreshTokenString();
        String newRefreshHash = TokenUtil.sha256Hex(newRefreshRaw);
        Instant newExpires = Instant.now().plusMillis(2592000000L); // 30 ngày


        // Cancel the old token
        var existingTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId());

        if (!existingTokens.isEmpty()) {
            // 1. Cập nhật trạng thái revoked
            existingTokens.forEach(t -> t.setRevoked(true));
            // 2. Tối ưu: Chỉ gọi saveAll một lần
            refreshTokenRepository.saveAll(existingTokens);
        }

        // Lưu token mới
        refreshTokenRepository.save(new RefreshToken(user.getId(), newRefreshHash, newExpires));
        return ResponseEntity.ok(new LoginResponse(newAccess, newRefreshRaw));
    }
}