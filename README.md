# Campus Connect — Java Full-Stack Starter Kit

A starter repository for a five-person campus-management MVP. This is a **development scaffold**, not a production-ready application.

## MVP modules
- Authentication and role-based access foundation
- Student academic profile/summary
- Timetable and attendance
- Placement drives, eligibility and applications
- Faculty, CRT and announcements
- Shared React dashboard

## Technology baseline
- Java 25
- Spring Boot 3.5.x (pin the exact patch version before the team starts)
- Maven Wrapper (generate/commit it using the instructions below)
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

## First setup
1. Agree on the exact dependency versions in `docs/TOOLCHAIN.md`.
2. Install JDK 25, Node.js 24 LTS, MySQL 8.4, Git and VS Code.
3. Create the Spring Boot project in `backend/` using Spring Initializr with Java 25, Maven, and dependencies: Spring Web, Spring Data JPA, Validation, Spring Security, MySQL Driver, and Spring Boot Test. Copy the generated project files into `backend/`. Generate and commit Maven Wrapper (`mvn -N wrapper:wrapper`) if it is not included.
4. From `frontend/`, run `npm install` once to generate `package-lock.json`, then use `npm ci` for repeatable installs.
5. Create a local database and user using `database/schema.sql` as a starting point. Never commit real credentials.
6. Copy `.env.example` to a local environment file only if your tooling loads it; otherwise set the environment variables in your terminal.
7. Review `docs/PRD.md`, `docs/TASK_ASSIGNMENTS.md`, `docs/API.md`, and `docs/DATA_MODEL.md` before parallel implementation.

## Run commands (after scaffolding the app)
Backend (PowerShell):
```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```
Frontend:
```powershell
cd frontend
npm ci
npm run dev
```
The frontend starter is intentionally minimal. Run `npm install` first to create the lockfile if one is not present.

## Git workflow
- `main`: release/demo-ready
- `develop`: integration branch
- Feature branches: `feature/<person>-<module>`
- Work only in your assigned files; open a PR into `develop`.
- Never commit secrets, `.env`, build output, IDE caches or generated dependencies.

## Important limitations
- This kit does not include a completed authentication system, finished business APIs, migrations, or production deployment.
- Do not use real student personal data in demos. Use synthetic data.
- Do not represent attendance GPS/photo verification or placement eligibility as production-validated.
