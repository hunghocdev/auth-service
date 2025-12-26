-- 1. Bảng Brands (Thương hiệu)
CREATE TABLE IF NOT EXISTS brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 2. Bảng Laptops (Sản phẩm - Chỉ chứa các cột cơ bản)
-- Lưu ý: KHÔNG khai báo các cột created_at, updated_at ở đây vì V3 sẽ làm việc đó
CREATE TABLE IF NOT EXISTS laptops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    description TEXT,
    is_deleted BIT(1) DEFAULT 0,
    CONSTRAINT fk_laptops_brands FOREIGN KEY (brand_id) REFERENCES brands(id)
);

-- 3. Bảng Users (Người dùng)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    phone_number VARCHAR(15),
    address VARCHAR(255),
    date_of_birth DATE,
    sex VARCHAR(10),
    avatar_url VARCHAR(255),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
);

-- 4. Bảng Refresh Tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    expires_at DATETIME(6),
    revoked BIT(1) NOT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);