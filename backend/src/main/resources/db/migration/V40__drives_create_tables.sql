CREATE TABLE IF NOT EXISTS company (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(150) NOT NULL UNIQUE,
  industry VARCHAR(100),
  website VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS company_contact (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             company_id BIGINT NOT NULL,
                                             name VARCHAR(120) NOT NULL,
  email VARCHAR(150),
  phone VARCHAR(30),
  designation VARCHAR(100),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (company_id) REFERENCES company(id)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS placement_drive (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             company_id BIGINT NOT NULL,
                                             role_title VARCHAR(150) NOT NULL,
  package_text VARCHAR(100),
  venue VARCHAR(200),
  reporting_time DATETIME,
  instructions TEXT,
  drive_date DATE NOT NULL,
  deadline DATETIME NOT NULL,
  status ENUM('DRAFT','OPEN','CLOSED','COMPLETED') NOT NULL DEFAULT 'DRAFT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (company_id) REFERENCES company(id),
  INDEX idx_drive_status_date (status, drive_date)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS eligibility_rule (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              drive_id BIGINT NOT NULL UNIQUE,
                                              min_cgpa DECIMAL(4,2),
  max_backlogs INT,
  min_class10 DECIMAL(5,2),
  min_class12 DECIMAL(5,2),
  min_attendance DECIMAL(5,2),
  allowed_branches VARCHAR(255),
  passing_year INT,
  min_ats_score INT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (drive_id) REFERENCES placement_drive(id)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS drive_application (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               drive_id BIGINT NOT NULL,
                                               student_id BIGINT NOT NULL,
                                               status ENUM('APPLIED','WITHDRAWN') NOT NULL DEFAULT 'APPLIED',
  applied_at DATETIME NOT NULL,
  eligibility_snapshot TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (drive_id) REFERENCES placement_drive(id),
  UNIQUE KEY uq_application (drive_id, student_id)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
