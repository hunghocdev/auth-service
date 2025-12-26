package com.example.authdemo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.security.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final Algorithm algorithm;
    private final long accessTtlMs;
    private final long refreshTtlMs;
    private final String issuer;

    public JwtService(
            @Value("${jwt.private-key-file}") String privateKeyFile,
            @Value("${jwt.public-key-file}") String publicKeyFile,
            @Value("${jwt.access-token-expiration}") long accessTtlMs,
            @Value("${jwt.refresh-token-expiration}") long refreshTtlMs,
            @Value("${jwt.issuer}") String issuer
    ) throws Exception {
        this.privateKey = (RSAPrivateKey) readPrivateKey(privateKeyFile);
        this.publicKey = (RSAPublicKey) readPublicKey(publicKeyFile);
        this.algorithm = Algorithm.RSA256(publicKey, privateKey);
        this.accessTtlMs = accessTtlMs;
        this.refreshTtlMs = refreshTtlMs;
        this.issuer = issuer;
    }

    // CẬP NHẬT: Nhận UserDetails để lấy danh sách Authorities (Roles)
    public String createAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();

        // Lấy danh sách Role chuyển thành List String
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(userDetails.getUsername())
                .withClaim("roles", roles) // Đưa Role vào Claim của JWT
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(accessTtlMs)))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    public String createRefreshTokenString() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

    public DecodedJWT verifyAccessToken(String token) {
        return JWT.require(algorithm).withIssuer(issuer).build().verify(token);
    }

    private PrivateKey readPrivateKey(String pemPath) throws Exception {
        String pem = Files.readString(Path.of(pemPath));
        pem = pem.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s","");
        byte[] der = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey readPublicKey(String pemPath) throws Exception {
        String pem = Files.readString(Path.of(pemPath));
        pem = pem.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "").replace("-----END RSA PUBLIC KEY-----", "")
                .replaceAll("\\s","");
        byte[] der = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public long getRefreshTtlMs() {
        return refreshTtlMs;
    }
}