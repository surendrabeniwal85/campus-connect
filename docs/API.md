# API Contract — Initial Proposal

All endpoints use `/api/v1`. JSON is the default request/response format. Except for the implemented database-backed login endpoint, these are proposed contracts to agree on before implementation, not claims that the routes already exist.

| Method | Endpoint | Purpose | Suggested role |
|---|---|---|---|
| GET | `/health` | Health check (may be `/actuator/health` if configured) | Public/internal |
| POST | `/auth/login` | Database-backed JWT login with portal validation | Public |
| GET | `/students/me/summary` | Current student's academic summary | `STUDENT` (candidate; access policy pending approval) |
| GET | `/students/{studentId}/summary` | View a student's summary | Pending approval; exact canonical roles and scope are unresolved |
| GET | `/attendance/sessions` | List sessions | Institute role access pending approval (`STUDENT`, `TEACHER`, `HOD`) |
| POST | `/attendance/sessions/{sessionId}/records` | Mark attendance | Institute role access pending approval (`STUDENT`, `TEACHER`, `HOD`) |
| GET | `/attendance/me/summary` | Current student's attendance summary | `STUDENT` (candidate; access policy pending approval) |
| GET | `/placements/drives` | List drives | Role access pending approval (`STUDENT`, `DIRECTOR_HR`, `TPO`, `D_TPO`, `PLACEMENT_COORDINATOR`) |
| GET | `/placements/drives/{driveId}/eligibility` | Eligibility and reasons | Role access pending approval (`STUDENT`, `DIRECTOR_HR`, `TPO`, `D_TPO`, `PLACEMENT_COORDINATOR`) |
| POST | `/placements/drives/{driveId}/applications` | Apply to a drive | `STUDENT` (candidate; access policy pending approval) |
| GET | `/announcements` | List announcements | Canonical role access pending approval |
| POST | `/announcements` | Create announcement | Canonical role and permission policy pending approval |

## Contract rules
- Keep response field names stable; coordinate changes before merging.
- Return DTOs, never JPA entities.
- Use meaningful HTTP status codes.
- Validation errors currently return the generic five-field error envelope
  below and do not identify invalid fields. Responses must not expose secrets.
  Field-level details are a proposed extension pending team approval.
- For access failures, return `401` when unauthenticated and `403` when authenticated but forbidden.

## Example error response
```json
{
  "timestamp": "2026-10-18T10:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "One or more fields are invalid.",
  "path": "/api/v1/placements/drives"
}
```

The current backend uses this five-field envelope for validation failures:
HTTP `400`, code `VALIDATION_ERROR`, and message `One or more fields are
invalid.` It does not include field names. Malformed request bodies use the
same envelope with code `MALFORMED_JSON` and a generic message.

Canonical roles are `STUDENT`, `TEACHER`, `HOD`, `DIRECTOR_HR`, `TPO`, `D_TPO`,
and `PLACEMENT_COORDINATOR`. Suggested endpoint access above is not an approved
permission matrix; role access and resource scopes remain pending team approval.
