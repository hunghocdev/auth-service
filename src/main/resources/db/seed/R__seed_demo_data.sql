-- =============================================
-- S3: ĐỔ DỮ LIỆU BRANDS VÀ LAPTOPS MẪU DIỆN RỘNG
-- =============================================

-- 1. Chèn thêm nhiều thương hiệu
INSERT INTO brands (name) VALUES
('Apple'), ('Dell'), ('HP'), ('Lenovo'), ('Asus'), ('Acer'), ('MSI'), ('Gigabyte'), ('Razer'), ('Microsoft')
ON CONFLICT (name) DO NOTHING;

-- 2. Chèn danh sách Laptop đa phân khúc
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'MacBook Pro M3 Max 16-inch', 3499.00, 'Apple M3 Max, 36GB RAM, 1TB SSD, Space Black', u.id
FROM brands b, users u WHERE b.name = 'Apple' AND u.username = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'MacBook Air M2 13-inch', 1099.00, 'Siêu mỏng nhẹ, pin 18 tiếng', u.id
FROM brands b, users u WHERE b.name = 'Apple' AND u.username = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Dell XPS 13 9315', 1250.00, 'Laptop doanh nhân cao cấp, màn hình vô cực', u.id
FROM brands b, users u WHERE b.name = 'Dell' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Dell Precision 5570', 2100.00, 'Máy trạm chuyên đồ họa kỹ thuật', u.id
FROM brands b, users u WHERE b.name = 'Dell' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Lenovo Legion 5 Pro Gen 8', 1599.00, 'Ryzen 7 7745HX, RTX 4070, 16GB DDR5', u.id
FROM brands b, users u WHERE b.name = 'Lenovo' AND u.username = 'staff_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Lenovo ThinkPad X1 Carbon Gen 11', 1850.00, 'Huyền thoại bàn phím, siêu bền bỉ', u.id
FROM brands b, users u WHERE b.name = 'Lenovo' AND u.username = 'staff_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'MSI Raider GE78 HX', 2899.00, 'Gaming quái vật, i9-13980HX, RTX 4080', u.id
FROM brands b, users u WHERE b.name = 'MSI' AND u.username = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Asus ROG Zephyrus G14 2024', 1600.00, 'Màn hình OLED, Ryzen 9, RTX 4060', u.id
FROM brands b, users u WHERE b.name = 'Asus' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'HP Spectre x360 14', 1450.00, 'Xoay gập 360 độ, bút cảm ứng đi kèm', u.id
FROM brands b, users u WHERE b.name = 'HP' AND u.username = 'support_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Acer Predator Helios Neo 16', 1150.00, 'Cấu hình gaming quốc dân cho sinh viên', u.id
FROM brands b, users u WHERE b.name = 'Acer' AND u.username = 'support_01' ON CONFLICT DO NOTHING;