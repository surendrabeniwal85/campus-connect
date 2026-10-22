# Placement Assistant Design System

The shared UI uses CSS variables and the existing React/Vite application. Tokens are defined in `frontend/src/design-system/tokens.css`; reusable components and their styles live alongside them. No new runtime dependency is required.

## Themes and accents

The default theme is light. Apply `data-theme="dark"` to a containing element to use the dark palette; the design-system demo includes a theme switch. Components consume semantic tokens so colors remain legible across both palettes.

- Institute: indigo-to-violet (`--color-institute-start`, `--color-institute-end`).
- Placement: teal-to-emerald (`--color-placement-start`, `--color-placement-end`).
- Shared semantic tokens cover canvas, surface, text, muted text, borders, focus, success, warning, and error.
- Typography, spacing, radii, shadows, and transitions are also expressed as CSS variables.

## Components

`frontend/src/design-system/components.tsx` exports `Button`, `Card`, `DataTable`, `TextField`, `Modal`, `Toast`, `Skeleton`, `EmptyState`, `LoadingState`, `ErrorState`, `ProgressRing`, `RoundTimeline`, `FunnelChart`, and `SectionTitle`.

Use explicit labels for form fields, keep table captions and column headers meaningful, and provide accessible names for progress indicators. Loading and error feedback use live/status semantics. Native dialog behavior provides keyboard dismissal and focus management. Focus-visible outlines and reduced-motion preferences are built in.

## Preview

Choose **Design system** in the application navigation to view the examples in both themes. Use the shared components rather than duplicating styles in feature modules.
