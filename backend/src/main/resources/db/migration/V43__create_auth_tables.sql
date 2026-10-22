CREATE TABLE app_role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    CONSTRAINT pk_app_role PRIMARY KEY (id),
    CONSTRAINT uk_app_role_name UNIQUE (name)
);

CREATE TABLE app_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    display_name VARCHAR(120) NOT NULL,
    email VARCHAR(190) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uk_app_user_email UNIQUE (email)
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES app_role (id)
);

INSERT INTO app_role (name) VALUES
    ('ROLE_STUDENT'),
    ('ROLE_TEACHER'),
    ('ROLE_HOD'),
    ('ROLE_DIRECTOR_HR'),
    ('ROLE_TPO'),
    ('ROLE_D_TPO'),
    ('ROLE_PLACEMENT_COORDINATOR');
