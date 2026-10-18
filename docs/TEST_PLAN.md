# Test Plan

## Backend
- Unit-test business rules in services.
- Use JUnit 5 and Mockito where appropriate.
- Use MockMvc for REST validation, status codes and authorization.
- Test unauthenticated `401` and forbidden `403` paths.
- Test duplicate attendance and duplicate placement applications.
- Test placement eligibility with both eligible and ineligible synthetic profiles.

## Frontend
- `npm run lint`
- `npm run build`
- Manually verify loading, success, empty and API-error states.
- Verify navigation and role-aware screens.
- Verify forms show validation feedback and prevent accidental duplicate submits.

## Demo smoke test
1. Start backend and MySQL.
2. Start frontend.
3. Sign in using only documented demo credentials if auth has been implemented.
4. Open academic summary.
5. Open attendance summary and show a session.
6. Open placement drives and show eligibility reasons.
7. Open announcements.
8. Confirm forbidden access is rejected by backend.
9. Confirm no real personal data or secrets are displayed.
