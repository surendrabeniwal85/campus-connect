-- V46: Seed demo recruitment drives, eligibility rules, and student cohort for Offer Path verification

-- 1. Demo companies
INSERT INTO company (name, industry, website) VALUES
('Demo Tech Pvt Ltd', 'Software Engineering', 'https://demotech.example.com'),
('Sample Analytics', 'Data & AI Systems', 'https://sampleanalytics.example.com'),
('Pretend Systems', 'Consulting & Cloud', 'https://pretendsystems.example.com');

-- 2. Demo recruitment drives (future dates for upcoming listing)
INSERT INTO placement_drive (company_id, role_title, employment_type, package_text, ctc_lpa, stipend_per_month, has_ppo, ppo_ctc_lpa, bond_months, job_location, venue, drive_date, deadline, status)
SELECT c.id, 'Software Engineer', 'FULL_TIME', '6.5 LPA', 6.50, NULL, FALSE, NULL, 12, 'Bengaluru', 'Seminar Hall 1', '2026-11-20', '2026-11-15 23:59:59', 'OPEN'
FROM company c WHERE c.name = 'Demo Tech Pvt Ltd';

INSERT INTO placement_drive (company_id, role_title, employment_type, package_text, ctc_lpa, stipend_per_month, has_ppo, ppo_ctc_lpa, bond_months, job_location, venue, drive_date, deadline, status)
SELECT c.id, 'Data Analyst Intern', 'INTERNSHIP_PPO', '30,000 / month', 8.50, 30000.00, TRUE, 8.50, 0, 'Hyderabad', 'Lab 3 / Online', '2026-11-28', '2026-11-22 23:59:59', 'OPEN'
FROM company c WHERE c.name = 'Sample Analytics';

INSERT INTO placement_drive (company_id, role_title, employment_type, package_text, ctc_lpa, stipend_per_month, has_ppo, ppo_ctc_lpa, bond_months, job_location, venue, drive_date, deadline, status)
SELECT c.id, 'Cloud Systems Associate', 'FULL_TIME', '9.5 LPA', 9.50, NULL, FALSE, NULL, 24, 'Pune', 'Main Auditorium', '2026-12-05', '2026-11-30 23:59:59', 'OPEN'
FROM company c WHERE c.name = 'Pretend Systems';

-- 3. Eligibility criteria for each drive
-- Drive 1 (Demo Tech): Min CGPA 7.0, Max Backlogs 0, Min Attendance 75%, Branches CSE/IT/ECE, Min ATS 60
INSERT INTO eligibility_rule (drive_id, min_cgpa, max_backlogs, min_class10, min_class12, min_attendance, allowed_branches, passing_year, min_ats_score)
SELECT d.id, 7.00, 0, 70.00, 70.00, 75.00, 'CSE,IT,ECE', 2027, 60
FROM placement_drive d JOIN company c ON d.company_id = c.id WHERE c.name = 'Demo Tech Pvt Ltd';

-- Drive 2 (Sample Analytics): Min CGPA 8.0, Max Backlogs 0, Min Attendance 80%, Branches CSE/IT/AI/DS, Min ATS 70
INSERT INTO eligibility_rule (drive_id, min_cgpa, max_backlogs, min_class10, min_class12, min_attendance, allowed_branches, passing_year, min_ats_score)
SELECT d.id, 8.00, 0, 75.00, 75.00, 80.00, 'CSE,IT,AI,DS', 2027, 70
FROM placement_drive d JOIN company c ON d.company_id = c.id WHERE c.name = 'Sample Analytics';

-- Drive 3 (Pretend Systems): Min CGPA 8.8, Max Backlogs 0, Min Attendance 75%, Branches CSE/IT/ECE, Min ATS 75
-- (Note: Primary student Surendra has CGPA 8.52, making him 'one rule away' by CGPA)
INSERT INTO eligibility_rule (drive_id, min_cgpa, max_backlogs, min_class10, min_class12, min_attendance, allowed_branches, passing_year, min_ats_score)
SELECT d.id, 8.80, 0, 80.00, 80.00, 75.00, 'CSE,IT,ECE', 2027, 75
FROM placement_drive d JOIN company c ON d.company_id = c.id WHERE c.name = 'Pretend Systems';

-- 4. Additional cohort student profiles for TPO one-rule-away and nudge testing
INSERT INTO app_user (display_name, email, password_hash) VALUES
('Amit Kumar', 'amit.kumar@institute.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$+ct5oDKf7OBlOvenkd9V/lRFVYAVFm7qoHnxXvZ6VQk='),
('Priya Sharma', 'priya.sharma@institute.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$+ct5oDKf7OBlOvenkd9V/lRFVYAVFm7qoHnxXvZ6VQk='),
('Rohit Verma', 'rohit.verma@institute.ac.in', 'pbkdf2-sha256$100000$AAECAwQFBgcICQoLDA0ODw==$+ct5oDKf7OBlOvenkd9V/lRFVYAVFm7qoHnxXvZ6VQk=');

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u CROSS JOIN app_role r
WHERE u.email IN ('amit.kumar@institute.ac.in', 'priya.sharma@institute.ac.in', 'rohit.verma@institute.ac.in') AND r.name = 'ROLE_STUDENT';

-- Amit Kumar: CGPA 6.80, backlogs 0, attendance 82%, branch CSE -> One rule away for Drive 1 (needs CGPA 7.00)
INSERT INTO student_profile (user_id, roll_number, full_name, email, phone, course, department, branch, semester, cgpa, active_backlogs, class10_percent, class12_percent, passing_year, ats_score)
SELECT u.id, 'CS2026-002', 'Amit Kumar', 'amit.kumar@institute.ac.in', '+91 98765 00002', 'B.Tech', 'Computer Science & Engineering', 'CSE', 7, 6.80, 0, 82.00, 80.00, 2027, 65
FROM app_user u WHERE u.email = 'amit.kumar@institute.ac.in';

-- Priya Sharma: CGPA 8.20, backlogs 1, attendance 85%, branch CSE -> One rule away for Drive 2 (needs 0 backlogs)
INSERT INTO student_profile (user_id, roll_number, full_name, email, phone, course, department, branch, semester, cgpa, active_backlogs, class10_percent, class12_percent, passing_year, ats_score)
SELECT u.id, 'CS2026-003', 'Priya Sharma', 'priya.sharma@institute.ac.in', '+91 98765 00003', 'B.Tech', 'Computer Science & Engineering', 'CSE', 7, 8.20, 1, 91.00, 89.00, 2027, 72
FROM app_user u WHERE u.email = 'priya.sharma@institute.ac.in';

-- Rohit Verma: CGPA 7.50, backlogs 0, attendance 68%, branch IT -> One rule away for Drive 1 (needs attendance 75%)
INSERT INTO student_profile (user_id, roll_number, full_name, email, phone, course, department, branch, semester, cgpa, active_backlogs, class10_percent, class12_percent, passing_year, ats_score)
SELECT u.id, 'CS2026-004', 'Rohit Verma', 'rohit.verma@institute.ac.in', '+91 98765 00004', 'B.Tech', 'Information Technology', 'IT', 7, 7.50, 0, 78.00, 76.00, 2027, 68
FROM app_user u WHERE u.email = 'rohit.verma@institute.ac.in';
