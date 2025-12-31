## 🚀 AuthDemo Project - Secure Authentication System
Dự án AuthDemo là một hệ thống quản lý xác thực và phân quyền chuẩn doanh nghiệp, tập trung vào tính bảo mật cao, khả năng mở rộng và trải nghiệm 
lập trình viên tốt nhất.**RSA Security (RS256):** Sử dụng thuật toán bất đối xứng **RSA (RS256)** để ký và xác thực token, tăng cường bảo mật so 
với các thuật toán đối xứng (HMAC).
## ✨ Tính Năng Nổi Bật
* **Google Authentication (OAuth2):** Tích hợp đăng nhập bằng Google. Tự động định danh người dùng qua mã sub duy nhất, hỗ trợ tự động đăng ký tài khoản mới và gán quyền mặc định.
* **RSA Security (RS256):** Sử dụng thuật toán bất đối xứng RSA (RS256) để ký và xác thực token, tăng cường bảo mật so với các thuật toán đối xứng (HMAC).
* **Token Rotation:** Cơ chế Refresh Token an toàn, giúp tự động cấp lại Access Token mới và thu hồi token cũ ngay sau khi sử dụng (One-Time-Use Refresh Tokens), giảm thiểu rủi ro bị đánh cắp token.
* **Role-Based Access Control (RBAC):** Phân quyền 3 cấp độ (USER, MANAGER, ADMIN) với cơ chế nạp quyền trực tiếp từ Database cho mỗi yêu cầu.
* **User Management:** Các API cơ bản để quản lý thông tin, lấy thông tin cá nhân (/me) và cập nhật cấu hình người dùng.
* **Interactive API Docs:** Tích hợp Swagger UI, cho phép xem cấu trúc API và kiểm thử trực tiếp trên giao diện web với nút Authorize hỗ trợ JWT.
* **Database Storage:** Lưu trữ trạng thái người dùng (User) và trạng thái Refresh Token trong cơ sở dữ liệu MySQL thông qua Spring Data JPA.
* **Advanced Search:** Tìm kiếm và lọc sản phẩm linh hoạt với JpaSpecificationExecutor.
* **Database Migration:** Quản lý phiên bản cấu trúc dữ liệu tự động bằng Flyway (V1 -> V5).
---
## 🛠️ Tech Stack

| Thành phần | Phiên bản/Công nghệ                        | Mục đích |
| :--- |:-------------------------------------------| :--- |
| **Core** | Java 17, Spring Boot 3.4.x                 | Nền tảng hiện đại, hiệu năng cao. |
| **Security** | Spring Security 6, Java-JWT, OAuth2 Client | Quản lý xác thực, ủy quyền RBAC và xử lý JWT/Social Login. |
| **Database** | PostgreSQL 15                              | Hệ quản trị dữ liệu quan hệ mạnh mẽ. |
| **Migration** | Flyway                                     | Quản lý phiên bản Database. |
| **Container** | Docker & Docker Compose                    | Đóng gói và triển khai đồng nhất mọi môi trường. |
| **API Docs** | SpringDoc OpenAPI 2.8.3                    | Tự động tạo tài liệu API và giao diện Swagger UI. |
---
## 📡 Tài Liệu API (Swagger UI)
Hệ thống tích hợp sẵn giao diện Swagger UI để hỗ trợ lập trình viên Frontend và Tester.
- Đường dẫn truy cập: http://localhost:8080/swagger-ui/index.html
- Định nghĩa API (JSON): http://localhost:8080/v3/api-docs
- Đường dẫn đăng nhập Auth google: http://localhost:8080/oauth2/authorization/google.
### Hướng dẫn kiểm thử API có bảo mật trên Swagger:
1. Truy cập API `POST /api/auth/login`, thực hiện đăng nhập để nhận chuỗi **accessToken**.
2. Nhấn nút **Authorize** (biểu tượng ổ khóa màu xanh) ở phía trên cùng bên phải giao diện Swagger.
3. Dán chuỗi Token vào ô **Value** (hệ thống đã cấu hình tự động xử lý tiền tố Bearer).
4. Nhấn **Authorize** -> **Close**.
5. Giờ đây, bạn có thể gọi các API yêu cầu đăng nhập như `/api/auth/me` hoặc `/api/laptops`.
## ⚙️ Cài Đặt & Khởi Chạy
### 1. Yêu Cầu Tiên Quyết
* **JDK 17** hoặc mới hơn.
* **Maven** (3.6+).
* **MySQL Server** đang hoạt động.
* **Cặp khóa RSA** trong thư mục `secrets/` (đã được cấu hình trong `application.properties`).
### 2. Cấu Hình OAuth2 (Google)
Bạn cần điền thông tin Client ID và Client Secret thực tế vào application.properties:
```
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```
### 3. Triển khai nhanh với Docker (Khuyên dùng)
Dự án được cấu hình sử dụng các cổng mặc định tiêu chuẩn để đảm bảo tính đồng nhất và dễ dàng kiểm thử.
* **Bước 1:** Đảm bảo thư mục secrets/ có chứa jwt_private.pem và jwt_public.pem.
* **Bước 2:** Chạy lệnh triển khai duy nhất:
````
    docker compose up --build -d
