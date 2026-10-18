# One-Day Build Plan

| Gate | Suggested time | Exit condition |
|---|---|---|
| G0 | 09:00–09:30 | PRD, versions, API and data model agreed |
| G1 | 09:30–10:15 | Backend and frontend skeletons build |
| G2 | 10:15–12:15 | Module branches compile; ownership respected |
| G3 | 12:15–12:45 | PRs reviewed, tests and diff checks reported |
| G4 | 12:45–14:00 | Backend connects to local MySQL |
| G5 | 14:00–15:15 | Frontend integrated with API and states handled |
| G6 | 15:15–16:15 | Core flows and 401/403 checks pass |
| G7 | 16:15–17:00 | Clean-checkout instructions and known limits documented |
| G8 | 17:00–17:30 | Freeze, rehearse and prepare demo |

## Stop-the-line gates
- Do not start parallel implementation until contracts are agreed.
- Do not merge a PR that breaks the build.
- Do not start a large refactor after the integration gate.
- Keep a working demo over adding extra features.
