package com.example.authdemo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.security.*;
import java.time.Instant;
import java.util.Base64; // Thêm import Base64
import java.util.Date;
import java.util.UUID;

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
        // Log đường dẫn để kiểm tra chính xác
        System.out.println("Loading Private Key from: " + Path.of(privateKeyFile).toAbsolutePath());
        System.out.println("Loading Public Key from: " + Path.of(publicKeyFile).toAbsolutePath());

        this.privateKey = (RSAPrivateKey) readPrivateKey(privateKeyFile);
        this.publicKey = (RSAPublicKey) readPublicKey(publicKeyFile);
        this.algorithm = Algorithm.RSA256(publicKey, privateKey);
        this.accessTtlMs = accessTtlMs;
        this.refreshTtlMs = refreshTtlMs;
        this.issuer = issuer;
    }

    // create access token; include minimal claims: sub=userId, jti=random, exp, iat, iss
//    public String createAccessToken(Long userId) {
//        Instant now = Instant.now();
//        return JWT.create()
//                .withIssuer(issuer)
//                .withSubject(userId.toString())
//                .withIssuedAt(Date.from(now))
//                .withExpiresAt(Date.from(now.plusMillis(accessTtlMs)))
//                .withJWTId(UUID.randomUUID().toString())
//                .sign(algorithm);
//    }

    public String createAccessToken(String username) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(username) // Đã đổi thành username (String)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(accessTtlMs)))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm); // Sử dụng biến algorithm (RSA) đã khởi tạo trong constructor
    }

    // create refresh token string (opaque) — you may still sign with JWT or use UUID. We'll use random UUID
    public String createRefreshTokenString() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

    public DecodedJWT verifyAccessToken(String token) {
        return JWT.require(algorithm).withIssuer(issuer).build().verify(token);
    }

    // read keys from PEM files (PKCS#8 private, X.509 public)
    private PrivateKey readPrivateKey(String pemPath) throws Exception {
        String pem = Files.readString(Path.of(pemPath));

        // Loại bỏ các header/footer phổ biến (PKCS#8 hoặc RSA cũ)
        pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "");

        // Loại bỏ tất cả khoảng trắng, xuống dòng, v.v. để có chuỗi Base64 thuần
        pem = pem.replaceAll("\\s","");

        byte[] der = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey readPublicKey(String pemPath) throws Exception {
        String pem = Files.readString(Path.of(pemPath));

        // Loại bỏ các header/footer phổ biến (X.509)
        pem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", ""); // Bao gồm cả RSA Public Key

        // Loại bỏ tất cả khoảng trắng, xuống dòng, v.v.
        pem = pem.replaceAll("\\s","");

        byte[] der = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    // Add inside JwtService class
    public long getRefreshTtlMs() {
        return refreshTtlMs;
    }
}