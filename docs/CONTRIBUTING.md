# Contributing

## Branches
- `main`: demo/release branch
- `develop`: integration branch
- `feature/surendra-auth`
- `feature/aliza-academics`
- `feature/naman-attendance`
- `feature/ashish-placement`
- `feature/pallavi-ui-comms`
- `fix/<short-issue>`

Create feature branches from `develop`, then open a PR back to `develop`. Promote a tested snapshot to `main` for the demo.

## Before committing
```bash
git status
git diff --check
git diff
```
Stage only files you own. Use descriptive commits, for example:
```bash
git add backend/src/main/java/com/campusconnect/academic
git commit -m "Add student academic summary"
git push -u origin feature/aliza-academics
```

## Pull request checklist
- [ ] Scope matches assigned module
- [ ] No unrelated files changed
- [ ] No secrets or personal data committed
- [ ] Tests/build run and results are reported accurately
- [ ] API contracts are unchanged or coordinated
- [ ] Error/loading/empty UI states handled
- [ ] Known limitations documented
