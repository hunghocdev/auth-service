package com.example.authdemo.module.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String email;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    private String fullName;
    private String phoneNumber;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String sex;

    // QUAN TRỌNG: EAGER giúp nạp Role ngay khi tìm User
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public boolean verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (rawPassword == null || this.passwordHash == null) return false;
        return passwordEncoder.matches(rawPassword, this.passwordHash);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // --- DIAGNOSTIC LOG (KIỂM TRA TẠI ĐÂY) ---
        if (roles == null || roles.isEmpty()) {
            System.out.println(">>> [SECURITY ALERT]: User [" + username + "] không có quyền nào trong Database (user_roles rỗng)!");
            return Collections.emptyList();
        }

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .filter(role -> role.getName() != null)
                .map(role -> {
                    String name = role.getName().trim().toUpperCase();
                    if (!name.startsWith("ROLE_")) name = "ROLE_" + name;
                    return new SimpleGrantedAuthority(name);
                })
                .collect(Collectors.toList());

        // In ra để bạn đối chiếu với Token
        System.out.println(">>> [AUTH SUCCESS]: User [" + username + "] đã nạp các quyền: " + authorities);
        return authorities;
    }

    @Override @JsonIgnore public String getPassword() { return this.passwordHash; }
    @Override public String getUsername() { return this.username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}