# Placement Assistant — Powered by AI

Placement Assistant is a five-person campus-management MVP built as one React
frontend and one Spring Boot API. It is a **development scaffold**, not a
production-ready application.

## MVP modules
- Authentication and role-based access foundation
- Student academic profile/summary
- Timetable and attendance
- Placement drives, eligibility and applications
- Faculty, CRT and announcements
- Shared React dashboard

## Technology baseline
- Java 25
- Spring Boot 4.1.1 (declared in `backend/pom.xml`)
- Maven Wrapper
- MySQL 8.4
- React + TypeScript + Vite
- Node.js 24 LTS
- GitHub branches, pull requests and CI

## Repository layout
- `backend/` — Spring Boot REST API
- `frontend/` — React + TypeScript app
- `database/` — local schema/seed SQL
- `docs/` — requirements, architecture, API and team agreements
- `scripts/` — local verification helpers

## Setup
1. Install JDK 25, Node.js 24 LTS, MySQL 8.4, Git and a supported Java IDE.
2. From `frontend/`, run `npm ci` to install the locked frontend dependencies.
3. Configure backend database credentials and `JWT_SECRET` locally; never commit local configuration or secrets.
4. Treat `database/schema.sql` as a local starter schema only. It is not evidence of a deployed database or a migration baseline.
5. Read `files/Placement_Assistant_PRD.docx` and `files/Placement_Assistant_Team_Guide.docx` before implementing a feature. Supporting architecture, API, security, data, testing and ownership notes are in `docs/`.

## Run commands
Backend (PowerShell):
```powershell
Set-Location backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```
Frontend:
```powershell
Set-Location frontend
npm ci
npm run lint
npm run build
npm run dev
```

## Git workflow
- `main`: release/demo-ready
- `develop`: integration branch
- Feature branches: `feature/<person>-<module>`
- Work only in your assigned files; open a PR into `develop`.
- Never commit secrets, `.env`, build output, IDE caches or generated dependencies.

## Important limitations
- The backend currently provides database-backed login and JWT validation. Account creation/claim, password reset, refresh tokens, lockout and role-specific authenticated pages are not connected.
- The public portal-selection view is a temporary authentication entry integration. The full public Home page, About and Contact sections remain Pallavi-owned.
- Tracked database SQL is a local starter, not a confirmed deployed schema. No Flyway migration history is present in the repository.
- Do not use real student personal data in demos. Use synthetic data.
- Do not represent attendance GPS/photo verification or placement eligibility as production-validated.
