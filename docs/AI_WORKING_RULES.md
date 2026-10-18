# AI-Assisted Development Rules

Paste this guardrail into the coding assistant before asking it to edit files:

> This is a five-person shared repository. My branch and file ownership are fixed. Do not edit outside allowed files, overwrite uncommitted work, regenerate the project, upgrade dependencies, add libraries without approval, silently change API contracts, or claim tests passed unless they were run. Inspect `git status` and list planned files first. After edits, report changed files, commands/results, assumptions and issues. If a shared-file change is needed, stop and ask.

## Per-task prompt template
1. State the module and permitted files.
2. State the API contract and acceptance criteria.
3. Ask the assistant to inspect existing files before editing.
4. Ask for the smallest coherent change.
5. Ask it to run the relevant tests/build and report exact output.
6. Review every diff before committing. Never paste secrets into an AI chat.
