package com.example.authdemo.security;

import com.example.authdemo.module.user.entity.User;
import com.example.authdemo.module.user.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary // ĐÁNH DẤU LÀ BEAN ƯU TIÊN SỐ 1 để sửa lỗi "Found 2 beans" trong log của bạn
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm user từ Database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng có tên: " + username));

        // In Log kiểm tra ngay tại đây để xem Role có thực sự được nạp từ DB không
        System.out.println(">>> [LOAD USER]: " + username + " | Roles from DB: " + user.getRoles().size());

        return user;
    }
}