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
Base package: `com.campusconnect`

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
