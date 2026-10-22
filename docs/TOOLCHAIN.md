# Toolchain and Version Record

This records repository declarations and the local toolchain observed on
21 October 2026. It does not verify the deployed database or every developer's
machine. Coordinate version changes before upgrading dependencies mid-sprint.

| Tool | Repository baseline | Local observation / note |
|---|---|---|
| JDK | Java 25 (`backend/pom.xml`) | Java 25.0.1 |
| Spring Boot | 4.1.1 (`backend/pom.xml`) | Use the repository declaration |
| Maven | Maven Wrapper | Wrapper reports Maven 3.10.0 |
| MySQL | 8.4 LTS target | Active server/deployed schema not verified |
| Node.js | 24.x LTS target | Node.js 24.15.0 |
| React / Vite | React + TypeScript + Vite | `package-lock.json` records resolved packages; package manifest uses `latest` specifiers |
| VS Code | Stable | No exact version pinned |

## VS Code extensions
- Extension Pack for Java
- Spring Boot Extension Pack / Spring Boot Tools
- ESLint
- Prettier
- GitHub Pull Requests (optional)

All teammates should use the same IDE/editor and pinned toolchain for the sprint to reduce environment differences.
