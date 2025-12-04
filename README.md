# 🔐 Spring Boot JWT Authentication Demo (RS256 & Token Rotation)
Dự án triển khai một hệ thống **Xác thực (Authentication)** an toàn và hiện đại sử dụng **Spring Boot 3** và **JSON Web Tokens (JWT)**. Hệ thống được thiết kế theo kiến trúc **Feature-based Packaging** để tối ưu hóa khả năng bảo trì và mở rộng, đồng thời tích hợp các cơ chế bảo mật nâng cao như **ký token bằng RSA (RS256)** và **Token Rotation**.

---
## 🚀 Tính Năng Nổi Bật
* **Modular Architecture:** Tổ chức mã nguồn rõ ràng theo tính năng chính (**auth**, **user**, **token**), giúp dễ dàng quản lý và mở rộng.
* **RSA Security (RS256):** Sử dụng thuật toán bất đối xứng **RSA (RS256)** để ký và xác thực token, tăng cường bảo mật so với các thuật toán đối xứng (HMAC).
* **Token Rotation:** Cơ chế **Refresh Token** an toàn, giúp tự động cấp lại **Access Token** mới và **thu hồi token cũ** ngay sau khi sử dụng (One-Time-Use Refresh Tokens), giảm thiểu rủi ro bị đánh cắp token.
* **User Management:** Các API cơ bản để quản lý thông tin và cấu hình người dùng.
* **Database Storage:** Lưu trữ trạng thái người dùng (**User**) và trạng thái **Refresh Token** trong cơ sở dữ liệu **MySQL** thông qua **Spring Data JPA**.
---
## 🛠️ Tech Stack

| Thành phần | Phiên bản/Công nghệ | Mục đích |
| :--- | :--- | :--- |
| **Core** | Java 17, Spring Boot 3.2+ | Nền tảng phát triển ứng dụng Microservice/REST API. |
| **Security** | Spring Security 6, Java-JWT (Auth0) | Quản lý xác thực, ủy quyền và xử lý JWT. |
| **Data** | MySQL, Spring Data JPA | Lưu trữ dữ liệu và thao tác với Database. |
| **Build Tool** | Maven | Quản lý dependencies và build project. |
| **Tooling** | OpenSSL | Khởi tạo cặp khóa RSA. |

---
## ⚙️ Cài Đặt & Khởi Chạy

### 1. Yêu Cầu Tiên Quyết

* **JDK 17** hoặc mới hơn.
* **Maven** (3.6+).
* **MySQL Server** đang hoạt động.

### 2. Cấu Hình Database

Tạo Database và cập nhật thông tin kết nối trong file `src/main/resources/application.properties` (hoặc `application.yml`):

sql
CREATE DATABASE authdemo_db;

 **Lưu ý:** Cập nhật thông tin spring.datasource.url, spring.datasource.username, và spring.datasource.password trong file cấu hình.

### 3. Trích xuất Public Key từ Private Key
openssl rsa -pubout -in jwt_private.pem -out jwt_public.pem

### Quay lại thư mục gốc
cd ..
**⚠️ Bảo mật:** Thư mục secrets/ và các file .pem đã được cấu hình trong .gitignore và KHÔNG được commit lên Version Control (Git).

## 4. Khởi Chạy Ứng Dụng
Sử dụng Maven để chạy ứng dụng:Bashmvn spring-boot:run
Ứng dụng sẽ khả dụng tại: http://localhost:8080 
## 📡API Endpoints

🔑 Auth Module

**1. Đăng ký**

POST /api/auth/register
```
Body:
{
"username": "user01",
"email": "user01@example.com",
"password": "password123"
}
```
**2. Đăng nhập**

POST /api/auth/login
```
Body:
{
"username": "user01",
"password": "password123"
}
```
```
Response:
{
"accessToken": "...",
"refreshToken": "..."
}
```
**3. Refresh Token (Rotation)**

POST /api/auth/refresh
```
Body:
{
"refreshToken": "<YOUR_REFRESH_TOKEN_HERE>"
}
```

**👤 User Module**

**4. Cập nhật thông tin**

PUT /api/auth/update-profile

```
Header:
Authorization: Bearer <access_token>
```
```│
├── config                 # Cấu hình chung (Security, OpenAPI, CORS)
├── security               # Cấu trúc JWT (Filter, Handler, Provider)
├── common                 # Các tiện ích, ngoại lệ (Utils, Exceptions)
│
└── module                 # Logic nghiệp vụ được đóng gói theo tính năng
├── auth               # Module Xác thực (Login, Register, Token Refresh)
│   ├── dto            # Data Transfer Objects (Request/Response)
│   └── ...            # Controller, Service, Repository
├── user               # Module Quản lý Người dùng (Profile Management)
│   ├── entity         # User Entity
│   └── ...
└── token              # Module Quản lý Refresh Token
├── entity         # RefreshToken Entity (Lưu trạng thái)
└── ...
```