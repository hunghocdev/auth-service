<<<<<<< HEAD
-- 1. Bảng Brands (Thương hiệu)
=======
-- 1. Brands
>>>>>>> c7be2dfa18121082554757b7ba29548c44077b8f
CREATE TABLE brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL
);

<<<<<<< HEAD
-- 2. Bảng Laptops
=======
-- 2. Laptops
>>>>>>> c7be2dfa18121082554757b7ba29548c44077b8f
CREATE TABLE laptops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE,
    description TEXT,
    is_deleted BIT(1) DEFAULT 0,
    brand_id BIGINT,
    CONSTRAINT fk_laptops_brands FOREIGN KEY (brand_id) REFERENCES brands(id)
);

<<<<<<< HEAD
-- 3. Bảng Users (Người dùng)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
=======
-- 3. Users (Phiên bản đầy đủ nhất)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
>>>>>>> c7be2dfa18121082554757b7ba29548c44077b8f
    full_name VARCHAR(255),
    phone_number VARCHAR(15),
    address VARCHAR(255),
    date_of_birth DATE,
    sex VARCHAR(10),
<<<<<<< HEAD
    avatar_url VARCHAR(255)
);

-- 4. Bảng Refresh Tokens
CREATE TABLE refresh_tokens (
=======
    avatar_url VARCHAR(255),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- 4. Refresh Tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
>>>>>>> c7be2dfa18121082554757b7ba29548c44077b8f
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    expires_at DATETIME(6),
    revoked BIT(1) NOT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);