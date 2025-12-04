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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;


@RestController // Các method bên trong sẽ trả về JSON thay vì trả về HTML.
@RequestMapping("/api/auth")    // mọi endpoint sẽ bắt đầu bằng: /api/auth/register or /api/auth/login
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserController(UserService userService, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/register")       // API đăng ký tài khoản
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        try {
            userService.register(req);      // Gọi tầng service để: kiểm tra username đã tồn tại chưa / hash password / lưu DB
            return ResponseEntity.ok(new AuthResponse(true, "Registered successfully"));
        } catch (IllegalArgumentException ex) {     // Trả về 409 CONFLICT khi: username đã tồn tại và dữ liệu không hợp lệ
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthResponse(false, ex.getMessage()));
        } catch (Exception ex) {    //Trả về 500 INTERNAL_SERVER_ERROR -> Ví dụ: lỗi kết nối DB.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(false, "Server error"));
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @Valid
            @RequestBody
            UpdateProfileRequest req, Principal principal
    ) {
        try {
            userService.updateProfile(principal.getName(), req);
            User user = userService.findByUsername(principal.getName()).orElseThrow();

            // --- SỬA Ở ĐÂY (CÁCH 2) ---
            // Vì JwtService.createAccessToken nhận String, nên truyền getUsername()
            String newAccess = jwtService.createAccessToken(user.getUsername());

            String newRefreshRaw = jwtService.createRefreshTokenString();
            String newRefreshHash = TokenUtil.sha256Hex(newRefreshRaw);
            Instant newExpires = Instant.now().plusMillis(2592000000L); // 30 ngày

            // Hủy Token cũ (Vẫn dùng ID để tìm trong DB là đúng)
            var existingTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId());
            existingTokens.forEach(t -> {
                t.setRevoked(true);
                refreshTokenRepository.save(t);
            });

            // Lưu token mới (Vẫn lưu theo userId vào DB là đúng)
            refreshTokenRepository.save(new RefreshToken(user.getId(), newRefreshHash, newExpires));

            return ResponseEntity.ok(new LoginResponse(newAccess, newRefreshRaw));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, ex.getMessage()));
        }
    }
}