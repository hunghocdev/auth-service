package com.example.authdemo.common.exception;

import com.example.authdemo.module.auth.dto.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

//RestControllerAdvice: Spring tự động quét và đăng ký annotation này làm nơi xử lý lỗi toàn bộ khi ứng dụng khởi động
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi @Valid (Validation)
     * Ví dụ: Email sai định dạng, Password để trống, v.v.
     * Trả về: 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Lấy lỗi đầu tiên để hiển thị cho gọn
        String errorMessage = "Dữ liệu không hợp lệ";
        FieldError firstError = ex.getBindingResult().getFieldError();
        if (firstError != null) {
            errorMessage = firstError.getDefaultMessage();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(false, errorMessage));
    }

    /**
     * Bắt lỗi không tìm thấy dữ liệu (ví dụ: findById không thấy)
     * Trả về: 404 Not Found
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<AuthResponse> handleNotFoundException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new AuthResponse(false, ex.getMessage()));
    }

    /**
     * Bắt lỗi liên quan đến tham số không hợp lệ (IllegalArgumentException)
     * Trả về: 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AuthResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(false, ex.getMessage()));
    }

    /**
     * Bắt tất cả các lỗi còn lại (Lỗi server, lỗi code, null pointer...)
     * Trả về: 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponse> handleAllExceptions(Exception ex) {
        // Log lỗi ra console để dev fix (Production nên dùng Logger)
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Lỗi hệ thống: " + ex.getMessage()));
    }
}