# Placement Assistant Project Map

Placement Assistant is a modular-monolith campus platform: one Spring Boot API,
one MySQL database, and one React + TypeScript + Vite frontend. The Institute
and Placement portals use the same backend and enforce role/portal access on
the server.

## Current repository structure

| Path | Purpose | Owner |
|---|---|---|
| `backend/src/main/java/com/placementassistant/backend/BackendApplication.java` | Spring Boot entry point | Surendra |
| `backend/src/main/java/com/placementassistant/backend/auth/` | Existing authentication, JWT, and user/role persistence | Surendra |
| `backend/src/main/java/com/placementassistant/backend/common/` | Shared API error handling | Surendra |
| `backend/src/main/java/com/placementassistant/backend/config/` | Security and application configuration | Surendra |
| `backend/src/test/java/com/placementassistant/backend/` | Backend tests; package layout mirrors production code | Respective backend owner |
| `backend/src/main/resources/application.yml.example` | Safe environment-based configuration example | Surendra |
| `database/schema.sql` | Local starter schema; use synthetic data only | Aliza |
| `docs/` | Product, architecture, API, data, security, and team documentation | Shared by topic |
| `frontend/src/App.tsx`, `styles.css` | Existing illustrative dashboard and shared styles | Pallavi |
| `frontend/src/main.tsx`, `frontend/src/auth/`, `frontend/src/pages/` | Authentication bootstrap, portal login pages, and in-memory session | Surendra; coordinate shared entry integration |
| `frontend/src/app/moduleRegistry.ts`, `frontend/src/app/routes.tsx` | Typed page catalog and registry-backed route resolution | Shared integration; coordinate with Pallavi |
| `frontend/src/auth/placement-assistant-foundation.css` | Scoped auth-page design tokens and visual primitives | Surendra contribution; shared-system owner Pallavi |
| `frontend/src/auth/PortalSelection.tsx` | Temporary public portal-selection entry; not the completed Home page | Surendra; coordinate replacement with Pallavi |
| `docs/OFFER_PATH_INTEGRATION.md` | Eligibility simulation/count/nudge contracts and ownership boundaries | Aliza, Pallavi, Ashish, Surendra |
| `frontend/src/features/` | Planned owner-specific feature pages; add within the assigned module | Respective feature owner |

Authentication is mounted at the Vite entry point. The module registry resolves
the public portal-selection view at `/`, Institute and Placement sign-in at
`/institute/login` and `/placement/login`, the authentication session at
`/auth/session`, and portal-specific reset-information pages. The session is
kept in memory. Reset, account creation/claim, and authenticated feature
destinations are not connected. `App.tsx` remains an illustrative dashboard
and is not rendered for unauthenticated visitors.

The public Home page, including About and Contact, is Pallavi-owned and is not
implemented by `PortalSelection.tsx`. To integrate her Home, preserve the
existing `/` registry entry and have its portal actions call
`onChoosePortal('/institute/login' | '/placement/login')`; the auth integration
will route those actions without creating another router.

## Feature ownership and destinations

| Owner | Backend location | Frontend location | Responsibilities |
|---|---|---|---|
| Surendra Beniwal | `platform/{common,config,security,auth,audit}/`, `institute/records/` | `app/`, shared services, `pages/InstituteLogin.tsx`, `pages/PlacementLogin.tsx`, `pages/ResetPassword.tsx`, `features/institute/records/` | Integration and backend foundation, authentication and authorization, accounts, audit, student records, registration, and CSV import/export |
| Naman Relia | `institute/classroom/` | `features/institute/classroom/` | Teacher/classroom workflows, timetable, class and CRT attendance, question bank |
| Pallavi Sharma | `institute/hub/`, faculty and communications services | Design system, layouts, shared shell, `pages/Home.tsx`, `features/institute/hub/` | News feed, dashboards, resume/ATS, AI Coach, Home, faculty/CRT views |
| Aliza Imran | `placement/drives/` | `features/placement/drives/` | Companies, drives, eligibility, applications, talks, internships/opportunities, placement statistics |
| Ashish Sharma | `placement/selection/`, `platform/notification/` | `features/placement/selection/`, `widgets/NotificationBell` | Rounds, candidates, evaluation, selection, live updates, feedback, notifications |

The backend destination folders and most feature pages above are planned
locations from the Team Guide; do not assume they are already implemented.
Within each backend feature, use controller, DTO, entity, repository, and service
layers as needed. Mirror the production package structure in tests.

## Shared services and boundaries

| Service or concern | Owner / collaborators | Boundary |
|---|---|---|
| Authentication, authorization, and audit | Surendra | Enforce access in the backend; clients do not grant permissions |
| Student summaries | Surendra | Other modules request student data through the owning query service |
| Placement eligibility and Offer Path simulation/count | Aliza | One authoritative evaluator for actual and read-only simulated decisions |
| Offer Path slider and AI Coach language | Pallavi | Present evaluator outputs; never recompute or hide eligibility rules |
| TPO nudge delivery | Ashish | Use the notification service and server-derived recipient audience |
| Attendance queries and records | Naman | Other modules use the attendance query service rather than reading attendance tables |
| News publishing and audience filtering | Pallavi; Aliza and Ashish publish relevant events | Apply audience rules server-side |
| Notifications and inbox/live delivery | Ashish | Send only to the specified users, roles, branches/semesters, applicants, or all audience |
| Placement rounds and student status queries | Ashish | Other modules use the round query service rather than accessing selection tables |
| Shared API client, layouts, and module registry | Pallavi | Coordinate contract and shared-file changes before editing |

Each module owns its persistence and business rules. Do not read another
module's tables directly; coordinate through its service/query API. Keep API
contracts stable and review cross-module changes with the owner.

## Portals and roles

| Portal | Roles |
|---|---|
| Institute | `STUDENT`, `TEACHER`, `HOD` |
| Placement | `DIRECTOR_HR`, `TPO`, `D_TPO`, `PLACEMENT_COORDINATOR` |

The same backend serves both portals. Login and every protected operation must
enforce that the authenticated role and requested resource are permitted.

## API, data, and testing references

- API base path: `/api/v1`; see `docs/API_CONTRACTS.md` and `docs/API.md`.
- Architecture and module boundaries: `docs/ARCHITECTURE.md`.
- Data model and starter schema: `docs/DATA_MODEL.md` and `database/schema.sql`.
- Security and privacy: `docs/SECURITY_AND_PRIVACY.md`.
- Test guidance: `docs/TEST_PLAN.md`; backend tests use synthetic data and H2.
- Database migrations must be reviewed, versioned, and owned by the relevant
  module. Coordinate migration versions and do not rewrite applied migrations.

`docs/API_CONTRACTS.md` is a draft for proposed endpoints and permissions. It
documents the current five-field generic error response and identifies
unresolved decisions, including reset-token delivery and migration baseline.
Do not imply that proposed endpoints are implemented or change API behavior
without approval.

## Contribution rule

Before adding or changing a page group, update this map and its entry in
`frontend/src/app/moduleRegistry.ts`. Work within the assigned folders, preserve
existing implementations, and coordinate shared-file changes with the owner.
