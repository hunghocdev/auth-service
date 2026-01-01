package com.example.secureauthservice.module.auth.controller;

import com.example.secureauthservice.module.auth.dto.AuthResponse;
import com.example.secureauthservice.module.auth.dto.LoginRequest;
import com.example.secureauthservice.module.auth.dto.LoginResponse;
import com.example.secureauthservice.module.auth.dto.RefreshRequest;
import com.example.secureauthservice.security.JwtService;
import com.example.secureauthservice.module.token.model.RefreshToken;
import com.example.secureauthservice.module.token.repository.RefreshTokenRepository;
import com.example.secureauthservice.module.token.TokenUtil;
import com.example.secureauthservice.module.user.entity.User;
import com.example.secureauthservice.module.user.repository.UserRepository;
import com.example.secureauthservice.module.user.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller handling authentication processes: Login, Token Refresh, and Current User Info.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // Refresh Token expiry duration (e.g., 30 days in milliseconds)
    private static final long REFRESH_TOKEN_EXPIRY_MS = 2592000000L;

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

    /**
     * Authenticates user and returns Access & Refresh tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest req) {
        // 1. Validate credentials via UserService
        boolean isAuthenticated = userService.login(req);
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Invalid username or password"));
        }

        // 2. Retrieve User entity
        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // 3. Generate Access Token
        String accessToken = jwtService.createAccessToken(user);

        // 4. Generate Refresh Token
        String refreshRaw = jwtService.createRefreshTokenString();
        String refreshHash = TokenUtil.sha256Hex(refreshRaw);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRY_MS / 1000);

        // 5. Revoke old refresh tokens for this user
        var existingTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId());
        if (!existingTokens.isEmpty()) {
            existingTokens.forEach(t -> t.setRevoked(true));
            refreshTokenRepository.saveAll(existingTokens);
        }

        // 6. Save new Refresh Token
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshHash, expiresAt));

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshRaw));
    }

    /**
     * Rotates the Refresh Token and provides a new Access Token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@Valid @RequestBody RefreshRequest req) {
        String refreshRaw = req.getRefreshToken();
        String tokenHash = TokenUtil.sha256Hex(refreshRaw);

        // 1. Locate valid token in DB
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Invalid refresh token"));
        }

        RefreshToken token = tokenOpt.get();

        // 2. Check for expiration
        if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Refresh token expired, please login again"));
        }

        // 3. Token Rotation (Revoke old, create new)
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        String newRefreshRaw = jwtService.createRefreshTokenString();
        String newRefreshHash = TokenUtil.sha256Hex(newRefreshRaw);
        OffsetDateTime newExpires = OffsetDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRY_MS / 1000);
        refreshTokenRepository.save(new RefreshToken(token.getUserId(), newRefreshHash, newExpires));

        // 4. Generate new Access Token
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Owner of this token not found"));

        String newAccessToken = jwtService.createAccessToken(user);

        return ResponseEntity.ok(new LoginResponse(newAccessToken, newRefreshRaw));
    }

    /**
     * Returns the currently authenticated user's profile details.
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName() != null ? user.getFullName() : "",
                "phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                "address", user.getAddress() != null ? user.getAddress() : "",
                "dateOfBirth", user.getDateOfBirth() != null ? user.getDateOfBirth() : "",
                "gender", user.getGender() != null ? user.getGender() : "",
                "roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        ));
    }
}