# Backend setup

This directory contains backend setup notes. Generate the Spring Boot project here from Spring Initializr:
- Project: Maven
- Language: Java
- Spring Boot: selected 3.5.x patch
- Java: 25
- Group: `com.campusconnect`
- Artifact: `campus-connect-api`
- Package: `com.campusconnect`
- Dependencies: Spring Web, Spring Data JPA, Validation, Spring Security, MySQL Driver, Spring Boot Test

Then commit the generated `pom.xml`, source files, tests, Maven Wrapper and wrapper properties. Do not create a second Git repository inside this folder.

Environment variables expected by local configuration:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET` (only once an actual token implementation is added)

Suggested source package layout is in `../docs/ARCHITECTURE.md`.
