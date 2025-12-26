-- 1. Đổ dữ liệu Thương hiệu
INSERT INTO brands (name) VALUES
('Apple'), ('Dell'), ('HP'), ('Lenovo'), ('Asus'), ('Acer'), ('MSI'), ('Razer');

-- 2. Đổ dữ liệu Laptop mẫu (Chỉ điền các cột đã có ở V1)
INSERT INTO laptops (name, price, description, is_deleted, brand_id) VALUES
('MacBook Air M2 2023', 999.0, 'Chip M2, RAM 8GB, SSD 256GB', 0, 1),
('Dell XPS 13 Plus', 1399.0, 'Thiết kế tương lai, màn hình OLED', 0, 2),
('Lenovo Legion 5 Pro', 1450.0, 'RTX 4060, Màn hình 2K 165Hz', 0, 4),
('Asus ROG Zephyrus G14', 1699.0, 'Gaming 14 inch mạnh nhất', 0, 5);