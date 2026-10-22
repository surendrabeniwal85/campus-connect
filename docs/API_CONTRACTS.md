# Campus Connect API Contracts — Proposed Draft

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
- Validation errors should identify invalid fields without exposing secrets.

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

## 4. HTTP status conventions

| Status | Documented behavior or proposal |
|---|---|
| `400 Bad Request` | **Documented example:** used for `VALIDATION_ERROR`. Invalid-field identification is required, but its response structure is not specified. |
| `401 Unauthorized` | **Documented requirement:** use when the request is unauthenticated. |
| `403 Forbidden` | **Documented requirement:** use when the user is authenticated but forbidden from the requested action. |
| `404 Not Found` | **Proposal for review:** the existing docs do not specify when or how this status is used. |
| `409 Conflict` | **Proposal for review:** the existing docs do not specify conflict behavior or response codes. |

The API proposal asks for meaningful HTTP status codes but does not define a complete status-code policy. The `404` and `409` entries above are not established requirements.

## 5. Field-level validation details

**Documented requirement:** Validation errors should identify invalid fields without exposing secrets.

**Proposal for team review:** Extend the five-field error envelope with an optional `errors` array for field-level details. The existing documentation does not define this extension’s name, structure, or required fields.

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

**Documented proposal:** The data model and SQL schema identify these four role names:

- `ROLE_ADMIN`
- `ROLE_FACULTY`
- `ROLE_STUDENT`
- `ROLE_TPO`

The SQL schema seeds these role names in `app_role`. Their presence in the schema does not define all role permissions.

## 7. Proposed endpoint access rules

The following paths and access descriptions are listed in `docs/API.md` as initial proposals, except for the implemented `POST /api/v1/auth/login` contract described in Section 9. Paths in the table are relative to `/api/v1`; the full-path column shows the corresponding proposed version-prefixed paths. The health path remains unresolved as described above.

| Method | Relative path | Proposed full path | Purpose | Suggested access |
|---|---|---|---|---|
| `GET` | `/health` | `/api/v1/health` | Health check; `docs/API.md` also mentions `/actuator/health` if configured | Public/internal |
| `GET` | `/actuator/health` | `/api/v1/actuator/health` | Alternative health-check path mentioned in `docs/API.md`, if configured | Public/internal |
| `POST` | `/auth/login` | `/api/v1/auth/login` | Database-backed JWT bearer login | Public |
| `GET` | `/students/me/summary` | `/api/v1/students/me/summary` | Current student's academic summary | Student |
| `GET` | `/students/{studentId}/summary` | `/api/v1/students/{studentId}/summary` | View a student's summary | Faculty/Admin with permission |
| `GET` | `/attendance/sessions` | `/api/v1/attendance/sessions` | List sessions | Faculty/Student |
| `POST` | `/attendance/sessions/{sessionId}/records` | `/api/v1/attendance/sessions/{sessionId}/records` | Mark attendance | Faculty |
| `GET` | `/attendance/me/summary` | `/api/v1/attendance/me/summary` | Current student's attendance summary | Student |
| `GET` | `/placements/drives` | `/api/v1/placements/drives` | List drives | Student/TPO |
| `GET` | `/placements/drives/{driveId}/eligibility` | `/api/v1/placements/drives/{driveId}/eligibility` | Eligibility and reasons | Student/TPO |
| `POST` | `/placements/drives/{driveId}/applications` | `/api/v1/placements/drives/{driveId}/applications` | Apply to a drive | Student |
| `GET` | `/announcements` | `/api/v1/announcements` | List announcements | Authenticated users |
| `POST` | `/announcements` | `/api/v1/announcements` | Create announcement | Faculty/TPO/Admin |

The two health rows represent the unresolved alternatives, not a decision to provide both routes. The docs require server-side access enforcement and distinguish unauthenticated (`401`) from authenticated-but-forbidden (`403`) requests. Exact permissions, including what “with permission” means, still need team agreement.

## 8. Unresolved implementation decisions

- **Health endpoint:** Choose between `/health` and `/actuator/health`, and clarify whether the selected path is under `/api/v1`.
- **Permissions:** Confirm exact role-to-endpoint rules and any additional resource-level checks before implementation.
- **Validation error details:** Decide whether to retain only the five-field envelope or approve an optional field-level extension.
- **Additional status codes:** Decide whether and when `404` and `409` should be used, since the existing docs do not define their behavior.
- **Database migrations:** `database/schema.sql` defines `app_user`, `app_role`, and `user_role`, including foreign-key relationships and role seed values. `docs/DATA_MODEL.md` proposes additional entities and says to agree on names and relationships; it also recommends choosing a migration approach and not mixing strategies. The team must approve the migration strategy before persistence work relies on the schema.

## Decisions requiring team approval before implementation

1. Health-check path and its versioning treatment.
2. Exact role permissions and resource-level authorization rules.
3. Validation error extension, if any, and conventions for `404` and `409`.
4. Database model alignment and migration strategy.

## 9. JWT bearer authentication contract

Authentication is stateless. The backend authenticates credentials against the database using the existing password-hash verification, then returns a signed, short-lived JWT. Protected routes accept the token only in the `Authorization` request header. The backend does not establish an HTTP session or issue authentication cookies. CSRF protection is disabled for this bearer-header design.

### Login

`POST /api/v1/auth/login` accepts JSON containing an email and password:

```http
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{
  "email": "student@example.test",
  "password": "<user-provided-password>"
}
```

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

### Frontend coordination

The client should keep the access token in memory and attach it as a bearer header to API requests. It does not need to send session cookies or bootstrap a CSRF token. A `401` should return the user to authentication; a `403` remains the authenticated-but-forbidden response. If the frontend and backend are deployed on different origins, configure CORS for the frontend origin and the `Authorization` header; do not enable credentialed cookie auth for JWT requests.
