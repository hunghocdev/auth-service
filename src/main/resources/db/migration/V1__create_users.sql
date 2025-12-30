-- =============================================
-- V1: KHỞI TẠO BẢNG USERS VÀ TRIGGER CẬP NHẬT THỜI GIAN
-- =============================================

-- 1. Tạo hàm dùng chung để tự động cập nhật updated_at (Postgres không có sẵn như MySQL)
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 2. Tạo bảng Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,

    full_name VARCHAR(255),
    phone_number VARCHAR(20),
    address TEXT,
    date_of_birth DATE,
    gender VARCHAR(20),
    avatar_url TEXT,

    -- Trạng thái tài khoản (ACTIVE / INACTIVE)
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Nguồn xác thực
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Áp dụng Trigger cho bảng users
CREATE TRIGGER update_users_modtime
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();