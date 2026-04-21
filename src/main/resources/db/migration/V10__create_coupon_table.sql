CREATE TABLE coupon (
                        id                  BIGSERIAL PRIMARY KEY,
                        coupon_name         VARCHAR(100) NOT NULL,
                        discount_type       VARCHAR(20) NOT NULL,
                        discount_value      DECIMAL(10,2) NOT NULL,
                        min_order_amount    DECIMAL(10,2),
                        max_discount_amount DECIMAL(10,2),
                        start_date          TIMESTAMP NOT NULL,
                        end_date            TIMESTAMP NOT NULL,
                        usage_limit         INTEGER,
                        used_count          INTEGER DEFAULT 0,
                        is_deleted          BOOLEAN DEFAULT FALSE,
                        created_at          TIMESTAMP NOT NULL,
                        updated_at          TIMESTAMP NOT NULL
);

COMMENT ON COLUMN coupon.discount_type IS 'PERCENTAGE:정률할인, FIXED:정액할인';
COMMENT ON COLUMN coupon.is_deleted IS '소프트 딜리트 여부';