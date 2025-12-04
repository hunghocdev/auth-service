package com.example.authdemo.module.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Vui lòng nhập username")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;

    public LoginRequest() {}

    // Getters & Setters...
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}