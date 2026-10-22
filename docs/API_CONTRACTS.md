# Placement Assistant API Contracts — Proposed Draft

> **Status:** Draft for team approval. This document consolidates proposals from `docs/API.md`, `docs/ARCHITECTURE.md`, `docs/DATA_MODEL.md`, `docs/SECURITY_AND_PRIVACY.md`, and `database/schema.sql`. It does not describe implemented API behavior.

## 1. Base path and versioning

**Documented proposal:** API endpoints use the `/api/v1` base path. JSON over HTTP is the documented communication format.

**Path convention for the endpoint table:** Paths are shown both relative to `/api/v1` and with the proposed `/api/v1` prefix. The prefixed paths are proposed paths, not confirmation that routes exist.

**Unresolved health path:** `docs/API.md` lists `GET /health` and notes that it may instead be `/actuator/health` if configured. It does not resolve whether either path is under `/api/v1`. The table shows the corresponding version-prefixed candidates only to reflect the general base-path proposal; the team must decide the actual health path and versioning treatment.

## 2. JSON responses and DTOs

**Documented requirements:**

- JSON is the default request and response format.
- Keep response field names stable and coordinate changes before merging.
- Return DTOs from REST endpoints; do not return JPA entities directly.
- Use meaningful HTTP status codes.
- Validation errors currently use the generic five-field envelope described
  below and do not include field-level details. Responses must not expose
  secrets. Any field-level extension remains subject to team approval.

The existing documentation does not define a general success-response envelope or endpoint-specific response schemas. Those contracts should be agreed before implementing individual endpoints.

## 3. Error response

**Documented proposal:** Use this five-field error envelope:

```json
{
  "timestamp": "2026-10-18T10:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "One or more fields are invalid.",
  "path": "/api/v1/placements/drives"
}
```

The fields are:

| Field | Proposed meaning |
|---|---|
| `timestamp` | Time the error response was produced |
| `status` | HTTP status code |
| `code` | Stable machine-readable error code |
| `message` | Human-readable summary |
| `path` | Request path associated with the error |

The docs provide this example but do not specify timestamp formatting, the complete set of error codes, or exact messages. Those details remain to be agreed. Validation responses must not expose secrets.

**Current implementation:** `GlobalExceptionHandler` returns this five-field
shape for validation failures with status `400`, code `VALIDATION_ERROR`, and
message `One or more fields are invalid.` It does not include invalid field
names. Malformed request bodies use the same envelope with code
`MALFORMED_JSON` and a generic message.

## 4. HTTP status conventions

| Status | Documented behavior or proposal |
|---|---|
| `400 Bad Request` | **Current behavior:** used for `VALIDATION_ERROR`; the response does not identify invalid fields. Any field-level extension remains pending approval (see Section 5). |
| `401 Unauthorized` | **Documented requirement:** use when the request is unauthenticated. |
| `403 Forbidden` | **Documented requirement:** use when the user is authenticated but forbidden from the requested action. |
| `404 Not Found` | **Proposal for review:** the existing docs do not specify when or how this status is used. |
| `409 Conflict` | **Proposal for review:** the existing docs do not specify conflict behavior or response codes. |

The API proposal asks for meaningful HTTP status codes but does not define a complete status-code policy. The `404` and `409` entries above are not established requirements.

## 5. Field-level validation details

**Current behavior:** Validation errors use the generic five-field envelope
described in Section 3 and do not identify invalid fields. This preserves the
current backend response contract.

**Proposal pending team approval:** If field-level validation details are
required, extend the five-field error envelope with an optional `errors` array.
The existing documentation does not define this extension’s name, structure,
or required fields. Do not implement the extension until approved.

For discussion only:

```json
{
  "timestamp": "2026-10-18T10:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "One or more fields are invalid.",
  "path": "/api/v1/example",
  "errors": [
    {
      "field": "exampleField",
      "message": "The value is invalid."
    }
  ]
}
```

This is a proposed extension requiring approval. It does not replace or alter the documented five-field error envelope.

## 6. Roles

**Legacy schema values:** The starter SQL schema seeds these role names:

- `ROLE_ADMIN`
- `ROLE_FACULTY`
- `ROLE_STUDENT`
- `ROLE_TPO`

The SQL schema seeds these names in `app_role`; they do not define the current canonical portal roles or all role permissions. The application currently defines the seven canonical roles and portal associations as follows:

- Institute: `STUDENT`, `TEACHER`, `HOD`
- Placement: `DIRECTOR_HR`, `TPO`, `D_TPO`, `PLACEMENT_COORDINATOR`

No automatic mapping from legacy `ROLE_ADMIN` or `ROLE_FACULTY` to a canonical role is defined.

