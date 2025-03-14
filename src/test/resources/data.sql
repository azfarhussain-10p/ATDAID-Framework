-- Clear existing users
DELETE FROM users;

-- Insert test users with BCrypt hashed passwords
-- Password for admin: TestAdmin123!
-- Password for user: TestUser123!
INSERT INTO users (id, email, password, first_name, last_name, role)
VALUES 
(1, 'admin@example.com', '$2a$10$rYJqMxmkqVQYo0lZHRYtDO/aN5rKdAqGYYRwzUzh2pwn9vFP.Uwx2', 'Admin', 'User', 'ROLE_ADMIN'),
(2, 'user@example.com', '$2a$10$9tWe6YsZulxgqhfjv4TQpuh6gkKWvz4p5dQr1M8pV5oTgE7n6oEyW', 'Regular', 'User', 'ROLE_USER'); 