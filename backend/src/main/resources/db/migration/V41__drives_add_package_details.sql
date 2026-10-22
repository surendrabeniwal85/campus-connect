ALTER TABLE placement_drive ADD COLUMN employment_type ENUM('FULL_TIME','INTERNSHIP','INTERNSHIP_PPO') NOT NULL DEFAULT 'FULL_TIME';
ALTER TABLE placement_drive ADD COLUMN ctc_lpa DECIMAL(6,2) NULL;
ALTER TABLE placement_drive ADD COLUMN stipend_per_month DECIMAL(10,2) NULL;
ALTER TABLE placement_drive ADD COLUMN has_ppo BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE placement_drive ADD COLUMN ppo_ctc_lpa DECIMAL(6,2) NULL;
ALTER TABLE placement_drive ADD COLUMN bond_months INT NULL;
ALTER TABLE placement_drive ADD COLUMN bond_details TEXT NULL;
ALTER TABLE placement_drive ADD COLUMN job_location VARCHAR(150) NULL;
ALTER TABLE placement_drive ADD COLUMN other_benefits TEXT NULL;
