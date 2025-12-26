package com.example.authdemo.exception;

import com.example.authdemo.module.auth.dto.AuthResponse;
import com.example.authdemo.common.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. Bắt lỗi Validation (@Valid) - Trả về 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = "Dữ liệu không hợp lệ";
        FieldError firstError = ex.getBindingResult().getFieldError();
        if (firstError != null) {
            errorMessage = firstError.getDefaultMessage();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(false, errorMessage));
    }

    /**
     * 2. Bắt lỗi Phân quyền (Access Denied) - Trả về 403
     * Cực kỳ quan trọng để Swagger hiển thị đúng thông báo khi user không đủ quyền
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AuthResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new AuthResponse(false, "Bạn không có quyền thực hiện hành động này"));
    }

    /**
     * 3. Bắt lỗi Sai thông tin đăng nhập - Trả về 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(false, "Tên đăng nhập hoặc mật khẩu không chính xác"));
    }

    /**
     * 4. Bắt lỗi Tài khoản đã tồn tại - Trả về 409
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AuthResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AuthResponse(false, ex.getMessage()));
    }

    /**
     * 5. Bắt lỗi Không tìm thấy dữ liệu - Trả về 404
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<AuthResponse> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new AuthResponse(false, "Không tìm thấy dữ liệu: " + ex.getMessage()));
    }

    /**
     * 6. Bắt tất cả các lỗi hệ thống còn lại - Trả về 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponse> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // Quan trọng để debug trong console IntelliJ
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Lỗi hệ thống: " + ex.getMessage()));
    }
}