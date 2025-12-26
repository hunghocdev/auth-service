package com.example.authdemo.module.auth.controller;

import com.example.authdemo.module.auth.dto.AuthResponse;
import com.example.authdemo.module.auth.dto.LoginRequest;
import com.example.authdemo.module.auth.dto.LoginResponse;
import com.example.authdemo.module.auth.dto.RefreshRequest;
import com.example.authdemo.security.JwtService;
import com.example.authdemo.module.token.model.RefreshToken;
import com.example.authdemo.module.token.repository.RefreshTokenRepository;
import com.example.authdemo.module.token.TokenUtil;
import com.example.authdemo.module.user.entity.User;
import com.example.authdemo.module.user.repository.UserRepository;
import com.example.authdemo.module.user.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // Thời gian sống của Refresh Token (ví dụ: 30 ngày)
    private static final long REFRESH_TOKEN_EXPIRY = 2592000000L;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // === Login ===
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest req) {
        // 1. Kiểm tra username/password thông qua UserService
        boolean ok = userService.login(req);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Sai tên đăng nhập hoặc mật khẩu"));
        }

        // 2. Tìm đối tượng User từ Database
        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // 3. Tạo Access Token (Sử dụng UserDetails fix)
        String access = jwtService.createAccessToken(user);

        // 4. Tạo Refresh Token
        String refreshRaw = jwtService.createRefreshTokenString();
        String refreshHash = TokenUtil.sha256Hex(refreshRaw);
        Instant expiresAt = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRY);

        // 5. Thu hồi (Revoke) các Refresh Token cũ của User này
        var existingTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId());
        if (!existingTokens.isEmpty()) {
            existingTokens.forEach(t -> t.setRevoked(true));
            refreshTokenRepository.saveAll(existingTokens);
        }

        // 6. Lưu Refresh Token mới vào database
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshHash, expiresAt));

        return ResponseEntity.ok(new LoginResponse(access, refreshRaw));
    }

    // === REFRESH TOKEN ===
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@Valid @RequestBody RefreshRequest req) {
        String refreshRaw = req.getRefreshToken();
        String h = TokenUtil.sha256Hex(refreshRaw);

        // 1. Tìm token trong database
        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHashAndRevokedFalse(h);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Refresh token không hợp lệ"));
        }

        RefreshToken token = opt.get();

        // 2. Kiểm tra thời hạn
        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Refresh token đã hết hạn, vui lòng đăng nhập lại"));
        }

        // 3. Cơ chế Rotation (Hủy token cũ, tạo cái mới)
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        String newRefreshRaw = jwtService.createRefreshTokenString();
        String newRefreshHash = TokenUtil.sha256Hex(newRefreshRaw);
        Instant newExpires = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRY);
        refreshTokenRepository.save(new RefreshToken(token.getUserId(), newRefreshHash, newExpires));

        // 4. Tìm user để cấp Access Token mới
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng sở hữu token này"));

        // Tạo Access Token mới từ đối tượng User
        String newAccess = jwtService.createAccessToken(user);

        return ResponseEntity.ok(new LoginResponse(newAccess, newRefreshRaw));
    }

    /**
     * API duy nhất xử lý lấy thông tin người dùng hiện tại.
     * Sử dụng @AuthenticationPrincipal để lấy trực tiếp User Entity từ SecurityContext.
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Chưa đăng nhập"));
        }

        // Trả về thông tin chi tiết
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName() != null ? user.getFullName() : "",
                "phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                "address", user.getAddress() != null ? user.getAddress() : "",
                "dateOfBirth", user.getDateOfBirth() != null ? user.getDateOfBirth() : "",
                "sex", user.getSex() != null ? user.getSex() : "",
                "roles", user.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList())
        ));
    }
}