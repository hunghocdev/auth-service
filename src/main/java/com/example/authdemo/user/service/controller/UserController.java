package com.example.authdemo.user.service.controller;

import com.example.authdemo.user.service.dto.AuthResponse;
import com.example.authdemo.user.service.dto.LoginRequest;
import com.example.authdemo.user.service.dto.RegisterRequest;
import com.example.authdemo.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController // Các method bên trong sẽ trả về JSON thay vì trả về HTML.
@RequestMapping("/api/auth")    // mọi endpoint sẽ bắt đầu bằng: /api/auth/register or /api/auth/login
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    @PostMapping("/login")      // API đăng nhập tài khoản
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        boolean ok = userService.login(req);    // tìm user trong DB / so sánh password hash

        if (ok) return ResponseEntity.ok(new AuthResponse(true, "Login success"));  // nếu ok tra ve la true
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(false, "Invalid credentials"));     // nguoc lại trả về  fail
    }
}