package com.example.authdemo.module.user.service;

import com.example.authdemo.module.auth.dto.LoginRequest;      // Kiểm tra login
import com.example.authdemo.module.auth.dto.RegisterRequest;   // Kiểm tra đăng nhập
import com.example.authdemo.module.user.dto.UpdateProfileRequest;
import com.example.authdemo.module.user.entity.Role;
import com.example.authdemo.module.user.entity.User;
import com.example.authdemo.module.user.repository.UserRepository;
import com.example.authdemo.module.user.repository.RoleRepository;
import com.example.authdemo.common.exception.UserAlreadyExistsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final  RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // check if the username exists in the database
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Register
    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            // Thay IllegalArgumentException bằng UserAlreadyExistsException
            throw new UserAlreadyExistsException("Username " + req.getUsername() + " đã tồn tại");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            // Thay IllegalArgumentException bằng UserAlreadyExistsException
            throw new UserAlreadyExistsException("Email " + req.getEmail() + " đã tồn tại");
        }
        // hash password in service layer
        String hashed = passwordEncoder.encode(req.getPassword());
        // save infor in database
        User user = new User(req.getUsername().trim(), req.getEmail().trim(), hashed);
        // Find ROLE_USER about DB and add in User
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.getRoles().add(userRole);
        userRepository.save(user);
    }

    // Login
    @Transactional(readOnly = true) // read only in database, don not make changes (insert/update/delete).
    public boolean login(LoginRequest req) {
        Optional<User> opt = userRepository.findByUsername(req.getUsername());
        if (opt.isEmpty()) return false;    // return false login false
        User user = opt.get();
        // verify password via the entity method (BCrypt checks salt embedded in stored hash)
        // Keep the comparison inside the entity so the password hash is not widely exposed
        return user.verifyPassword(req.getPassword(), passwordEncoder);
    }
    // Update profile
    @Transactional
    public void updateProfile(String currentUsername, UpdateProfileRequest req) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // if value is not null, set value update value
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null) user.setPhoneNumber(req.getPhoneNumber());
        if (req.getAddress() != null) user.setAddress(req.getAddress());
        if (req.getDateOfBirth() != null) user.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null) user.setGender(req.getGender());
        // save in database
        userRepository.save(user);
    }
}