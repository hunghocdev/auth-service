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
        try {
            userService.register(req);      // call the service layer to: check if username already exists/hash password/save DB
            return ResponseEntity.ok(new AuthResponse(true, "Registered successfully"));
        } catch (IllegalArgumentException ex) {     // Return 409 CONFLICT when: data already exist or invalid data
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthResponse(false, ex.getMessage()));
        } catch (Exception ex) {    //Return 500 INTERNAL_SERVER_ERROR -> erro database.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(false, "Server error"));
        }
    }

    // API update profile
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest req, Principal principal) { // Principal is authenticated user
        try {
            userService.updateProfile(principal.getName(), req);
            User user = userService.findByUsername(principal.getName()).orElseThrow();

            // Because JwtService.createAccessToken takes a String, pass getUsername()
            String newAccess = jwtService.createAccessToken(user.getUsername());
            String newRefreshRaw = jwtService.createRefreshTokenString();
            String newRefreshHash = TokenUtil.sha256Hex(newRefreshRaw);
            Instant newExpires = Instant.now().plusMillis(2592000000L); // 30 ngày

            // Cancel the old token
            var existingTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId());
            existingTokens.forEach(t -> {
                t.setRevoked(true);
                refreshTokenRepository.save(t);
            });
            // Save new token
            refreshTokenRepository.save(new RefreshToken(user.getId(), newRefreshHash, newExpires));
            // return json
            return ResponseEntity.ok(new LoginResponse(newAccess, newRefreshRaw));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, ex.getMessage()));
        }
    }
}