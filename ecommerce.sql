DROP DATABASE IF EXISTS ecommerce;
CREATE DATABASE ecommerce;
USE ecommerce;

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
email VARCHAR(100) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL
);
select * from admins;
-- =========================
-- ADMINS
-- =========================
CREATE TABLE admins (
id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(100) NOT NULL,
password VARCHAR(255) NOT NULL
);

INSERT INTO admins(username,password)
VALUES ('admin','admin123');

-- =========================
-- PRODUCTS
-- =========================
CREATE TABLE products (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(150) NOT NULL,
description TEXT,
price DOUBLE NOT NULL,
image_url VARCHAR(255)
);

INSERT INTO products(name,description,price,image_url)
VALUES
('Nike Air Max','Running shoes',2500000,'nike.jpg'),
('Adidas Samba','Classic shoes',2200000,'samba.jpg'),
('Puma RS-X','Sport shoes',1800000,'puma.jpg');
INSERT INTO products(name,description,price,image_url)
VALUES
('Nike Air Max','Running shoes',2500000,'p4.jpg'),
('Adidas Samba','Classic shoes',2200000,'p5.jpg'),
('Puma RS-X','Sport shoes',1800000,'p6.jpg'),
('Puma Suede','Running shoes',2500000,'p7.jpg'),
('Adidas UltraBoost','Running shoes',2200000,'p8.jpg'),
('Adidas Yeezy350','Sport shoes',1800000,'p9.jpg');
SET SQL_SAFE_UPDATES = 0;
UPDATE products SET image_url = 'p1.jpg' WHERE name = 'Nike Air Max';
UPDATE products SET image_url = 'p2.jpg' WHERE name = 'Adidas Samba';
UPDATE products SET image_url = 'p3.jpg' WHERE name = 'Puma RS-X';
UPDATE products SET name = 'Puma Speedcat' WHERE image_url = 'p6.jpg';

-- =========================
-- USER PUBLIC KEYS
-- KHÔNG LƯU PRIVATE KEY
-- =========================
CREATE TABLE user_keys (
id INT AUTO_INCREMENT PRIMARY KEY,


user_id INT NOT NULL,

public_key TEXT NOT NULL,

status VARCHAR(20) DEFAULT 'ACTIVE',

created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

revoked_at DATETIME NULL,

FOREIGN KEY (user_id)
    REFERENCES users(id)


);

-- =========================
-- ORDERS
-- =========================
CREATE TABLE orders (
id INT AUTO_INCREMENT PRIMARY KEY,

user_id INT NOT NULL,

product_name VARCHAR(255),

quantity INT DEFAULT 1,

price DOUBLE,

promotion VARCHAR(100),

order_date DATETIME DEFAULT CURRENT_TIMESTAMP,

-- dữ liệu chữ ký số
order_hash TEXT,

signature TEXT,

public_key_id INT,

verify_status VARCHAR(20) DEFAULT 'PENDING',

FOREIGN KEY (user_id)
    REFERENCES users(id),

FOREIGN KEY (public_key_id)
    REFERENCES user_keys(id)


);

-- =========================
-- SAMPLE USER
-- =========================
INSERT INTO users(name,email,password)
VALUES
('Nguyen Van A','[user@gmail.com](mailto:user@gmail.com)','123456');
