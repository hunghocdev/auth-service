# 🔐 Spring Boot JWT Authentication Demo

Dự án mẫu triển khai hệ thống xác thực (Authentication) sử dụng **Spring Boot 3** và **JWT (JSON Web Token)** với cơ chế Access Token & Refresh Token.

## 🚀 Tính năng chính
- **Đăng ký (Register):** Tạo tài khoản mới, mã hóa mật khẩu với BCrypt.
- **Đăng nhập (Login):** Cấp Access Token (ngắn hạn) và Refresh Token (dài hạn).
- **Refresh Token Rotation:** Cơ chế đổi Refresh Token cũ lấy mới để tăng cường bảo mật.
- **RSA Encryption:** Sử dụng cặp khóa RSA (Public/Private) để ký và xác thực JWT.
- **Database:** Lưu trữ User và Refresh Token Hash trong MySQL.

## 🛠️ Công nghệ sử dụng
- Java 17
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- MySQL
- JWT (Java-JWT Library)

## ⚙️ Cài đặt và Chạy ứng dụng

### 1. Yêu cầu (Prerequisites)
- JDK 17+
- Maven
- MySQL Server
- OpenSSL (hoặc Git Bash để tạo key)

### 2. Cấu hình Database
Tạo database trống trong MySQL:
```sql
CREATE DATABASE authdemo_db;
```
Cập nhật file `src/main/resources/application.properties` (nếu cần):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/authdemo_db
spring.datasource.username=<YOUR_DB_USERNAME>
spring.datasource.password=<YOUR_DB_PASSWORD>
```

### 3. Tạo RSA Keys (Quan trọng ⚠️)
Vì lý do bảo mật, các khóa bí mật không được đưa lên GitHub. Bạn cần tự tạo chúng:

1. Tạo thư mục `secrets` tại thư mục gốc của dự án.
2. Mở Terminal tại thư mục `secrets` và chạy lệnh:

```bash
# Tạo Private Key
openssl genpkey -algorithm RSA -out jwt_private.pem -pkeyopt rsa_keygen_bits:2048

# Tạo Public Key từ Private Key
openssl rsa -pubout -in jwt_private.pem -out jwt_public.pem
```

Cấu trúc thư mục sau khi tạo sẽ như sau:
```
authdemo/
├── src/
├── target/
├── secrets/          <-- Thư mục này (chứa .pem) bị git ignore
│   ├── jwt_private.pem
│   └── jwt_public.pem
├── pom.xml
└── ...
```

### 4. Chạy ứng dụng
```bash
mvn spring-boot:run
```
Server sẽ khởi động tại: `http://localhost:8080`

---

## 📡 API Documentation

### 1. Đăng ký (Register)
- **Endpoint:** `POST /api/auth/register`
- **Body:**
```json
{
  "username": "user01",
  "email": "user01@example.com",
  "password": "password123"
}
```

### 2. Đăng nhập (Login)
- **Endpoint:** `POST /api/auth/login`
- **Body:**
```json
{
  "username": "user01",
  "password": "password123"
}
```
- **Response:** Trả về `accessToken` và `refreshToken`.

### 3. Làm mới Token (Refresh Token)
- **Endpoint:** `POST /api/auth/refresh`
- **Body:**
```json
{
  "refreshToken": "<YOUR_REFRESH_TOKEN_HERE>"
}
```

## 🛡️ Lưu ý bảo mật
- File `.gitignore` đã được cấu hình để loại bỏ thư mục `secrets/`.
- Không bao giờ commit file `jwt_private.pem` lên Version Control.

## 📄 License
MIT License