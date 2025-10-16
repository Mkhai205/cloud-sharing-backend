-- Create tables if not exist
CREATE TABLE IF NOT EXISTS permission
(
    name
    VARCHAR
(
    255
) NOT NULL PRIMARY KEY,
    description VARCHAR
(
    500
)
    );

CREATE TABLE IF NOT EXISTS role
(
    name
    VARCHAR
(
    255
) NOT NULL PRIMARY KEY,
    description VARCHAR
(
    500
)
    );

CREATE TABLE IF NOT EXISTS role_permissions
(
    role_name
    VARCHAR
(
    255
) NOT NULL,
    permissions_name VARCHAR
(
    255
) NOT NULL,
    PRIMARY KEY
(
    role_name,
    permissions_name
),
    FOREIGN KEY
(
    role_name
) REFERENCES role
(
    name
),
    FOREIGN KEY
(
    permissions_name
) REFERENCES permission
(
    name
)
    );

CREATE TABLE IF NOT EXISTS users
(
    id
    UUID
    NOT
    NULL
    PRIMARY
    KEY,
    email
    VARCHAR
(
    255
) NOT NULL UNIQUE,
    password VARCHAR
(
    255
),
    provider VARCHAR
(
    50
) NOT NULL,
    provider_id VARCHAR
(
    255
),
    first_name VARCHAR
(
    255
) NOT NULL,
    last_name VARCHAR
(
    255
) NOT NULL,
    avatar_url VARCHAR
(
    500
),
    credits INTEGER,
    is_account_verified BOOLEAN,
    verify_otp VARCHAR
(
    255
),
    verify_otp_expiry TIMESTAMP,
    reset_password_token VARCHAR
(
    255
),
    reset_password_token_expiry TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id
    UUID
    NOT
    NULL,
    roles_name
    VARCHAR
(
    255
) NOT NULL,
    PRIMARY KEY
(
    user_id,
    roles_name
),
    FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
),
    FOREIGN KEY
(
    roles_name
) REFERENCES role
(
    name
)
    );

CREATE TABLE IF NOT EXISTS invalidated_token
(
    id
    VARCHAR
(
    255
) NOT NULL PRIMARY KEY,
    expiration_time TIMESTAMP
    );

-- Clear existing data to avoid duplicates
DELETE
FROM users_roles;
DELETE
FROM role_permissions;
DELETE
FROM users;
DELETE
FROM role;
DELETE
FROM permission;

-- Insert Permissions
INSERT INTO permission (name, description)
VALUES ('READ_USER', 'Read user information'),
       ('WRITE_USER', 'Create and update user information'),
       ('DELETE_USER', 'Delete user information'),
       ('READ_FILE', 'Read file information'),
       ('WRITE_FILE', 'Upload and update files'),
       ('DELETE_FILE', 'Delete files'),
       ('ADMIN_ACCESS', 'Full admin access');

-- Insert Roles
INSERT INTO role (name, description)
VALUES ('ADMIN', 'Administrator with full access'),
       ('USER', 'Regular user with limited access');

-- Insert Role-Permission relationships (fixed column names)
INSERT INTO role_permissions (role_name, permissions_name)
VALUES
-- Admin permissions
('ADMIN', 'READ_USER'),
('ADMIN', 'WRITE_USER'),
('ADMIN', 'DELETE_USER'),
('ADMIN', 'READ_FILE'),
('ADMIN', 'WRITE_FILE'),
('ADMIN', 'DELETE_FILE'),
('ADMIN', 'ADMIN_ACCESS'),
-- User permissions
('USER', 'READ_USER'),
('USER', 'READ_FILE'),
('USER', 'WRITE_FILE');

-- Insert Users
-- Admin user (email: admin@cloudsharing.com  password: admin123 - encoded with BCrypt)
INSERT INTO users (id, email, password, provider, provider_id, first_name, last_name, avatar_url, credits,
                   is_account_verified, verify_otp, verify_otp_expiry, reset_password_token,
                   reset_password_token_expiry, created_at, updated_at)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'admin@cloudsharing.com',
        '$2a$10$tVRXfhNaUKHibOfX7hy7e.ztt1as8LucOiTto5yJx8cOoGMBcZ0PS', 'LOCAL', null, 'Admin', 'User',
        'https://ui-avatars.com/api/?name=Admin+User&background=0D8ABC&color=fff', 1000, true, null, null, null, null,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Regular user 1 (email: user1@example.com  password: user123 - encoded with BCrypt)
       ('550e8400-e29b-41d4-a716-446655440002', 'user1@example.com',
        '$2a$10$8L0Gkh2E6c8Uf9wqsQF0BepIqaP47KKEnGscUeuejMCmdNMI0nI4G', 'LOCAL', null, 'John', 'Doe',
        'https://ui-avatars.com/api/?name=John+Doe&background=7C3AED&color=fff', 100, true, null, null, null, null,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Regular user 2 (email: user2@example.com  password: user456 - encoded with BCrypt)
       ('550e8400-e29b-41d4-a716-446655440003', 'user2@example.com',
        '$2a$10$z1zqDW9sFEDCOciDW9Bgfu/TYA1Zcw5P5x8sSWMFdItnbdhiKY25K', 'LOCAL', null, 'Jane', 'Smith',
        'https://ui-avatars.com/api/?name=Jane+Smith&background=EC4899&color=fff', 150, true, null, null, null, null,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert User-Role relationships
INSERT INTO users_roles (user_id, roles_name)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'ADMIN'),
       ('550e8400-e29b-41d4-a716-446655440002', 'USER'),
       ('550e8400-e29b-41d4-a716-446655440003', 'USER');
