package com.example.authdemo.user.service.dto;
//success: boolean -> thao tác thành công hay thất bại
//message: String -> thông báo gửi lại cho client

public class AuthResponse {
    private boolean success;
    private String message;

    public AuthResponse() {}
    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
