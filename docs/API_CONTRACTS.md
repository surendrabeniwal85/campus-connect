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

The following paths and access descriptions are listed in `docs/API.md` as an initial proposal, not as implemented routes. Paths in the table are relative to `/api/v1`; the full-path column shows the corresponding proposed version-prefixed paths. The health path remains unresolved as described above.

| Method | Relative path | Proposed full path | Purpose | Suggested access |
|---|---|---|---|---|
| `GET` | `/health` | `/api/v1/health` | Health check; `docs/API.md` also mentions `/actuator/health` if configured | Public/internal |
| `GET` | `/actuator/health` | `/api/v1/actuator/health` | Alternative health-check path mentioned in `docs/API.md`, if configured | Public/internal |
| `POST` | `/auth/login` | `/api/v1/auth/login` | Database-backed session login; supersedes the initial “Demo sign-in” proposal in Section 9, pending team approval | Public for proposed login |
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
- **Session login:** Review and approve the proposed session-based login contract in Section 9. Credential values are not specified; the proposed request fields `email` and `password` are documented in Section 9. No token format or production authentication guarantee is defined.
- **Permissions:** Confirm exact role-to-endpoint rules and any additional resource-level checks before implementation.
- **Validation error details:** Decide whether to retain only the five-field envelope or approve an optional field-level extension.
- **Additional status codes:** Decide whether and when `404` and `409` should be used, since the existing docs do not define their behavior.
- **Database migrations:** `database/schema.sql` defines `app_user`, `app_role`, and `user_role`, including foreign-key relationships and role seed values. `docs/DATA_MODEL.md` proposes additional entities and says to agree on names and relationships; it also recommends choosing a migration approach and not mixing strategies. The team must approve the migration strategy before persistence work relies on the schema.

## Decisions requiring team approval before implementation

1. Health-check path and its versioning treatment.
2. Proposed session-based login, CSRF bootstrap, and logout contract in Section 9.
3. Exact role permissions and resource-level authorization rules.
4. Validation error extension, if any, and conventions for `404` and `409`.
5. Database model alignment and migration strategy.

## 9. Proposed session-based authentication contract

> **PROPOSED — PENDING TEAM APPROVAL.** These request, response, CSRF, logout, and failure details extend the initial API proposal. They are not implemented behavior or an approved production authentication design.

Authentication uses server-side HTTP sessions. The login endpoint authenticates existing database users; it does not establish a JWT or return credentials. CSRF protection remains enabled for state-changing requests, including login and logout.

### Login

`POST /api/v1/auth/login` accepts JSON containing an email and password:

```http
POST /api/v1/auth/login
Content-Type: application/json
X-XSRF-TOKEN: <csrf-token>
Cookie: JSESSIONID=<session-cookie>; XSRF-TOKEN=<csrf-token>
```

```json
{
  "email": "student@example.test",
  "password": "<user-provided-password>"
}
```

On successful authentication, the server establishes a server-side session. The response contains identity and role names only; it must not include the submitted password, stored password hash, session identifier as JSON, or other authentication credentials.

```json
{
  "email": "student@example.test",
  "roles": ["ROLE_STUDENT"]
}
```

The session cookie is delivered by the HTTP session mechanism, not in the response JSON. The request and response examples are illustrative; credentials shown are placeholders, not demo credentials.

### Authentication failures and validation

Unknown users, disabled users, and incorrect passwords return the same generic authentication failure:

```json
{
  "timestamp": "2026-10-18T10:00:00Z",
  "status": 401,
  "code": "AUTHENTICATION_FAILED",
  "message": "Invalid username or password.",
  "path": "/api/v1/auth/login"
}
```

The response must not disclose whether an account exists or is disabled. Login request validation failures return `400 Bad Request` using the existing five-field error envelope with code `VALIDATION_ERROR`; they do not add the pending optional `errors` array.

### CSRF token bootstrap and client requests

`GET /api/v1/auth/csrf` bootstraps a CSRF token using a cookie-based token repository. The proposed cookie name is `XSRF-TOKEN`; the proposed request header name is `X-XSRF-TOKEN`. The endpoint is proposed and does not currently imply an implemented route.

The React client first requests the bootstrap endpoint, retains the CSRF cookie, and sends its value in `X-XSRF-TOKEN` on state-changing requests. Requests that need the server-side session must include cookies. The `JSESSIONID` session cookie must remain `HttpOnly`; the CSRF token cookie must be available to the client if the client is expected to copy it into the request header. Neither cookie value should be logged or returned as response data.

CSRF failures return `403 Forbidden`. Under the proposed common error-envelope convention, they should use the same five fields, with a generic message that does not reveal token internals. The exact CSRF error `code` and `message` remain subject to team approval.

### Logout

`POST /api/v1/auth/logout` requires a valid CSRF token. On success, the server invalidates the server-side session and expires/clears its session cookie, then returns `204 No Content` with no response body. This endpoint and behavior are proposed and are not currently implemented.

### Decisions for this authentication proposal

- Confirm the login request and response fields, including whether any identity field beyond `email` and `roles` is needed.
- Confirm `AUTHENTICATION_FAILED` and the generic message as the agreed 401 contract.
- Confirm the CSRF bootstrap endpoint, cookie (`XSRF-TOKEN`), and header (`X-XSRF-TOKEN`), including how the token is exposed to the browser.
- Confirm the proposed 403 error-envelope behavior and choose a stable CSRF error code and generic message.
- Confirm the logout path, CSRF requirement, 204 response, session invalidation, and session-cookie clearing behavior.
- Decide CORS policy and credentialed cross-origin request requirements if the frontend and backend use different origins.
- Decide cookie attributes for the deployment context, including `SameSite`, `Secure`, path, and lifetime; preserve `HttpOnly` for the session cookie.
