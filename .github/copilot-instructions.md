# Copilot instructions

## Project baseline

- Project: Placement Assistant — Powered by AI.
- Backend: Java 25, Spring Boot 4.1.1 (the version already pinned in
  `backend/pom.xml`), Maven Wrapper, Spring Data JPA, Spring Security, and MySQL
  8.4; tests use H2.
- Database changes use reviewed, versioned Flyway migrations when Flyway is
  available in the project. Do not add dependencies or alter tool versions
  without approval. Never edit an already-applied migration; coordinate new
  migration versions with the module owner.
- Frontend: the existing React + TypeScript + Vite setup. Preserve its current
  structure and styles. Do not add a router, Bootstrap, or other dependency
  without the frontend owner's approval.

## Ownership and scope

- Check `PROJECT_MAP.md` and `.github/CODEOWNERS` before changing files. Work
  only in the assigned module; ask the owner before changing shared files or
  another person's area.
- Do not change `pom.xml`, CI workflows, package manifests, or security
  configuration without explicit approval from the responsible owner.
- Preserve existing implementations. Do not regenerate scaffolding or replace
  the frontend.
- Keep `backend/src/main/resources/application.yml` local, untracked, and
  untouched. Use environment variables and the safe
  `application.yml.example`; never add credentials or secrets to source.
- Use synthetic student and placement data. Never expose passwords, JWT
  secrets, tokens, or personal information in logs, examples, or tests.

## API and authorization

- Use `/api/v1`, stable request/response field names, validated request DTOs,
  and response DTOs rather than returning persistence entities.
- Keep controllers thin; put business rules in services and use constructor
  injection.
- Enforce permissions on the backend for every protected action. The Institute
  roles are `STUDENT`, `TEACHER`, and `HOD`; Placement roles are `DIRECTOR_HR`,
  `TPO`, `D_TPO`, and `PLACEMENT_COORDINATOR`. A role must be permitted in
  the selected portal and for the requested resource.
- Respect module data ownership. Do not query another module's tables directly;
  use its owner-approved service/query interface.
- `docs/API_CONTRACTS.md` is currently a draft. It documents a five-field
  error envelope, while the Team Guide also mentions
  `{status, message, fieldErrors}`. Do not silently change the implemented
  authentication error contract or choose between these shapes without team
  approval.
- Preserve the existing stateless JWT behavior, claims, issuer/audience, token
  lifetime, and security rules unless an explicitly approved task changes them.

## Roles and portals

- Institute portal: Student, Teacher, HOD (Institute master).
- Placement portal: Director HR (Placement master), TPO, D_TPO, Placement
  Coordinator.
- Keep portal-specific access checks server-side. Do not treat hidden frontend
  navigation as authorization.

## Testing and safe changes

- Add or update tests with behavior changes. Use JUnit/MockMvc and H2 for
  backend tests; cover valid input, invalid input, empty/not-found cases where
  relevant, and forbidden access. Use synthetic fixtures only.
- Run backend verification with `cd backend; .\mvnw.cmd -B clean verify`.
- Run frontend checks with `cd frontend; npm run lint` and `npm run build`.
- Review the full diff. Do not run destructive Git commands or commit/push on
  behalf of the developer.
