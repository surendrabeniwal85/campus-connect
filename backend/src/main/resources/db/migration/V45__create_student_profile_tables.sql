CREATE TABLE IF NOT EXISTS student_profile (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  roll_number VARCHAR(50) NOT NULL,
  full_name VARCHAR(150) NOT NULL,
  email VARCHAR(190) NOT NULL,
  phone VARCHAR(30),
  course VARCHAR(100) NOT NULL DEFAULT 'B.Tech',
  department VARCHAR(100) NOT NULL DEFAULT 'Computer Science & Engineering',
  branch VARCHAR(100) NOT NULL DEFAULT 'CSE',
  semester INT NOT NULL DEFAULT 7,
  cgpa DECIMAL(4,2) DEFAULT 8.52,
  active_backlogs INT DEFAULT 0,
  class10_percent DECIMAL(5,2) DEFAULT 88.50,
  class12_percent DECIMAL(5,2) DEFAULT 86.00,
  passing_year INT DEFAULT 2027,
  ats_score INT DEFAULT 82,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (user_id) REFERENCES app_user(id),
  CONSTRAINT uk_student_roll UNIQUE (roll_number),
  CONSTRAINT uk_student_email UNIQUE (email)
);

INSERT INTO student_profile (
  user_id, roll_number, full_name, email, phone, course, department, branch,
  semester, cgpa, active_backlogs, class10_percent, class12_percent, passing_year, ats_score
)
SELECT u.id, 'CS2026-001', 'Surendra Beniwal', 'student@institute.ac.in', '+91 98765 43210',
       'B.Tech', 'Computer Science & Engineering', 'CSE', 7, 8.52, 0, 88.50, 86.00, 2027, 82
FROM app_user u WHERE u.email = 'student@institute.ac.in';
