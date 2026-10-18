# Security and Privacy Checklist

- Use synthetic student and placement data in development/demo.
- Never commit API keys, passwords, JWT secrets or `.env` files.
- Store password hashes, never plaintext passwords.
- Enforce authorization in Spring Security and service layer as needed.
- Validate all input on the server; encode/escape output appropriately.
- Avoid logging tokens, passwords or sensitive personal data.
- Do not claim the demo login is production authentication.
- Attendance GPS, geofencing and photos are explicitly out of scope for this MVP.
- Before collecting real student data, review consent, purpose, access control, retention, deletion and institutional policy.
- Use HTTPS, secure secret management, backups, audit logging and dependency scanning before any production deployment.
