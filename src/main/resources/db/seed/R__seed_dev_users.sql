-- =============================================
-- S2: ĐỔ DỮ LIỆU NGƯỜI DÙNG KIỂM THỬ (DEV USERS)
-- TẤT CẢ MẬT KHẨU LÀ: 123456
-- BCrypt: $2a$10$7vNfV8WJ0LhQx2XjW6mSxe9/X9kPzV5n5t8zRzV1jI1s3Z9Y/7t6S
-- =============================================

-- 1. Chèn danh sách User với đầy đủ thông tin chi tiết
-- Chuyển từ DO NOTHING sang DO UPDATE để cập nhật thông tin khi file thay đổi
INSERT INTO users (username, email, password_hash, full_name, phone_number, address, date_of_birth, gender, status, auth_provider) VALUES
('admin', 'admin@authdemo.com', '$2a$10$7vNfV8WJ0LhQx2XjW6mSxe9/X9kPzV5n5t8zRzV1jI1s3Z9Y/7t6S', 'Hệ Thống Admin', '0901234567', '123 Đường Admin, Quận 1, TP.HCM', '1990-01-01', 'MALE', 'ACTIVE', 'LOCAL'),
('manager_01', 'manager1@authdemo.com', '$2a$10$7vNfV8WJ0LhQx2XjW6mSxe9/X9kPzV5n5t8zRzV1jI1s3Z9Y/7t6S', 'Quản Lý Cửa Hàng A', '0912345678', '456 Đại Lộ Manager, Quận 7, TP.HCM', '1992-05-15', 'FEMALE', 'ACTIVE', 'LOCAL'),
('staff_01', 'staff1@authdemo.com', '$2a$10$7vNfV8WJ0LhQx2XjW6mSxe9/X9kPzV5n5t8zRzV1jI1s3Z9Y/7t6S', 'Nhân Viên Bán Hàng', '0923456789', '789 Đường Staff, Quận Bình Thạnh, TP.HCM', '1995-10-20', 'MALE', 'ACTIVE', 'LOCAL'),
('support_01', 'support1@authdemo.com', '$2a$10$7vNfV8WJ0LhQx2XjW6mSxe9/X9kPzV5n5t8zRzV1jI1s3Z9Y/7t6S', 'Hỗ Trợ Kỹ Thuật', '0934567890', '101 Đường Support, Quận Cầu Giấy, Hà Nội', '1998-12-12', 'OTHER', 'ACTIVE', 'LOCAL'),
('user_test', 'user@authdemo.com', '$2a$10$7vNfV8WJ0LhQx2XjW6mSxe9/X9kPzV5n5t8zRzV1jI1s3Z9Y/7t6S', 'Khách Hàng Thân Thiết', '0945678901', '202 Đường User, Quận Ba Đình, Hà Nội', '2000-07-07', 'FEMALE', 'ACTIVE', 'LOCAL')
ON CONFLICT (username) DO UPDATE SET
    email = EXCLUDED.email,
    password_hash = EXCLUDED.password_hash,
    full_name = EXCLUDED.full_name,
    phone_number = EXCLUDED.phone_number,
    address = EXCLUDED.address,
    date_of_birth = EXCLUDED.date_of_birth,
    gender = EXCLUDED.gender,
    status = EXCLUDED.status,
    updated_at = now();

-- 2. Gán quyền tương ứng (Logic: Admin có tất cả, Manager có User+Manager, v.v.)
-- Sử dụng ON CONFLICT DO NOTHING cho bảng trung gian là hợp lý vì không có thông tin gì để update thêm
-- ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name IN ('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;

-- MANAGER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'manager_01' AND r.name IN ('ROLE_USER', 'ROLE_MANAGER')
ON CONFLICT DO NOTHING;

-- STAFF
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'staff_01' AND r.name IN ('ROLE_USER', 'ROLE_STAFF')
ON CONFLICT DO NOTHING;

-- SUPPORT
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'support_01' AND r.name IN ('ROLE_USER', 'ROLE_SUPPORT')
ON CONFLICT DO NOTHING;

-- NORMAL USER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'user_test' AND r.name = 'ROLE_USER'
ON CONFLICT DO NOTHING;