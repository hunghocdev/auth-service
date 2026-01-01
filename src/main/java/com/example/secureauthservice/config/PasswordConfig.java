package com.example.secureauthservice.config;
//====== Class mã hóa mật khẩu của người dùng ======

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;    // Thư viện crypto → cung cấp thuật toán mã hóa an toàn.
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration  // Bđánh dấu class config.
public class PasswordConfig {

    @Bean       // khai báo bean Spring.
    public PasswordEncoder passwordEncoder() {
        // strength/cost mặc định 10; tăng lên 12 nếu muốn chậm hơn (an toàn hơn)
        return new BCryptPasswordEncoder();   // tạo BCrypt encoder.
        /*Tạo instance của BCryptPasswordEncoder, dùng để:
            - Mã hóa mật khẩu (encode(rawPassword)) trước khi lưu vào DB.
            - Kiểm tra mật khẩu (matches(rawPassword, hashedPassword)) khi login.
         */
    }
}
