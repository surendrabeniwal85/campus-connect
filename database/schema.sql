-- Local development starter schema. Confirm this model with docs/DATA_MODEL.md.
-- Use synthetic data only. Prefer migrations for shared/team environments.

CREATE DATABASE IF NOT EXISTS placement_assistant
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- Create a least-privilege local user manually and do not commit its password:
-- CREATE USER 'campus_app'@'localhost' IDENTIFIED BY 'choose-a-local-password';
-- GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX
-- ON placement_assistant.* TO 'campus_app'@'localhost';
-- FLUSH PRIVILEGES;

USE placement_assistant;

CREATE TABLE IF NOT EXISTS app_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  display_name VARCHAR(120) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES app_user(id),
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES app_role(id)
);

INSERT IGNORE INTO app_role(name)
VALUES ('ROLE_ADMIN'), ('ROLE_FACULTY'), ('ROLE_STUDENT'), ('ROLE_TPO');
