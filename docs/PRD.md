# Product Requirements Document — Campus Connect

**Status:** Draft for team review  
**Target:** One-day MVP/demo  
**Audience:** Students, faculty, Training & Placement Office (TPO), administrators

## 1. Problem
Campus workflows such as academic summaries, attendance, announcements and placements are spread across separate channels. Campus Connect aims to demonstrate a unified campus dashboard.

## 2. MVP goals
1. Provide a role-aware dashboard for Admin, Faculty, Student and TPO demo roles.
2. Show a student academic summary using synthetic data.
3. Show timetable/attendance information and a simple attendance-marking flow.
4. Show placement drives, basic eligibility reasons and applications.
5. Show faculty/CRT announcements in one place.
6. Demonstrate a deterministic Campus Readiness & Placement Intelligence (CRPI) checklist with transparent reasons.

## 3. Out of scope for the one-day MVP
- Production SSO, password reset and account lifecycle
- Official marksheets or student-record imports
- GPS/geofencing/photo-based attendance verification
- Email/SMS delivery
- Employer portal and real application submissions
- Production hosting, backups, monitoring or formal security certification

## 4. Roles
- `ROLE_ADMIN`: demo administration
- `ROLE_FACULTY`: class/attendance and academic views
- `ROLE_STUDENT`: own profile, attendance and placement view
- `ROLE_TPO`: placement drives and eligibility overview

Authorization must be enforced on the server, not only by hiding UI controls.

## 5. Functional requirements
- FR-01: Provide a shared responsive application shell and navigation.
- FR-02: Expose versioned REST endpoints under `/api/v1`.
- FR-03: Display student academic summary with loading, error and empty states.
- FR-04: Display class sessions and attendance summary; prevent duplicate attendance records.
- FR-05: Display placement drives, eligibility criteria and clear eligibility reasons.
- FR-06: Display announcements and a basic faculty/CRT view.
- FR-07: Return consistent validation and API error responses.
- FR-08: Use synthetic demo data only.

## 6. Non-functional requirements
- Use DTOs for API requests/responses; do not expose JPA entities directly.
- Validate input on the server.
- Add tests for key service/controller behaviour and role access.
- Keep dependency versions consistent and documented.
- Avoid committing credentials, personal information or secrets.
- Build and lint must pass before demo release.

## 7. Success criteria
- Fresh checkout setup steps are documented.
- Backend test/build and frontend build/lint run successfully.
- Each core screen has loading/error/empty/success handling.
- One student, one attendance and one placement demo flow can be shown.
- Known limitations are stated honestly.

## 8. Demo data
Use fabricated names, student IDs, attendance figures and placement criteria. Do not use actual student records.
