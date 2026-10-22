# Team Ownership and File Boundaries

The current Placement Assistant Team Guide is authoritative. All five members
contribute frontend pages to one shared system; ownership of the system and
shell remains with Pallavi.

## Surendra — access, master accounts, and student records

- Own authentication, authorization, account management, audit foundation,
  student records, registration, CSV import/export, backend integration, and
  repository/CI documentation.
- Frontend pages: Institute login, Placement login, reset password, Account
  Manager for HOD and Director HR, student list/profile/registration, and CSV
  import.
- Coordinate shared page routing, API contracts, schema, and student rules.
- Migration ownership: V10–V29.

## Naman — classroom and training

- Own attendance, classroom, CRT, timetable, and question-bank backend and
  frontend modules.
- Provide owner-approved attendance/CRT query contracts for other features.
- Migration ownership: V30–V39.

## Aliza — academics and placement drives

- Own academics, companies, drives, eligibility rules/evaluator, applications,
  opportunities, and placement statistics.
- Own Offer Path simulation and per-drive one-rule-away calculations, reusing
  the authoritative eligibility evaluator.
- Migration ownership: V1–V9, V40–V49, and V90+.

## Ashish — selection and communication

- Own placement selection, rounds, notifications, feedback, and related
  backend/frontend modules.
- Own the notification service used by TPO nudges.
- Migration ownership: V50–V59.

## Pallavi — shared visual system, shell, and student hub

- Own the shared design system, layouts, shell, Home page, news, dashboards,
  resume/ATS, AI Coach, and student hub.
- Own shared frontend dependencies, API client, route integration, and shared
  components; coordinate contributions from feature owners.
- Own news/resume migrations V60–V69.
- Offer Path slider card and AI Coach wording remain Pallavi-owned.

## Offer Path cross-module boundary

See `docs/OFFER_PATH_INTEGRATION.md` for the read-only simulation, evaluator
result semantics, one-rule-away count, and notification dependencies. Aliza
owns eligibility decisions and simulation/count logic; Pallavi owns the
student-facing card; Ashish owns notification delivery. Surendra contributes
authentication, student records, authorization, and agreed integration
support, but does not take over those modules.

## Shared rules

- One person owns a shared file at a time; coordinate changes with its owner.
- Do not regenerate or overwrite another person's work.
- Each module documents endpoint/request/response contracts and tests its core
  rules.
