-- =============================================
-- 1. SEED DATA FOR BRANDS
-- =============================================
INSERT INTO brands (brand_name) VALUES ('Apple');      -- ID: 1
INSERT INTO brands (brand_name) VALUES ('Dell');       -- ID: 2
INSERT INTO brands (brand_name) VALUES ('HP');         -- ID: 3
INSERT INTO brands (brand_name) VALUES ('Lenovo');     -- ID: 4
INSERT INTO brands (brand_name) VALUES ('Asus');       -- ID: 5
INSERT INTO brands (brand_name) VALUES ('MSI');        -- ID: 6

-- =============================================
-- 2. SEED DATA FOR LAPTOPS
-- =============================================
INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('MacBook Air M2 2023', 999.0, 'Chip M2, RAM 8GB, SSD 256GB', 0, 1);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Dell XPS 13 Plus', 1399.0, 'Thiết kế tương lai, màn hình OLED', 0, 2);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Lenovo Legion 5 Pro', 1450.0, 'RTX 4060, Màn hình 2K 165Hz', 0, 4);

INSERT INTO laptops (name, price, description, is_deleted, brand_id)
VALUES ('Asus ROG Zephyrus G14', 1699.0, 'Gaming 14 inch mạnh nhất', 0, 5);