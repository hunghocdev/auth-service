-- Thêm cột auditing cho bảng laptops để lưu vết người tạo/sửa
ALTER TABLE laptops
ADD COLUMN created_at DATETIME(6),
ADD COLUMN updated_at DATETIME(6),
ADD COLUMN created_by VARCHAR(255),
ADD COLUMN updated_by VARCHAR(255);

-- Cập nhật dữ liệu cũ để tránh lỗi NULL
UPDATE laptops SET created_at = NOW(), created_by = 'system';