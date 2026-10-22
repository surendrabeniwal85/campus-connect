# Offer Path integration contract

## Ownership

- **Aliza** owns the authoritative placement eligibility evaluator, the
  read-only simulation operation, the per-drive one-rule-away count, and its
  integration with the existing eligibility engine.
- **Pallavi** owns the Offer Path slider card, its journey presentation, and
  AI Coach wording in the shared design system and shell.
- **Surendra** owns authentication, account and student-record foundation,
  access control, and agreed integration support. Surendra does not implement
  Aliza's eligibility engine or Pallavi's Offer Path feature UI without
  explicit reassignment.
- **Ashish** owns notification delivery and recipient behavior. TPO nudges
  must use that owner-approved service rather than writing notification data
  directly.

## Current repository status

The repository currently has no drive or eligibility entities, repositories,
services, controllers, persistence migrations, or notification service. The
existing `GET /api/v1/placements/drives/{driveId}/eligibility` row in
`docs/API_CONTRACTS.md` is a proposal, not an implemented endpoint. The current
Team Guide defines owner-service contracts, but those services are not
implemented: `StudentQueryService` returns `cgpa`, `activeBacklogs`,
`profileCompleteness`, `isFinalYear`, `branch`, `semester`, and `department`;
`AttendanceQueryService` returns `classPercent` and `crtPercent`;
`AtsQueryService` returns the latest ATS score (optional eligibility input);
`EligibilityService` returns `PASS`, `FAIL`, and `MISSING` with reasons;
`DriveQueryService` returns drives, criteria, and applicant lists; and
`NotificationService.send()` delivers push/inbox messages to approved
audiences. The dashboard contains illustrative placement values only; they
are not eligibility inputs.

## Required evaluator semantics

The shared evaluator is the sole authority for actual and simulated decisions.
Each evaluated rule must be represented as:

| Field | Meaning |
|---|---|
| `ruleId` | Stable identifier from the drive's configured eligibility rules |
| `label` | Human-readable rule name |
| `status` | Exactly `PASS`, `FAIL`, or `MISSING` |
| `reason` | Explanation of the result, including why data is missing |
| `observedValue` | Actual or simulated value when known; otherwise omitted |
| `requiredValue` | The configured criterion when one exists; never inferred by the client |

`MISSING` is not `PASS`, and a missing requirement must remain visible in all
eligibility paths and simulations. The server must return the configured rules
and reasons rather than letting the client hide or recreate them.

## Contract shapes to approve before implementation

The following shapes describe the integration data needed; they do not settle
the final route names, field rules, or status-code details. Confirm exact paths
and role permissions in `docs/API_CONTRACTS.md` with Aliza, Ashish, and the
team before exposing endpoints.

### Drive eligibility explanation

The existing draft path is `GET /api/v1/placements/drives/{driveId}/eligibility`.
Its response needs the drive identity, the authoritative overall outcome, and
the complete ordered rule list using the statuses above. No threshold or
eligibility formula is defined here.

```json
{
  "driveId": "server-defined-id",
  "outcome": "server-defined-eligibility-outcome",
  "rules": [
    {
      "ruleId": "server-defined-rule-id",
      "label": "Configured requirement",
      "status": "MISSING",
      "reason": "The required source data is unavailable."
    }
  ]
}
```

The example identifiers and text are illustrative only. The evaluator owner
must define the actual outcome vocabulary and rule identifiers.

### Read-only what-if simulation

Simulation input must identify the drive or drives being evaluated and contain
only explicit, user-supplied hypothetical overrides for supported measures:
attendance, CGPA, ATS/resume score, and backlog count. Omitted values mean
"use authoritative current data"; an explicitly unknown current value remains
`MISSING`. The server must validate overrides against approved field rules,
evaluate through the same engine as actual eligibility, and perform no writes
to student, result, attendance, resume, application, or eligibility records.
The Team Guide's source fields are `classPercent` for attendance, `cgpa`,
`latestAtsScore` for ATS/resume score, and `activeBacklogs` for backlog count.
`crtPercent` may appear in the actual required path when a drive rule uses it;
whether it is a simulation override is not specified and must be agreed by
Aliza and Naman.

