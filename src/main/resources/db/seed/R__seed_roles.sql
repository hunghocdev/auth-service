-- Repeatable migration: Cập nhật danh sách quyền hệ thống
-- Flyway sẽ chạy lại file này nếu bạn thêm quyền mới vào đây

INSERT INTO roles (name, description) VALUES
('ROLE_USER', 'Người dùng thông thường - chỉ xem dữ liệu cá nhân'),
('ROLE_STAFF', 'Nhân viên vận hành - hỗ trợ xử lý đơn hàng/sản phẩm'),
('ROLE_SUPPORT', 'Nhân viên hỗ trợ khách hàng'),
('ROLE_MANAGER', 'Quản lý cửa hàng - toàn quyền với sản phẩm và thương hiệu'),
('ROLE_ADMIN', 'Quản trị viên hệ thống - toàn quyền quản lý User và hệ thống')
ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;