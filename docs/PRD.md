# Product Requirements Document — Placement Assistant

> **Historical MVP draft — superseded.** Use
> `files/Placement_Assistant_PRD.docx` and
> `files/Placement_Assistant_Team_Guide.docx` as the current requirements and
> ownership authorities. This file is retained for history; its Campus Connect
> product wording, MVP scope, and feature descriptions are not current
> requirements.

**Status:** Draft for team review  
**Target:** One-day MVP/demo  
**Audience:** Students, faculty, Training & Placement Office (TPO), administrators

## 1. Problem
Campus workflows such as academic summaries, attendance, announcements and placements are spread across separate channels. Campus Connect aims to demonstrate a unified campus dashboard.

## 2. MVP goals
1. Provide a role-aware experience for the seven canonical Institute and Placement roles.
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

The one-day demonstration scope above does not supersede the current Team
Guide's assigned authentication, account, student-record, and integration
work.

## 4. Roles

**Institute portal:** `STUDENT`, `TEACHER`, `HOD`

**Placement portal:** `DIRECTOR_HR`, `TPO`, `D_TPO`, `PLACEMENT_COORDINATOR`

Authorization must be enforced on the server, not only by hiding UI controls.
Legacy database roles `ROLE_ADMIN` and `ROLE_FACULTY` have no approved mapping
to these canonical roles.

## 5. Functional requirements
- FR-01: Provide a shared responsive application shell and navigation.
- FR-02: Expose versioned REST endpoints under `/api/v1`.
- FR-03: Display student academic summary with loading, error and empty states.
- FR-04: Display class sessions and attendance summary; prevent duplicate attendance records.
- FR-05: Display placement drives, eligibility criteria and clear eligibility reasons.
- FR-06: Display announcements and a basic faculty/CRT view.
- FR-07: Return consistent validation and API error responses.
- FR-08: Use synthetic demo data only.
- FR-09: Offer Path explains each drive's exact eligibility requirements using
  the authoritative eligibility evaluator and explicit `PASS`, `FAIL`, and
  `MISSING` rule results. A read-only what-if simulation may vary explicitly
  supplied attendance, CGPA, ATS/resume score, and backlog inputs to show which
  drives would unlock and why. Missing values and rules remain visible; the
  simulation never changes actual student records or replaces authoritative
  eligibility decisions. Actual progress may include attendance, CRT, and
  resume-score requirements only when supported by their approved data
  contracts.
- FR-10: TPOs can see a per-drive count of students exactly one rule away and
  trigger a one-click nudge through the notification service. The evaluator,
  missing-data classification, notification contract, and access policy must
  be approved by their owning modules.
- FR-11: Use the Placement Assistant visual direction: spacious white surfaces,
  clear headings, modular tiles, a thin journey rail, rounded cards with one
  notched corner, saffron primary actions, indigo Institute and teal Placement
  accents, Manrope UI text, and Fraunces display headings. Maintain accessible
  contrast, keyboard focus, phone layouts, semantic HTML, and reduced-motion
  support. Do not use Microsoft branding, Segoe UI, or copy its page layout.

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

## 9. Offer Path and design ownership

- **Aliza** owns the eligibility engine, read-only simulation operation, and
  one-rule-away count. Actual and simulated decisions use the same evaluator.
- **Pallavi** owns the Offer Path slider card, AI Coach wording, shared visual
  system, shell, and Home page.
- **Ashish** owns notification delivery used by TPO nudges.
- **Surendra** owns access/authentication, account/student foundation, and
  agreed integration support; do not take over Aliza's engine or Pallavi's
  feature UI without reassignment.

See `docs/OFFER_PATH_INTEGRATION.md` for contract dependencies and
`frontend/src/auth/placement-assistant-foundation.css` for the scoped
authentication-page contribution. Pallavi owns the shared design-system
documentation and broader adoption.
