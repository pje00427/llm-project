CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS vector_store (
                                            id         UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    content    TEXT,
    metadata   JSONB,
    embedding  vector(3072),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_vector_store_metadata
    ON vector_store USING gin (metadata);