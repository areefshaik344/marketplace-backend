CREATE TABLE otp_tokens (
    id VARCHAR(255) PRIMARY KEY,
    phone VARCHAR(20) UNIQUE NOT NULL,
    otp VARCHAR(6) NOT NULL,
    attempts INT DEFAULT 0,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_otp_phone ON otp_tokens(phone);
