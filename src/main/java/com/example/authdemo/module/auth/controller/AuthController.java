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
import com.example.authdemo.module.user.repository.UserRepository; // Nhớ import cái này
import com.example.authdemo.module.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository; // Thêm Repository để tìm user khi refresh

    // Thời gian hết hạn của Refresh Token (30 ngày tính bằng mili giây)
    private static final long REFRESH_TOKEN_EXPIRY = 2592000000L;

    public AuthController(UserService userService,
                          JwtService jwtService,
                          RefreshTokenRepository refreshTokenRepository,
                          UserRepository userRepository) { // Inject thêm vào constructor
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // === ĐĂNG NHẬP ===
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest req) {
        // 1. Kiểm tra đăng nhập
        boolean ok = userService.login(req);
        if (!ok) return ResponseEntity.status(401).body(new AuthResponse(false, "Invalid credentials"));

        User user = userService.findByUsername(req.getUsername()).orElseThrow();

        // 2. Tạo Access Token
        // SỬA QUAN TRỌNG: Dùng Username thay vì ID
        String access = jwtService.createAccessToken(user.getUsername());

        // 3. Tạo Refresh Token
        String refreshRaw = jwtService.createRefreshTokenString();
        String refreshHash = TokenUtil.sha256Hex(refreshRaw);
        Instant expiresAt = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRY);

        // 4. Thu hồi các token cũ (Rotation Strategy)
        var existingTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId());
        existingTokens.forEach(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });

        // 5. Lưu token mới
        RefreshToken r = new RefreshToken(user.getId(), refreshHash, expiresAt);
        refreshTokenRepository.save(r);

        return ResponseEntity.ok(new LoginResponse(access, refreshRaw));
    }

    // === LÀM MỚI TOKEN (REFRESH) ===
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody RefreshRequest req) {
        String refreshRaw = req.getRefreshToken();
        String h = TokenUtil.sha256Hex(refreshRaw);

        // 1. Tìm token trong DB
        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHashAndRevokedFalse(h);
        if (opt.isEmpty()) return ResponseEntity.status(401).body(new AuthResponse(false, "Invalid refresh token"));

        RefreshToken token = opt.get();

        // 2. Kiểm tra hết hạn
        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            return ResponseEntity.status(401).body(new AuthResponse(false, "Refresh token expired"));
        }

        // 3. Rotation: Hủy cái cũ, cấp cái mới
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        String newRefreshRaw = jwtService.createRefreshTokenString();
        String newRefreshHash = TokenUtil.sha256Hex(newRefreshRaw);
        Instant newExpires = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRY);

        refreshTokenRepository.save(new RefreshToken(token.getUserId(), newRefreshHash, newExpires));

        // 4. Tạo Access Token Mới
        // SỬA QUAN TRỌNG: Phải tìm User để lấy Username
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found during refresh"));

        String newAccess = jwtService.createAccessToken(user.getUsername()); // Dùng Username

        return ResponseEntity.ok(new LoginResponse(newAccess, newRefreshRaw));
    }
}