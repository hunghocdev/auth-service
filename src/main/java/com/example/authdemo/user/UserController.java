package com.example.authdemo.user;

import com.example.authdemo.dto.AuthResponse;
import com.example.authdemo.dto.RegisterRequest;
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
    // Login endpoint is implemented in AuthController to support JWTs (access + refresh).
    // Keep this controller focused on user registration and other non-auth-token endpoints.
}