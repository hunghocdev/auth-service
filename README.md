# 🔐 Spring Boot JWT Authentication Demo (RS256 & Token Rotation)
Dự án triển khai một hệ thống **Xác thực (Authentication)** an toàn và hiện đại sử dụng **Spring Boot 3** và **JSON Web Tokens (JWT)**. Hệ thống được thiết kế theo kiến trúc **Feature-based Packaging** để tối ưu hóa khả năng bảo trì và mở rộng, đồng thời tích hợp các cơ chế bảo mật nâng cao như **ký token bằng RSA (RS256)** và **Token Rotation**.

---
## 🚀 Tính Năng Nổi Bật
* **Modular Architecture:** Tổ chức mã nguồn rõ ràng theo tính năng chính (**auth**, **user**, **token**), giúp dễ dàng quản lý và mở rộng.
* **RSA Security (RS256):** Sử dụng thuật toán bất đối xứng **RSA (RS256)** để ký và xác thực token, tăng cường bảo mật so với các thuật toán đối xứng (HMAC).
* **Token Rotation:** Cơ chế **Refresh Token** an toàn, giúp tự động cấp lại **Access Token** mới và **thu hồi token cũ** ngay sau khi sử dụng (One-Time-Use Refresh Tokens), giảm thiểu rủi ro bị đánh cắp token.
* **User Management:** Các API cơ bản để quản lý thông tin và cấu hình người dùng.
* **Interactive API Docs:** Tích hợp Swagger UI, cho phép xem cấu trúc API và kiểm thử trực tiếp trên giao diện web.
* **Database Storage:** Lưu trữ trạng thái người dùng (**User**) và trạng thái **Refresh Token** trong cơ sở dữ liệu **MySQL** thông qua **Spring Data JPA**.
* **Advanced Search:** Tìm kiếm và lọc sản phẩm linh hoạt với JpaSpecificationExecutor.
---
## 🛠️ Tech Stack

| Thành phần | Phiên bản/Công nghệ                 | Mục đích |
| :--- |:------------------------------------| :--- |
| **Core** | Java 17, Spring Boot 3.2+           | Nền tảng phát triển ứng dụng Microservice/REST API. |
| **Security** | Spring Security 6, Java-JWT (Auth0) | Quản lý xác thực, ủy quyền và xử lý JWT. |
| **API Docs** | SpringDoc OpenAPI 2.8.3             | Tự động tạo tài liệu API và giao diện Swagger UI. |
| **Data** | MySQL, Spring Data JPA              | Lưu trữ dữ liệu và thao tác với Database. |
| **Build Tool** | Maven                               | Quản lý dependencies và build project. |
| **Tooling** | OpenSSL                             | Khởi tạo cặp khóa RSA. |

---
## 📡 Tài Liệu API (Swagger UI)
Hệ thống tích hợp sẵn giao diện Swagger UI để hỗ trợ lập trình viên Frontend và Tester.
- Đường dẫn truy cập: http://localhost:8080/swagger-ui/index.html
- Định nghĩa API (JSON): http://localhost:8080/v3/api-docs
### Hướng dẫn kiểm thử API có bảo mật trên Swagger:
1. Truy cập API POST /api/auth/login, thực hiện đăng nhập để nhận chuỗi accessToken.
2. Nhấn nút Authorize (biểu tượng ổ khóa màu xanh) ở phía trên cùng bên phải giao diện Swagger.
3. Dán chuỗi Token vào ô Value (hệ thống đã cấu hình tự động thêm tiền tố "Bearer ").
4. Nhấn Authorize -> Close.
5. Giờ đây, bạn có thể gọi các API yêu cầu đăng nhập như /api/laptops bằng nút Try it out.
## ⚙️ Cài Đặt & Khởi Chạy

### 1. Yêu Cầu Tiên Quyết

* **JDK 17** hoặc mới hơn.
* **Maven** (3.6+).
* **MySQL Server** đang hoạt động.
* **Cặp khóa RSA** trong thư mục secrets/ (đã được cấu hình trong application.properties).

### 2. Cấu Hình Dependency (pom.xml)
Đảm bảo bạn sử dụng phiên bản tương thích với Spring Boot 3.4:

````
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.3</version>
</dependency>
````
### 3. Khởi Chạy
````
mvn clean spring-boot:run
````

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

### 📂 Cấu Trúc Dự Án
````
com.example.authdemo
│
├── config                 # Cấu hình (Security, OpenApiConfig, Auditing)
├── security               # JWT Filter & JwtService (RSA Logic)
├── common                 # BaseEntity, GlobalExceptionHandler, DTOs
│
└── module                 # NGHIỆP VỤ THEO TÍNH NĂNG
├── auth               # API Login, Register, Refresh Token
├── user               # Quản lý người dùng & Profile
├── token              # Quản lý vòng đời Refresh Token
└── product            # Quản lý Laptop & Brand
├── controller     # Chứa các Swagger Annotations (@Operation, @Tag)
├── service
├── repository     # Specification Search
└── model          # Entities (Laptop, Brand)
````


### ⚠️ Lưu Ý Bảo Mật
- Thư mục **secrets/** chứa khóa Private Key tuyệt đối không được đưa lên Git.
- Trên môi trường Production, nên tắt Swagger UI bằng cấu hình: springdoc.api-docs.enabled=false.