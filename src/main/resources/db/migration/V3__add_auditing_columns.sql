-- Thêm các cột auditing vào bảng laptops sau khi bảng đã có dữ liệu
ALTER TABLE laptops
ADD COLUMN created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
ADD COLUMN updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
ADD COLUMN created_by VARCHAR(255) DEFAULT 'system',
ADD COLUMN updated_by VARCHAR(255);

-- Cập nhật dữ liệu auditing cho các bản ghi cũ từ V2
UPDATE laptops SET created_at = NOW(), created_by = 'system' WHERE created_by IS NULL;