# AI Prompt Pack — Campus Connect

Use the shared guardrail in `docs/AI_WORKING_RULES.md` first. Replace bracketed values before prompting.

## Surendra — foundation
**Prompt S1 — backend foundation**
> Inspect the existing repository and `docs/ARCHITECTURE.md`, `docs/API.md`, and `docs/SECURITY_AND_PRIVACY.md`. I own backend build/config, `common/**`, `config/**`, `security/**`, and `identity/**`. First report `git status` and the exact files you intend to change. Implement only a minimal compilable Spring Boot Java 25 foundation using the already-agreed Spring Boot patch and dependencies. Add a consistent error response and tests. Do not implement fake production authentication or edit frontend/domain folders. Run tests and report exact commands/results.

**Prompt S2 — authorization**
> In my owned security files only, implement the agreed demo role authorization structure. Enforce authorization server-side and include tests for unauthenticated 401 and forbidden 403. Do not invent credentials or claim the login is production-ready. Ask before changing the API contract.

## Aliza — academics
**Prompt A1 — student summary**
> Implement the student academic summary described in `docs/API.md` and `docs/DATA_MODEL.md`. Only edit backend `academic/**`, its tests, and frontend `src/features/students/**`. Use DTOs, validation, synthetic data and clear error/empty UI states. Do not edit shared router, package files, security or other modules. Inspect existing patterns first, then implement the smallest vertical slice and run relevant tests.

## Naman — attendance
**Prompt N1 — attendance**
> Implement class sessions and attendance summary per `docs/API.md` and `docs/DATA_MODEL.md`. Only edit backend `attendance/**`, its tests, and frontend `src/features/attendance/**`. Prevent duplicate records for a student/session and enforce faculty-only marking on the server. Do not add GPS, photos or geofencing. Follow existing shared DTO/error conventions and report tests actually run.

## Ashish — placements
**Prompt H1 — placement eligibility**
> Implement placement drives, eligibility criteria/reasons and applications as described in `docs/API.md` and `docs/DATA_MODEL.md`. Only edit backend `placement/**`, its tests, and frontend `src/features/placements/**`. Eligibility must be deterministic and explain each criterion in plain language; do not create an opaque AI score. Prevent duplicate applications and validate inputs. Do not edit shared files without approval.

## Pallavi — frontend shell and communications
**Prompt P1 — app shell**
> Build the shared React + TypeScript shell using the existing Vite setup. You own frontend app/layout/router/shared components/API client/package files and backend `faculty/**` and `communications/**`. Preserve current visual style, make it responsive, and add loading/error/empty states. Coordinate shared-file changes and do not overwrite other feature pages. Run lint/build and report exact results.

**Prompt P2 — announcements**
> Implement the announcement list/create flow using the agreed API contract. Only use your assigned frontend communications/shared files and backend `communications/**`. Validate input and enforce faculty/TPO/admin permission server-side. Use synthetic demo content only.
