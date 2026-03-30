CREATE TABLE search_history
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          VARCHAR(100) NOT NULL,
    raw_query        TEXT         NOT NULL,
    parsed_condition JSONB,
    created_at       TIMESTAMP    NOT NULL DEFAULT now()
);