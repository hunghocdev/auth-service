-- =============================================
-- 1. SEED DATA FOR BRANDS
-- =============================================

-- Các thương hiệu phổ biến
INSERT INTO brands (brand_name) VALUES ('Apple');      -- ID: 1
INSERT INTO brands (brand_name) VALUES ('Dell');       -- ID: 2
INSERT INTO brands (brand_name) VALUES ('HP');         -- ID: 3
INSERT INTO brands (brand_name) VALUES ('Lenovo');     -- ID: 4
INSERT INTO brands (brand_name) VALUES ('Asus');       -- ID: 5
INSERT INTO brands (brand_name) VALUES ('Acer');       -- ID: 6

-- Các dòng Gaming
INSERT INTO brands (brand_name) VALUES ('MSI');        -- ID: 7
INSERT INTO brands (brand_name) VALUES ('Razer');      -- ID: 8
INSERT INTO brands (brand_name) VALUES ('Gigabyte');   -- ID: 9
INSERT INTO brands (brand_name) VALUES ('Alienware');  -- ID: 10

-- Các dòng khác
INSERT INTO brands (brand_name) VALUES ('Microsoft');  -- ID: 11
INSERT INTO brands (brand_name) VALUES ('Samsung');    -- ID: 12
INSERT INTO brands (brand_name) VALUES ('LG');         -- ID: 13


-- =============================================
-- 2. SEED DATA FOR LAPTOPS
-- =============================================

-- --- Apple (Brand ID: 1) ---
INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('MacBook Air M2 2023', 999.0, 'Chip M2, RAM 8GB, SSD 256GB, Màn hình Liquid Retina', 0, 1);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('MacBook Pro 14 M3 Pro', 1999.0, 'Chip M3 Pro mạnh mẽ, RAM 18GB, SSD 512GB', 0, 1);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('MacBook Pro 16 M3 Max', 3499.0, 'Cấu hình khủng nhất, RAM 36GB, dành cho dựng phim 4K', 0, 1);

-- --- Dell (Brand ID: 2) ---
INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Dell XPS 13 Plus', 1399.0, 'Thiết kế tương lai, phím cảm ứng, màn hình OLED 3.5K', 0, 2);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Dell Inspiron 16 5630', 749.0, 'Laptop văn phòng bền bỉ, Core i5 gen 13', 0, 2);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Dell Alienware m16 R2', 2499.0, 'Cỗ máy gaming hủy diệt, RTX 4080', 0, 2); -- Alienware giờ thuộc Dell

-- --- HP (Brand ID: 3) ---
INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('HP Spectre x360 14', 1599.0, 'Laptop doanh nhân xoay gập 360 độ, bút cảm ứng', 0, 3);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('HP Omen 16', 1299.0, 'Gaming tầm trung, tản nhiệt Omen Tempest Cooling', 0, 3);

-- --- Lenovo (Brand ID: 4) ---
INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Lenovo ThinkPad X1 Carbon Gen 11', 1899.0, 'Siêu nhẹ, bàn phím gõ sướng nhất thế giới', 0, 4);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Lenovo Legion 5 Pro', 1450.0, 'Màn hình 2K 165Hz, RTX 4060, Best Seller gaming', 0, 4);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Lenovo Yoga 9i', 1350.0, 'Loa Bowers & Wilkins xoay chiều, màn hình 4K OLED', 0, 4);

-- --- Asus (Brand ID: 5) ---
INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Asus ROG Zephyrus G14', 1699.0, 'Gaming 14 inch mạnh nhất thế giới, LED AniMe Matrix', 0, 5);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Asus ZenBook S 13 OLED', 1099.0, 'Mỏng nhất thế giới, nặng chỉ 1kg', 0, 5);

-- --- MSI (Brand ID: 7) ---
INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('MSI Raider GE78 HX', 2899.0, 'Dải đèn LED RGB ma trận, Core i9 HX cực mạnh', 0, 7);

-- --- Razer (Brand ID: 8) ---
INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Razer Blade 15 Advanced', 2999.0, 'Vỏ nhôm nguyên khối, thiết kế như MacBook đen', 0, 8);