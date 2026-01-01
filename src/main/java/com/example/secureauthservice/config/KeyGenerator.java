package com.example.secureauthservice.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        try {
            // 1. Tạo cặp khóa RSA 2048 bit
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            // 2. Lưu Private Key
            saveKey("secrets/jwt_private.pem", "PRIVATE KEY", privateKey.getEncoded());

            // 3. Lưu Public Key
            saveKey("secrets/jwt_public.pem", "PUBLIC KEY", publicKey.getEncoded());

            System.out.println("✅ Đã tạo xong 2 file key trong thư mục secrets!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveKey(String fileName, String header, byte[] keyBytes) throws IOException {
        String type = "-----BEGIN " + header + "-----\n";
        String end = "\n-----END " + header + "-----";

        // Base64 MimeEncoder sẽ tự động xuống dòng mỗi 76 ký tự cho đúng chuẩn PEM
        String encoded = Base64.getMimeEncoder().encodeToString(keyBytes);

        String pemContent = type + encoded + end;

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(pemContent.getBytes());
        }
    }
}