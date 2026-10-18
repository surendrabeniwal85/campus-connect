# Team Ownership and File Boundaries

Five-person team: Surendra Beniwal, Aliza Imran, Naman Relia, Ashish Sharma, Pallavi Sharma.

## Surendra — integration/backend foundation
- Own: backend build/config, `common/**`, `config/**`, `security/**`, `identity/**`, root docs, CI and final integration.
- Coordinate: API contract, data model, branch protection, release.
- Avoid editing: teammates' domain folders and frontend feature pages unless agreed.

## Aliza — academics
- Own: backend `academic/**`, related tests, frontend `src/features/students/**`.
- Deliver: synthetic student profile and academic summary.

## Naman — attendance
- Own: backend `attendance/**`, related tests, frontend `src/features/attendance/**`.
- Deliver: sessions, attendance summary and duplicate-record prevention.

## Ashish — placements
- Own: backend `placement/**`, related tests, frontend `src/features/placements/**`.
- Deliver: drives, criteria, eligibility explanations and applications.

## Pallavi — frontend shell and communications
- Own: frontend app shell, layout, router, shared components/API client and package files; backend `faculty/**` and `communications/**`.
- Deliver: responsive dashboard, announcements and faculty/CRT views.

## Shared rules
- One person owns a shared file at a time.
- Do not regenerate or overwrite another person's work.
- If a shared-file change is needed, discuss it before editing.
- Each module must document endpoint/request/response contracts and include tests for its core rules.
