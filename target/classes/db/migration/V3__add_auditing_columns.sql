<<<<<<< HEAD
-- Thêm cột auditing cho bảng laptops để lưu vết người tạo/sửa
=======
-- Thêm cột auditing cho bảng laptops
>>>>>>> c7be2dfa18121082554757b7ba29548c44077b8f
ALTER TABLE laptops
ADD COLUMN created_at DATETIME(6),
ADD COLUMN updated_at DATETIME(6),
ADD COLUMN created_by VARCHAR(255),
ADD COLUMN updated_by VARCHAR(255);

<<<<<<< HEAD
-- Cập nhật dữ liệu cũ để tránh lỗi NULL
=======
-- Cập nhật dữ liệu cũ (để không bị null)
>>>>>>> c7be2dfa18121082554757b7ba29548c44077b8f
UPDATE laptops SET created_at = NOW(), created_by = 'system';