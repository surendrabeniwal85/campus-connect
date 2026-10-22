INSERT INTO company (name, industry, website) VALUES
 ('Demo Tech Pvt Ltd', 'Software', 'https://example.com/demo-tech'),
 ('Sample Analytics', 'Data and AI', 'https://example.com/sample-analytics'),
 ('Pretend Systems', 'Consulting', 'https://example.com/pretend-systems');

INSERT INTO placement_drive (company_id, role_title, employment_type, package_text, ctc_lpa, has_ppo, ppo_ctc_lpa,
  bond_months, job_location, venue, drive_date, deadline, status)
SELECT id, 'Software Engineer', 'FULL_TIME', '6 LPA', 6.00, FALSE, NULL, 12, 'Bengaluru', 'Seminar Hall',
  DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 'OPEN'
FROM company WHERE name = 'Demo Tech Pvt Ltd';

INSERT INTO placement_drive (company_id, role_title, employment_type, package_text, stipend_per_month, has_ppo, ppo_ctc_lpa,
  job_location, venue, drive_date, deadline, status)
SELECT id, 'Data Intern', 'INTERNSHIP_PPO', '25000 per month', 25000.00, TRUE, 8.00, 'Remote', 'Online',
  DATE_ADD(CURDATE(), INTERVAL 20 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 'OPEN'
FROM company WHERE name = 'Sample Analytics';

INSERT INTO placement_talk (title, speaker_name, talk_at, venue_or_link, description, audience) VALUES
 ('Cracking the aptitude round', 'Guest speaker', DATE_ADD(NOW(), INTERVAL 7 DAY), 'Auditorium', 'Tips and practice questions.', 'ALL');

INSERT INTO opportunity (type, title, organisation, link, location, deadline, final_year_only) VALUES
 ('HACKATHON', 'Demo Hack 2026', 'Pretend Systems', 'https://example.com/hack', 'Online', DATE_ADD(NOW(), INTERVAL 12 DAY), FALSE),
 ('INTERNSHIP', 'Summer Intern', 'Demo Tech Pvt Ltd', 'https://example.com/intern', 'Pune', DATE_ADD(NOW(), INTERVAL 25 DAY), FALSE),
 ('OFF_CAMPUS', 'Graduate Trainee', 'Sample Analytics', 'https://example.com/trainee', 'Hyderabad', DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE);
