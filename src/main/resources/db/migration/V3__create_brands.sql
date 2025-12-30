-- =============================================
-- V3: KHỞI TẠO BẢNG BRANDS
-- =============================================

CREATE TABLE IF NOT EXISTS brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_brands_modtime
    BEFORE UPDATE ON brands
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();