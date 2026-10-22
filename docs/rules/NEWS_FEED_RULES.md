# News Feed Rules

The frontend news components are presentation-only and do not authorize access. The server must filter every feed and detail request for the authenticated user before returning a `NewsItem`.

## Types and presentation

Supported types: `DRIVE`, `TALK`, `HACKATHON`, `INTERNSHIP`, `OFF_CAMPUS`, and `ROUND_UPDATE`. Feed results are newest-first, ten items per page, and may be filtered by type. A card presents headline, type, publication date, summary, and an optional deadline. Selecting a card opens its details and any action must be supplied by the owning integration.

## Audience enforcement

- Off-campus items are visible to students in semester 7 or 8 only.
- Round updates are visible only to students associated with the relevant drive.
- Optional branch and department restrictions are enforced on the server.
- Frontend filtering is for presentation only and must never be treated as an authorization control.

## Integration boundary

The current API contract draft does not define the news response DTO, pagination query conventions, action-link fields, or publisher interface. The UI therefore accepts typed page data and callbacks rather than issuing network requests or assuming an endpoint response. Confirm these fields and `NewsPublisher.publish()` with the owning teammates before connecting API or publishing behavior.
