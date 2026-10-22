-- Seed initial accounts for all 7 canonical roles with PBKDF2 hashes (auto-upgraded on login)

INSERT INTO app_user (display_name, email, password_hash) VALUES
('Student User', 'student@institute.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$+ct5oDKf7OBlOvenkd9V/lRFVYAVFm7qoHnxXvZ6VQk=');
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u CROSS JOIN app_role r
WHERE u.email = 'student@institute.ac.in' AND r.name = 'ROLE_STUDENT';

INSERT INTO app_user (display_name, email, password_hash) VALUES
('Teacher User', 'teacher@institute.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$8e50dQ036f+zP22FSxVLy5Uxr/QU3zp5WqIwcutW0I4=');
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u CROSS JOIN app_role r
WHERE u.email = 'teacher@institute.ac.in' AND r.name = 'ROLE_TEACHER';

INSERT INTO app_user (display_name, email, password_hash) VALUES
('HOD User', 'hod@institute.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$R+UY8cwjYivL7mjgRPVr/XwLpGMj0ni3e44/9/obTro=');
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u CROSS JOIN app_role r
WHERE u.email = 'hod@institute.ac.in' AND r.name = 'ROLE_HOD';

INSERT INTO app_user (display_name, email, password_hash) VALUES
('Director HR User', 'hr.director@placement.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$yY7Kfwn4HWXr3FjKLOe2zqevWKc3gQucufUKjoXQqKc=');
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u CROSS JOIN app_role r
WHERE u.email = 'hr.director@placement.ac.in' AND r.name = 'ROLE_DIRECTOR_HR';

INSERT INTO app_user (display_name, email, password_hash) VALUES
('TPO User', 'tpo@placement.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$xs6FecIo+XGvStSDiLWOzwr+xw0HGGnpkBaER2O9eSo=');
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u CROSS JOIN app_role r
WHERE u.email = 'tpo@placement.ac.in' AND r.name = 'ROLE_TPO';

INSERT INTO app_user (display_name, email, password_hash) VALUES
('Deputy TPO User', 'dtpo@placement.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$b1XCWbOXBpLzXI6+LltDv9dJWcSwfpFgPZe38ZY915Y=');
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u CROSS JOIN app_role r
WHERE u.email = 'dtpo@placement.ac.in' AND r.name = 'ROLE_D_TPO';

INSERT INTO app_user (display_name, email, password_hash) VALUES
('Placement Coordinator User', 'coordinator@placement.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$u9WcuSAyEakxPAUqtyuAfM8RVDtW8pVs/waANGmdA44=');
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u CROSS JOIN app_role r
WHERE u.email = 'coordinator@placement.ac.in' AND r.name = 'ROLE_PLACEMENT_COORDINATOR';
