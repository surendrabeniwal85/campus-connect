# Placement Assistant Backend

This directory contains the Placement Assistant Spring Boot API.
- Project: Maven
- Language: Java
- Spring Boot: 4.1.1 (`pom.xml`)
- Java: 25
- Group: `com.campusconnect`
- Artifact: `placement-assistant-api`
- Package: `com.placementassistant.backend`
- Dependencies: Spring Boot Actuator, Spring Data JPA, Validation, Web MVC, Spring Security, OAuth2 Resource Server, MySQL Connector/J, and H2 for tests

The Maven project, wrapper, authentication API, and tests already exist. Do not
regenerate the project or create another Git repository in this directory.

Environment variables expected by local configuration:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Use `application.yml.example` as safe configuration documentation. The local
`application.yml` is developer configuration and must not be committed.

Suggested module boundaries are in `../docs/ARCHITECTURE.md`. The database
starter schema is not a migration baseline; check the current migration
decision in `../docs/API_CONTRACTS.md` before adding persistence.
