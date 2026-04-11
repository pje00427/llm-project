CREATE TABLE products
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    price       INT          NOT NULL CHECK (price >= 0),
    stock       INT          NOT NULL DEFAULT 0,
    rating      DOUBLE PRECISION      DEFAULT 0.0,
    category_id BIGINT REFERENCES categories (id),
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);