## 7. Proposed endpoint access rules

The following paths and access descriptions are listed in `docs/API.md` as initial proposals, except for the implemented `POST /api/v1/auth/login` contract described in Section 9. Paths in the table are relative to `/api/v1`; the full-path column shows the corresponding proposed version-prefixed paths. The health path remains unresolved as described above.

| Method | Relative path | Proposed full path | Purpose | Suggested access |
|---|---|---|---|---|
| `GET` | `/health` | `/api/v1/health` | Health check; `docs/API.md` also mentions `/actuator/health` if configured | Public/internal |
| `GET` | `/actuator/health` | `/api/v1/actuator/health` | Alternative health-check path mentioned in `docs/API.md`, if configured | Public/internal |
| `POST` | `/auth/login` | `/api/v1/auth/login` | Database-backed JWT bearer login | Public |
| `GET` | `/students/me/summary` | `/api/v1/students/me/summary` | Current student's academic summary | `STUDENT` (candidate; pending approval) |
| `GET` | `/students/{studentId}/summary` | `/api/v1/students/{studentId}/summary` | View a student's summary | Pending approval; exact roles and scope are unresolved |
| `GET` | `/attendance/sessions` | `/api/v1/attendance/sessions` | List sessions | Pending approval; Institute role access is unresolved |
| `POST` | `/attendance/sessions/{sessionId}/records` | `/api/v1/attendance/sessions/{sessionId}/records` | Mark attendance | Pending approval; Institute role access is unresolved |
| `GET` | `/attendance/me/summary` | `/api/v1/attendance/me/summary` | Current student's attendance summary | `STUDENT` (candidate; pending approval) |
| `GET` | `/placements/drives` | `/api/v1/placements/drives` | List drives | Pending approval; student and Placement role access is unresolved |
| `GET` | `/placements/drives/{driveId}/eligibility` | `/api/v1/placements/drives/{driveId}/eligibility` | Eligibility and reasons | Pending approval; student and Placement role access is unresolved |
| `POST` | `/placements/drives/{driveId}/applications` | `/api/v1/placements/drives/{driveId}/applications` | Apply to a drive | `STUDENT` (candidate; pending approval) |
| `GET` | `/announcements` | `/api/v1/announcements` | List announcements | Pending approval; authenticated role access is unresolved |
| `POST` | `/announcements` | `/api/v1/announcements` | Create announcement | Pending approval; exact roles and scope are unresolved |

The two health rows represent the unresolved alternatives, not a decision to provide both routes. The docs require server-side access enforcement and distinguish unauthenticated (`401`) from authenticated-but-forbidden (`403`) requests. The canonical roles are `STUDENT`, `TEACHER`, `HOD`, `DIRECTOR_HR`, `TPO`, `D_TPO`, and `PLACEMENT_COORDINATOR`; the endpoint rows do not approve assignments or permissions. Exact role access and resource scope remain pending team approval.

## 8. Unresolved implementation decisions

- **Health endpoint:** Choose between `/health` and `/actuator/health`, and clarify whether the selected path is under `/api/v1`.
- **Permissions:** Confirm exact role-to-endpoint rules and any additional resource-level checks before implementation.
- **Validation error details:** Confirm whether the current generic five-field envelope is sufficient or approve an optional field-level extension before changing the response format.
- **Additional status codes:** Decide whether and when `404` and `409` should be used, since the existing docs do not define their behavior.
- **Database migrations:** `database/schema.sql` defines `app_user`, `app_role`, and `user_role`, including foreign-key relationships and role seed values. `docs/DATA_MODEL.md` proposes additional entities and says to agree on names and relationships; it also recommends choosing a migration approach and not mixing strategies. The team must approve the migration strategy before persistence work relies on the schema.
- **Migration baseline and account/student persistence:** The repository contains no tracked Flyway migrations. The starter `database/schema.sql` is documented as a local manual setup, not a Flyway baseline, and its role seeds are legacy values. Before V10/V20 work, Aliza and the team must identify the deployed V1–V9 schema/history or explicitly approve a non-destructive baseline strategy for fresh and existing databases. Do not enable automatic baselining or map `ROLE_ADMIN`/`ROLE_FACULTY` without approval.
- **Student data rules:** The Team Guide requires the mentor to provide field ranges, grade scale, and credit rules. These are not defined in the repository requirements; agree them before validating marks, results, CGPA, semester progression, or CSV rows.
- **Reset-password request contract:** The PRD specifies a one-time expiring reset token; the Team Guide lists `POST /auth/forgot-password` and `POST /auth/reset-password`. Neither defines request/response fields, token lifetime, delivery channel, persistence, or generic response requirements. An OTP flow would be a change from the current PRD contract and requires explicit product/team approval. Establish the approved contract and persistence/delivery mechanism before exposing endpoints or a functional reset form. Do not return a success-shaped placeholder.
- **Master-account boundaries:** HOD account operations are department-scoped and Director HR manages placement staff, but the existing schema has no department association for users and no approved department/placement-staff parent schema. Agree the parent data contract and ownership integration before implementing account writes or audit records.
- **Offer Path integration:** See `docs/OFFER_PATH_INTEGRATION.md` for the proposed evaluator, read-only simulation, TPO count, and notification data shapes. Aliza owns evaluator/simulation/count; Pallavi owns the Offer Path UI; Ashish owns notification delivery. No endpoint, rule thresholds, unknown-value policy for "one rule away," student measure ranges, or notification delivery contract is implemented or inferred here. Confirm these contracts with their owners before adding API rows or persistence.

