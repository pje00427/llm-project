ALTER TABLE products
    ADD COLUMN external_product_id VARCHAR(100) UNIQUE,
    ADD COLUMN is_orderable        BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN product_type        VARCHAR(20) NOT NULL DEFAULT 'INTERNAL';