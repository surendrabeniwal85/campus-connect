# API Contract — Initial Proposal

All endpoints use `/api/v1`. JSON is the default request/response format. These are contracts to agree on before implementation, not claims that all endpoints already exist.

| Method | Endpoint | Purpose | Suggested role |
|---|---|---|---|
| GET | `/health` | Health check (may be `/actuator/health` if configured) | Public/internal |
| POST | `/auth/login` | Demo sign-in | Public for demo only |
| GET | `/students/me/summary` | Current student's academic summary | Student |
| GET | `/students/{studentId}/summary` | View a student's summary | Faculty/Admin with permission |
| GET | `/attendance/sessions` | List sessions | Faculty/Student |
| POST | `/attendance/sessions/{sessionId}/records` | Mark attendance | Faculty |
| GET | `/attendance/me/summary` | Current student's attendance summary | Student |
| GET | `/placements/drives` | List drives | Student/TPO |
| GET | `/placements/drives/{driveId}/eligibility` | Eligibility and reasons | Student/TPO |
| POST | `/placements/drives/{driveId}/applications` | Apply to a drive | Student |
| GET | `/announcements` | List announcements | Authenticated users |
| POST | `/announcements` | Create announcement | Faculty/TPO/Admin |

## Contract rules
- Keep response field names stable; coordinate changes before merging.
- Return DTOs, never JPA entities.
- Use meaningful HTTP status codes.
- Validation errors should identify invalid fields without exposing secrets.
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
