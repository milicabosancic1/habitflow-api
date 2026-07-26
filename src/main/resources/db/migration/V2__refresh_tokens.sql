-- Refresh tokeni za dugotrajne sesije (mobilni klijenti) sa mogucnoscu revokacije po uredjaju

CREATE TABLE refresh_tokens (
    id         VARCHAR(36) PRIMARY KEY,
    user_id    VARCHAR(36) NOT NULL REFERENCES users(id),
    token_hash VARCHAR(64) NOT NULL UNIQUE, -- SHA-256 hash opaque tokena, sirovi token se nikad ne cuva
    expires_at BIGINT NOT NULL,
    revoked    BOOLEAN DEFAULT FALSE,
    created_at BIGINT
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
