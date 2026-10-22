# Initial Data Model Proposal

> Historical starter proposal. The role values below are legacy seed examples,
> not the current canonical role list and not approved mappings. Use the
> current PRD and `docs/API_CONTRACTS.md` before designing schema changes.

Agree on names and relationships before writing repositories. Keep this model small for the demo.

## Entities
- `User`: id, displayName, email, passwordHash (never plaintext), enabled
- `Role`: id, name (`ROLE_ADMIN`, `ROLE_FACULTY`, `ROLE_STUDENT`, `ROLE_TPO`)
- `StudentProfile`: id, userId, studentCode (synthetic), department, semester, cgpa
- `AcademicSummary`: may be a DTO/projection rather than a table; earned credits, backlogs, CGPA
- `Course`: id, code, title, semester
- `ClassSession`: id, courseId, facultyUserId, startsAt, room
- `AttendanceRecord`: id, classSessionId, studentId, status, markedAt; unique constraint on classSession + student
- `Company`: id, name, description
- `PlacementDrive`: id, companyId, roleTitle, deadline, minimumCgpa, allowedDepartments, active
- `Application`: id, driveId, studentId, status, appliedAt; unique constraint on drive + student
- `Announcement`: id, title, body, audience, createdBy, createdAt

## Rules
- Use IDs consistently and document whether they are UUIDs or numeric IDs.
- Use `BigDecimal` for decimal values such as CGPA where appropriate.
- Use `Instant`/`LocalDateTime` consistently; document timezone handling.
- Never seed real student personal data.
- Prefer Flyway or another approved migration approach if time allows; do not mix migration strategies.