````
* **Bước 3:** Truy cập hệ thống:
  - **Swagger UI:** http://localhost:8080/swagger-ui.html
  - **Database (DBeaver):** Kết nối qua cổng mặc định **5432**.

(Lưu ý: Nếu máy của bạn đang chạy sẵn một dịch vụ PostgreSQL khác ở cổng 5432, hãy tạm dừng dịch vụ đó trước khi chạy Docker để tránh xung đột).
### 4. Chạy thủ công (Maven)
Đảm bảo bạn đã cấu hình đúng thông tin kết nối Database trong `application.properties`.
````
mvn clean spring-boot:run
````
## 🔑 Auth & User Module
| Method |Endpoint | Quyền hạn | Mô tả | 
| :--- |:-----|:----------| :--- |
|POST|/api/auth/register|Public|Đăng ký tài khoản hệ thống.|
|POST|/api/auth/login|Public|Đăng nhập lấy cặp Token.|
|GET|/oauth2/authorization/google|Public|Khởi chạy luồng đăng nhập bằng Google.|
|GET|/api/auth/me|ROLE_USER|Lấy thông tin chi tiết người dùng hiện tại.|
|PUT|/api/auth/update-profile|ROLE_USER|Cập nhật thông tin Profile (Họ tên, ngày sinh...).|

### 📂 Cấu Trúc Dự Án
````
com.example.authdemo
│
├── config                 # Cấu hình (Security, OpenApiConfig, Auditing)
├── security               # JWT Filter, JwtService, OAuth2 Handlers
├── common                 # BaseEntity, GlobalExceptionHandler, DTOs
└── module                 # NGHIỆP VỤ THEO TÍNH NĂNG
    ├── auth               # DTOs & Controller cho Login/Register
    ├── user               # Entity User (Duy nhất), Role, Repositories, Service
    ├── token              # Quản lý Refresh Token & TokenUtil
    └── product            # Quản lý Laptop, Brand & Specification Search
        ├── controller     # Chứa các Swagger Annotations (@Operation, @Tag)
        ├── service
        ├── repository     # Specification Search
        └── model          # Entities (Laptop, Brand)
````


### ⚠️ Lưu Ý Bảo Mật
- Thư mục secrets/ chứa khóa Private Key tuyệt đối KHÔNG được commit lên Git (Đã cấu hình trong .gitignore).
- Mật khẩu trong DB được mã hóa bằng BCrypt.
- Trên môi trường Production, hãy tắt Swagger UI bằng cách cấu hình springdoc.api-docs.enabled=false.