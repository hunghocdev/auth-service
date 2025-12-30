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
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String fullName;
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;

    @Column(columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE / INACTIVE

    @Column(name = "auth_provider", nullable = false)
    private String authProvider = "LOCAL"; // LOCAL / GOOGLE

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) return Collections.emptyList();

        return roles.stream()
                .map(role -> {
                    String name = role.getName().toUpperCase();
                    if (!name.startsWith("ROLE_")) name = "ROLE_" + name;
                    return new SimpleGrantedAuthority(name);
                })
                .collect(Collectors.toList());
    }

    @Override @JsonIgnore public String getPassword() { return this.passwordHash; }
    @Override public String getUsername() { return this.username; }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return isEnabled(); }
    @Override public boolean isCredentialsNonExpired() { return true; }

    public boolean verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (rawPassword == null || this.passwordHash == null) return false;
        return passwordEncoder.matches(rawPassword, this.passwordHash);
    }
}