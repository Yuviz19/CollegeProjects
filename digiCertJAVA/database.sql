-- ============================================================
--  Digital Certificate Issuance and Verification System
--  Run this file once in MySQL to create the database.
--       mysql -u root -p < database.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS digiCertJAVA_db;
USE digiCertJAVA_db;

-- Login user for the application (single role)
CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password_hash CHAR(64)    NOT NULL
);

-- Stored certificates
CREATE TABLE IF NOT EXISTS certificates (
    id             VARCHAR(20) PRIMARY KEY,          -- e.g. DC-1A2B3C4D
    holder_name    VARCHAR(100) NOT NULL,
    course         VARCHAR(100) NOT NULL,
    institution    VARCHAR(100) NOT NULL,
    grade          VARCHAR(50)  NOT NULL,
    issue_date     DATE         NOT NULL,
    expiry_date    DATE         NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE / REVOKED
    signature      CHAR(64)     NOT NULL,            -- SHA-256 "digital signature"
    revoked_reason TEXT
);

-- Demo login: admin / admin123   (used by LoginFrame "Create demo user" button)
INSERT INTO users (username, password_hash)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');
