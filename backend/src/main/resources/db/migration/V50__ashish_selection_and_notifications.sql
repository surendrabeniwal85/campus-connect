-- V50__ashish_selection_and_notifications.sql
-- Selection, Staff Profiles, Live Updates, Feedback, and Notifications

CREATE TABLE placement_staff_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    designation VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE live_session_update (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drive_id BIGINT NOT NULL,
    stage VARCHAR(100) NOT NULL,
    counts_json TEXT,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_live_drive FOREIGN KEY (drive_id) REFERENCES placement_drives(id)
);

CREATE TABLE drive_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drive_id BIGINT NOT NULL,
    source VARCHAR(50) NOT NULL, -- STUDENT, TPO, COMPANY
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feedback_drive FOREIGN KEY (drive_id) REFERENCES placement_drives(id)
);

CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    link VARCHAR(255),
    audience VARCHAR(50) NOT NULL, -- USER, ROLE, BRANCH_SEM, DRIVE_APPLICANTS, ALL
    target_value VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification_recipient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_status BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_notif_ref FOREIGN KEY (notification_id) REFERENCES notification(id) ON DELETE CASCADE,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);