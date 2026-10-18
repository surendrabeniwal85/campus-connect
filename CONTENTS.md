# Starter Kit Contents

- Root setup: README, ignore/config files, `.env.example`
- Product and engineering docs: PRD, architecture, API, data model, toolchain, team ownership, contribution rules, tests, security/privacy, demo script, Phase 2 backlog, one-day plan, AI working rules and prompt pack
- `frontend/`: a responsive React + TypeScript + Vite starter dashboard
- `backend/`: Spring Initializr setup instructions and sample configuration
- `database/schema.sql`: local schema starter and role seed
- `.github/`: pull request template and CI workflow
- `.vscode/extensions.json`: recommended editor extensions
- `scripts/verify.ps1`: local verification helper

Important: the backend Maven project and wrapper are not pre-generated in this zip. Follow `backend/README.md` to generate them in the `backend/` directory, then commit the generated files. The frontend dependency lockfile is also generated locally with `npm install`.
