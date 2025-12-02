package com.example.authdemo.user.service;

import com.example.authdemo.user.service.entity.User;
import com.example.authdemo.user.service.dto.LoginRequest;      // Kiểm tra login
import com.example.authdemo.user.service.dto.RegisterRequest;   // Kiểm tra đăng nhập
import com.example.authdemo.user.service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Nếu là đăng ký mới
    // Kiểm tra username or email đã tồn tại hay chưa
    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");  // bắt lỗi ném ra thông báo
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // hash password in service layer
        // bắt đầu mã hóa mật khẩu
        String hashed = passwordEncoder.encode(req.getPassword());
        // thêm thông tin user mới vào DB
        User user = new User(req.getUsername().trim(), req.getEmail().trim(), hashed);
        userRepository.save(user);
    }

    // Nếu là đăng nhập
    @Transactional(readOnly = true) // Chỉ đọc dữ liệu từ database, không thực hiện thay đổi (insert/update/delete).
    public boolean login(LoginRequest req) {
        // tìm thông tin của người đăng nhập trong DB
        Optional<User> opt = userRepository.findByUsername(req.getUsername());
        // opt trả về rỗng có nghĩa là không có Username trong DB
        if (opt.isEmpty()) return false;    // Trả về false đăng nhập thất bại

        User user = opt.get();      // lấy thông tin của user đăng nhập
        // verify password (BCrypt checks salt embedded in stored hash)
        // user.getPasswordHash(): Mật khẩu đã hash được lưu trong DB
        // req.getPassword(): là mật khẩu plaintext mà người dùng vừa nhập
        // đem 2 cái matches với nhau nếu trùng passwordHash thì trả về true và ngược lại
        return passwordEncoder.matches(req.getPassword(), user.getPasswordHash());
    }
}