## Decisions requiring team approval before implementation

1. Health-check path and its versioning treatment.
2. Exact role permissions and resource-level authorization rules.
3. Validation error extension, if any, and conventions for `404` and `409`.
4. Database model alignment and migration strategy.

## 9. JWT bearer authentication contract

Authentication is stateless. The backend authenticates credentials against the database using the existing password-hash verification, then returns a signed, short-lived JWT. Protected routes accept the token only in the `Authorization` request header. The backend does not establish an HTTP session or issue authentication cookies. CSRF protection is disabled for this bearer-header design.

### Login

`POST /api/v1/auth/login` accepts a portal, email, and password:

```http
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{
  "portal": "INSTITUTE",
  "email": "student@example.test",
  "password": "<user-provided-password>"
}
```

`portal` is required and is either `INSTITUTE` or `PLACEMENT`. Authentication is rejected with `403 ACCESS_DENIED` when the user's recognized role authorities do not all belong to the selected portal. Unknown and legacy role names are not implicitly mapped.

The current login endpoint does not yet issue refresh tokens, implement login lockout, or require a first-login password change. It issues only the existing 900-second access token.

Successful authentication returns the bearer token, a 900-second validity interval, and identity/roles:

```json
{
  "accessToken": "<signed-jwt>",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "email": "student@example.test",
  "roles": ["ROLE_STUDENT"]
}
```

The JWT is signed with HS256. It contains `iss` (`campus-connect-api`), `aud` (`campus-connect-api`), the authenticated email as `sub`, `iat`, `exp`, and a `roles` claim. The decoder validates the signature, issuer, audience, required identity/role/time claims, and expiry. The signing key must be supplied externally using `JWT_SECRET` and contain at least 32 UTF-8 bytes; do not commit it or log it.

### Authenticated requests and errors

Send the access token on each protected request:

```http
Authorization: Bearer <signed-jwt>
```

Missing or invalid authentication returns `401 Unauthorized` using the common five-field error envelope with code `AUTHENTICATION_REQUIRED`. An authenticated user without required permissions receives `403 Forbidden` with code `ACCESS_DENIED`. Incorrect credentials, unknown users, and disabled users continue to return the same generic `401` error with code `AUTHENTICATION_FAILED` and message `Invalid username or password.` Login request validation failures return `400 Bad Request` with code `VALIDATION_ERROR`. No authentication error response exposes credentials or password hashes.

### Logout and token lifetime

There is no server-side logout endpoint or revocation mechanism. The client can log out by discarding its copy of the access token. Discarding the token does not invalidate an already issued JWT; it remains valid until its 900-second expiry. The API does not issue refresh tokens.

## 10. Offer Path eligibility integration (proposed)

Offer Path is not implemented in this repository. The evaluator and
simulation/count operations belong to Aliza; the slider card and AI Coach
wording belong to Pallavi; TPO notification delivery belongs to Ashish.
Surendra owns authentication, student records, and agreed integration support.
See `docs/OFFER_PATH_INTEGRATION.md` for the minimum DTO shapes, read-only
requirements, data-owner dependencies, and unresolved semantics.

The existing drive eligibility path in Section 7 remains a proposal. No
simulation, one-rule-away count, or nudge route is approved. Do not expose one
until the owners agree the route, permissions, request/response DTOs, missing
data behavior, persistence baseline, and notification semantics. Every
eligibility rule must return exactly `PASS`, `FAIL`, or `MISSING`, and the
simulation must use the same evaluator as actual eligibility without writes.

### Frontend coordination

The client should keep the access token in memory and attach it as a bearer header to API requests. It does not need to send session cookies or bootstrap a CSRF token. A `401` should return the user to authentication; a `403` remains the authenticated-but-forbidden response. If the frontend and backend are deployed on different origins, configure CORS for the frontend origin and the `Authorization` header; do not enable credentialed cookie auth for JWT requests.
