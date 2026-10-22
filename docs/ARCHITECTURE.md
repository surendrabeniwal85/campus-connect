# Architecture

## Shape
Use a modular monolith for the MVP: one Spring Boot application, one MySQL database, and one React SPA.

```text
React + TypeScript (Vite)
        |
        | JSON over HTTP: /api/v1/*
        v
Spring Boot REST API
  common / config / security
  identity / academic / attendance
  placement / faculty / communications
        |
        v
      MySQL 8.4
```

## Backend package proposal
Base package: `com.placementassistant.backend`

- `common/`: error response, exception handling, shared DTOs
- `config/`: configuration
- `security/`: authentication and authorization
- `identity/`: users and roles
- `academic/`: student profile and academic summary
- `attendance/`: class sessions and attendance
- `placement/`: companies, drives, criteria, applications and rounds
- `faculty/`: faculty and CRT views
- `communications/`: announcements/notifications

Within each module, use controller/service/repository/entity/dto/mapper as needed. Keep module boundaries clear.

## Rules
- Controllers validate/request DTOs and delegate to services.
- Services hold business rules.
- Repositories access persistence.
- Do not return persistence entities directly from REST endpoints.
- Use a consistent error response: `{ "timestamp": "...", "status": 400, "code": "VALIDATION_ERROR", "message": "...", "path": "..." }`.
- Enforce access rules server-side.
- Avoid premature microservices and unapproved dependencies.

## Placement eligibility and Offer Path boundary

Placement eligibility is owned by Aliza's placement module. Drive explanations,
actual decisions, read-only what-if simulations, and per-drive one-rule-away
counts must use the same authoritative evaluator. A simulation accepts only
explicit hypothetical inputs, preserves `PASS`, `FAIL`, and `MISSING` rule
results, never writes student or eligibility data, and never treats missing
source data as a passing rule. Attendance/CRT, resume-score, and student
queries use their owning service contracts rather than direct table access.

The Offer Path slider and AI Coach wording are Pallavi-owned frontend work;
TPO nudge delivery uses Ashish's notification service. Contract dependencies
and unimplemented surfaces are tracked in `docs/OFFER_PATH_INTEGRATION.md`.

## Visual foundation

The shared visual language and shell are owned by Pallavi. The foundation uses
white modular surfaces, a thin student journey rail, a notched rounded-card
shape, saffron primary actions, indigo Institute and teal Placement accents,
Manrope UI text, and Fraunces display headings. Keep focus visible, contrast
accessible, layouts responsive, and decorative motion reduced when requested.
Surendra's scoped auth-page tokens are in
`frontend/src/auth/placement-assistant-foundation.css`; they do not replace
shared styles or implement Offer Path UI.
