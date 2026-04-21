CREATE TABLE coupon_user (
    id          BIGSERIAL PRIMARY KEY,
    coupon_id   BIGINT NOT NULL REFERENCES coupon(id),
    user_id     BIGINT,
    code        VARCHAR(50) NOT NULL UNIQUE,
    status      VARCHAR(20) NOT NULL DEFAULT 'UNREGISTERED',
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);

COMMENT ON COLUMN coupon_user.status IS 'UNREGISTERED:미등록, REGISTERED:등록완료, USED:사용완료';
COMMENT ON COLUMN coupon_user.user_id IS '쿠폰 등록한 사용자 ID (미등록 시 NULL)';