The response must distinguish authoritative actual results from hypothetical
results, preserve each rule's `PASS`/`FAIL`/`MISSING` reason, list drives newly
unlocked by the simulation, and explain which rules changed and why. A
simulation must not create an application, update a persisted eligibility
record, invent an absent input, or suppress a rule.

The following DTO shape is the minimum information the UI needs; every
placeholder is intentionally untyped until the source model owners approve
units and validation. The route is not selected; this is not permission to
implement an endpoint under this example name.

```json
{
  "driveIds": ["server-defined-drive-id"],
  "overrides": {
    "attendance": "explicit hypothetical value",
    "cgpa": "explicit hypothetical value",
    "atsScore": "explicit hypothetical value",
    "backlogCount": "explicit hypothetical value"
  }
}
```

The response must include `actualResults` evaluated from persisted source data,
`simulatedResults` evaluated from that same engine with only supplied
overrides, and `newlyUnlockedDrives`. Each per-drive result includes the
authoritative outcome and complete `rules` list. Each newly unlocked entry
identifies the drive and explains which rules changed from `FAIL` to `PASS`.
Any `MISSING` rule stays present with its reason in both result sets unless
explicit, valid simulated input supplies that exact missing measure. The
simulation operation is read-only regardless of the proposed route.

```json
{
  "actualResults": [
    {
      "driveId": "server-defined-drive-id",
      "outcome": "server-defined-eligibility-outcome",
      "rules": []
    }
  ],
  "simulatedResults": [
    {
      "driveId": "server-defined-drive-id",
      "outcome": "server-defined-eligibility-outcome",
      "rules": []
    }
  ],
  "newlyUnlockedDrives": [
    {
      "driveId": "server-defined-drive-id",
      "changedRules": [
        {
          "ruleId": "server-defined-rule-id",
          "from": "FAIL",
          "to": "PASS",
          "reason": "Evaluator-provided explanation."
        }
      ]
    }
  ]
}
```

Outcome vocabulary, identifiers, reason wording, and empty-rule behavior belong
to Aliza's evaluator contract and remain to be agreed.

Progress cards may show the student's actual required path, including
attendance, CRT, and resume/ATS requirements only when those requirements and
their source data are returned by the approved evaluator and owner query
services. Do not assume that every drive uses all measures or fabricate a
measure when an owner service has no value.

### TPO one-rule-away count

The TPO view needs a per-drive count derived server-side from authoritative
student data and the same evaluator, not a client-side approximation. Before
implementation, the owners must define how a `MISSING` rule affects this
classification. The count must not classify a student as one rule away merely
because only one rule is currently known.

The count request identifies a drive; pagination applies only if a separately
approved authorized student-detail view is added. The count response needs
the drive identity, count, evaluation timestamp or data version, and a
documented definition of "exactly one rule away." A candidate definition is a
student whose rule results contain exactly one `FAIL` and `PASS` for every
other rule. Aliza and the team must approve this definition and decide how
`MISSING` affects classification; missing criteria must not be counted as
passing. Aggregate count responses must not expose student details.

### One-click nudge

The nudge action must be an authenticated, server-authorized TPO operation
using Ashish's notification service. It must target only the server-derived
one-rule-away audience for the selected drive, revalidate that audience at
send time, and return an outcome count without exposing private student data.
The request needs the selected drive identity and must not accept arbitrary
recipient identities from the client. The result needs a delivery/accepted
count and a safely reported failure count, not recipient PII. Endpoint path,
idempotency behavior, message content controls, audit event, and notification
result schema require approval by Aliza, Ashish, and the team.

## Dependencies and decisions

1. Aliza implements the documented `EligibilityService`, drive/criteria
   contract, explanation DTO, and simulation/count operations.
2. Surendra implements the documented `StudentQueryService` after student
   model rules, migration history, and baseline are approved. Student data is
   not synthesized for missing values.
3. Naman implements `AttendanceQueryService`; Pallavi implements
   `AtsQueryService`. Offer Path calls those owner-approved services and does
   not read their tables.
4. Ashish implements `NotificationService.send()` and approves access,
   idempotency, and audit behavior for TPO nudges.
5. The team approves migration history and database baseline before any
   persistence is added. Do not create an alternate eligibility implementation
   or invent drive/student relationships.
