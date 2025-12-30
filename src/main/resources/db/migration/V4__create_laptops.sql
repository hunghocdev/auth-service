-- =============================================
-- V4: KHỞI TẠO BẢNG LAPTOPS
-- =============================================

CREATE TABLE IF NOT EXISTS laptops (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    description TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,

    created_by BIGINT,
    updated_by BIGINT,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_laptops_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_laptops_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_laptops_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

CREATE INDEX idx_laptops_brand_id ON laptops(brand_id);

CREATE TRIGGER update_laptops_modtime
    BEFORE UPDATE ON laptops
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();