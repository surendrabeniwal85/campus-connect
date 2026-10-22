# AI Coach Design

The coach is intended to use transparent, deterministic rules based on available eligibility, attendance, academic, and ATS facts. Each recommendation must state the action and why it is suggested. The coach must not create hidden rejection, ranking, or eligibility decisions.

## Missing information

Recommendations must not infer unavailable facts. The UI identifies missing inputs and presents an unavailable state until relevant data is supplied through agreed service interfaces.

## Privacy and optional language models

Rule-based guidance is the default. No external model integration is implemented. Any future optional integration requires separate approval, explicit student consent, removal of name/email/phone/address, exclusion of marks, an environment-supplied key, a bounded timeout, and deterministic rule-based fallback. Never log resume text or personal data.

## Integration boundary

The current repository has no approved dashboard or coach API DTO, nor agreed service interfaces for academic, attendance, or placement facts. The frontend card accepts recommendation view data only; backend access and endpoint implementation must wait for those contracts.
