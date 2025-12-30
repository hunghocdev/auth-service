-- =============================================
-- S3: ĐỔ DỮ LIỆU BRANDS VÀ LAPTOPS DIỆN RỘNG (MASSIVE SEED)
-- Flyway sẽ quét file này nếu cấu hình locations bao gồm db/seed
-- =============================================

-- 1. Chèn danh sách Thương hiệu (Brands)
INSERT INTO brands (name) VALUES
('Apple'), ('Dell'), ('HP'), ('Lenovo'), ('Asus'), ('Acer'), ('MSI'), ('Gigabyte'), ('Razer'), ('Microsoft'), ('Samsung'), ('LG')
ON CONFLICT (name) DO NOTHING;

-- 2. Chèn danh sách Laptop mẫu (Hơn 30 mẫu mã đa dạng)
-- Logic: Dùng Subquery để lấy ID của Brand và ID của User (Người tạo)

-- PHÂN KHÚC APPLE (Admin tạo)
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'MacBook Pro 14 M3', 1599.00, 'M3 Chip, 8-core CPU, 10-core GPU, 8GB Unified Memory, 512GB SSD', u.id
FROM brands b, users u WHERE b.name = 'Apple' AND u.username = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'MacBook Pro 16 M3 Max', 3499.00, 'M3 Max Chip, 14-core CPU, 30-core GPU, 36GB Unified Memory, 1TB SSD', u.id
FROM brands b, users u WHERE b.name = 'Apple' AND u.username = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'MacBook Air 15 M2', 1299.00, 'M2 Chip, Liquid Retina display, 8GB RAM, 256GB SSD', u.id
FROM brands b, users u WHERE b.name = 'Apple' AND u.username = 'admin' ON CONFLICT DO NOTHING;

-- PHÂN KHÚC DELL (Manager tạo)
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Dell XPS 13 9315', 1100.00, 'Intel Core i5-1230U, 8GB RAM, 256GB SSD, 13.4-inch FHD+', u.id
FROM brands b, users u WHERE b.name = 'Dell' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Dell XPS 15 9530', 2150.00, 'Intel Core i7-13700H, 16GB RAM, 512GB SSD, RTX 4050', u.id
FROM brands b, users u WHERE b.name = 'Dell' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Dell Alienware m16 R1', 2599.00, 'Core i9-13900HX, 32GB RAM, 1TB SSD, RTX 4080', u.id
FROM brands b, users u WHERE b.name = 'Dell' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Dell Latitude 5440', 950.00, 'Business laptop, Core i5-1335U, 16GB RAM, 512GB SSD', u.id
FROM brands b, users u WHERE b.name = 'Dell' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

-- PHÂN KHÚC ASUS (Staff tạo)
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Asus ROG Strix G16', 1399.00, 'Core i7-13650HX, 16GB RAM, RTX 4060, 165Hz Display', u.id
FROM brands b, users u WHERE b.name = 'Asus' AND u.username = 'staff_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Asus Zenbook 14 OLED', 999.00, 'Ryzen 7 7730U, 16GB RAM, 512GB SSD, 2.8K OLED', u.id
FROM brands b, users u WHERE b.name = 'Asus' AND u.username = 'staff_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Asus TUF Gaming F15', 850.00, 'Core i5-12500H, 8GB RAM, RTX 3050, Durable Gaming', u.id
FROM brands b, users u WHERE b.name = 'Asus' AND u.username = 'staff_01' ON CONFLICT DO NOTHING;

-- PHÂN KHÚC LENOVO (Support tạo)
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Lenovo ThinkPad X1 Carbon Gen 11', 1750.00, 'Carbon Fiber, i7-1355U, 16GB RAM, 512GB SSD', u.id
FROM brands b, users u WHERE b.name = 'Lenovo' AND u.username = 'support_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Lenovo Legion Slim 7i', 1899.00, 'Core i9-13900H, 16GB RAM, RTX 4070, 240Hz Screen', u.id
FROM brands b, users u WHERE b.name = 'Lenovo' AND u.username = 'support_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Lenovo IdeaPad Slim 5', 650.00, 'Ryzen 5 7530U, 16GB RAM, 512GB SSD, Budget Friendly', u.id
FROM brands b, users u WHERE b.name = 'Lenovo' AND u.username = 'support_01' ON CONFLICT DO NOTHING;

-- PHÂN KHÚC HP (Admin tạo)
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'HP Spectre x360 14', 1499.00, '2-in-1, Core i7-1355U, 16GB RAM, OLED Touch', u.id
FROM brands b, users u WHERE b.name = 'HP' AND u.username = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'HP Victus 15', 799.00, 'Core i5-13420H, 8GB RAM, RTX 3050, Entry Level Gaming', u.id
FROM brands b, users u WHERE b.name = 'HP' AND u.username = 'admin' ON CONFLICT DO NOTHING;

-- PHÂN KHÚC RAZER & MSI (Manager tạo)
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Razer Blade 14', 2399.00, 'Ryzen 9 7940HS, RTX 4070, 16GB RAM, QHD 240Hz', u.id
FROM brands b, users u WHERE b.name = 'Razer' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'MSI Katana 15', 1099.00, 'Core i7-13620H, 16GB RAM, RTX 4060, Performance Gaming', u.id
FROM brands b, users u WHERE b.name = 'MSI' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

-- PHÂN KHÚC MICROSOFT & LG (Staff tạo)
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Microsoft Surface Laptop 5', 1299.00, '13.5 inch, Core i7, 16GB RAM, 512GB SSD, Platinum', u.id
FROM brands b, users u WHERE b.name = 'Microsoft' AND u.username = 'staff_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'LG Gram 17', 1450.00, 'Ultra-lightweight, i7-1360P, 16GB RAM, 1TB SSD, 17-inch QHD', u.id
FROM brands b, users u WHERE b.name = 'LG' AND u.username = 'staff_01' ON CONFLICT DO NOTHING;

-- BỔ SUNG THÊM DỮ LIỆU ĐỂ TEST PHÂN TRANG (Hơn 15 máy nữa)
INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Acer Swift Go 14', 750.00, 'OLED Display, i5-13500H, 16GB RAM', u.id
FROM brands b, users u WHERE b.name = 'Acer' AND u.username = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Acer Nitro V 15', 820.00, 'i5-13420H, RTX 4050, Gaming Budget', u.id
FROM brands b, users u WHERE b.name = 'Acer' AND u.username = 'manager_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Gigabyte G5 KF', 900.00, 'i5-12500H, RTX 4060, High Value Gaming', u.id
FROM brands b, users u WHERE b.name = 'Gigabyte' AND u.username = 'staff_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Asus Vivobook 16X', 720.00, 'Ryzen 7 5800H, 16GB RAM, Large Screen', u.id
FROM brands b, users u WHERE b.name = 'Asus' AND u.username = 'support_01' ON CONFLICT DO NOTHING;

INSERT INTO laptops (brand_id, name, price, description, created_by)
SELECT b.id, 'Dell Vostro 3430', 600.00, 'Core i3-1305U, 8GB RAM, Office use', u.id
FROM brands b, users u WHERE b.name = 'Dell' AND u.username = 'admin' ON CONFLICT DO NOTHING;