package com.example.authdemo.token;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
    void deleteByUserIdAndRevokedTrue(Long userId); // optional housekeeping

    // Find all non-revoked refresh tokens for a specific user (useful for revocation/rotation)
    java.util.List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);
}
