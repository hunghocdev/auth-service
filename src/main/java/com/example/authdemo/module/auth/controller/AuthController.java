package com.example.authdemo.module.auth.controller;

import com.example.authdemo.module.auth.dto.AuthResponse;
import com.example.authdemo.module.auth.dto.LoginRequest;
import com.example.authdemo.module.auth.dto.LoginResponse;
import com.example.authdemo.module.auth.dto.RefreshRequest;
import com.example.authdemo.security.JwtService;
import com.example.authdemo.module.token.model.RefreshToken;
import com.example.authdemo.module.token.repository.RefreshTokenRepository;
import com.example.authdemo.module.token.TokenUtil;
import com.example.authdemo.module.user.model.User;
import com.example.authdemo.module.user.repository.UserRepository;
import com.example.authdemo.module.user.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private static final long REFRESH_TOKEN_EXPIRY = 2592000000L;

    public AuthController(UserService userService, JwtService jwtService, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // === Login ===
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest req) {
        // 1. check username/password
        boolean ok = userService.login(req);
        if (!ok) {
            // Đây là logic nghiệp vụ (sai pass), trả về 401 luôn
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Sai tên đăng nhập hoặc mật khẩu"));
        }

        // 2. Tìm User (Nếu lỗi DB hoặc logic lạ -> GlobalHandler bắt 500)
        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // 3. Tạo Token
        String access = jwtService.createAccessToken(user.getUsername());
        String refreshRaw = jwtService.createRefreshTokenString();
        String refreshHash = TokenUtil.sha256Hex(refreshRaw);
        Instant expiresAt = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRY);

        // Revoke token cũ
        var existingTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId());
        if (!existingTokens.isEmpty()) {
            // 1. Cập nhật trạng thái revoked
            existingTokens.forEach(t -> t.setRevoked(true));
            // 2. Tối ưu: Chỉ gọi saveAll một lần
            refreshTokenRepository.saveAll(existingTokens);
        }
        // Save new token in database
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshHash, expiresAt));

        //  Đóng gói dto và trả về client
        return ResponseEntity.ok(new LoginResponse(access, refreshRaw));
    }

    // === REFRESH TOKEN ===
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@Valid @RequestBody RefreshRequest req) {
        String refreshRaw = req.getRefreshToken();
        String h = TokenUtil.sha256Hex(refreshRaw);

        // 1. Tìm token
        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHashAndRevokedFalse(h);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Refresh token không hợp lệ"));
        }

        RefreshToken token = opt.get();

        // 2. Kiểm tra hết hạn
        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Refresh token đã hết hạn, vui lòng đăng nhập lại"));
        }

        // 3. Rotation (Đổi cái cũ lấy cái mới)
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        String newRefreshRaw = jwtService.createRefreshTokenString();
        String newRefreshHash = TokenUtil.sha256Hex(newRefreshRaw);
        Instant newExpires = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRY);
        refreshTokenRepository.save(new RefreshToken(token.getUserId(), newRefreshHash, newExpires));

        // 4. Tìm user để tạo Access Token
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng sở hữu token này"));

        String newAccess = jwtService.createAccessToken(user.getUsername());

        return ResponseEntity.ok(new LoginResponse(newAccess, newRefreshRaw));
    }
}