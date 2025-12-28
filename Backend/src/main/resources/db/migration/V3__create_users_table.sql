-- Sprint 4: JWT Authentication - Create users table
-- V4__create_users_table.sql

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_username UNIQUE (username)
);

-- Create index for faster username lookups
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Create index for admin queries
CREATE INDEX IF NOT EXISTS idx_users_is_admin ON users(is_admin);

-- Insert default admin user (password: admin123 - BCrypt hashed)
-- Password hash için: $2a$10$... formatı BCrypt
INSERT INTO users (username, password, is_admin, enabled) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE, TRUE)
ON CONFLICT (username) DO NOTHING;

-- Insert default regular user (password: user123 - BCrypt hashed)
INSERT INTO users (username, password, is_admin, enabled) 
VALUES ('user', '$2a$10$9sIPYr3hN/5KPqJqsqY0aOvXLQBqTKJH5DMHB1KjB4lU1zZqF3Hqm', FALSE, TRUE)
ON CONFLICT (username) DO NOTHING;

COMMENT ON TABLE users IS 'JWT Authentication - User accounts table';
COMMENT ON COLUMN users.username IS 'Unique username for login';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password';
COMMENT ON COLUMN users.is_admin IS 'Admin privilege flag';
COMMENT ON COLUMN users.enabled IS 'Account active/inactive status';