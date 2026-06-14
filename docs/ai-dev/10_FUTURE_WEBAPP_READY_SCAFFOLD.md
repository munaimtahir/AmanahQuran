# 10 — Future Web-App Ready Scaffold

## Purpose

The repository should be organized so a web app can be added later without disturbing the Android V1 app.

This is a planning document only. Do not implement the web app in V1.

## Recommended Direction

Use a monorepo-style structure:

```text
/
  apps/
    android/
    web/
  packages/
    shared/
  docs/
  tools/
```

## Why This Helps

This layout allows:

- Android V1 to remain focused.
- Web app to be added later under `/apps/web`.
- Shared contracts to be documented under `/packages/shared`.
- Content import and validation tools to remain platform-neutral.
- Documentation to remain central in `/docs`.

## Android V1 Location

Preferred if starting fresh:

```text
/apps/android
```

Inside it, keep normal Android files:

```text
/apps/android/app
/apps/android/settings.gradle.kts
/apps/android/build.gradle.kts
```

## Web App Placeholder

Allowed now:

```text
/apps/web/README.md
/apps/web/.gitkeep
```

The web README should state:

```text
The Amanah Quran web app is future scope.
Do not implement web features during Android V1.
The future web app must follow the same verified-content, no-ads, no-tracking, no-login-by-default, and privacy-first principles.
```

## Shared Package Placeholder

Allowed now:

```text
/packages/shared/README.md
/packages/shared/schemas/
```

Allowed contents:

- JSON schemas.
- Data contracts.
- Manifest templates.
- Validation report templates.

Not allowed in V1:

- Shared runtime SDK.
- Backend client.
- Auth logic.
- Cloud sync logic.
- Web UI components.

## Tools Folder

Recommended:

```text
/tools/content-import
/tools/validation
/tools/release
```

These tools should serve both Android and future web app by producing verified, platform-neutral content artifacts.

## Future Web App Principles

The future web app must:

- Use verified Quran content.
- Show Trust Center/source transparency.
- Avoid ads and tracking.
- Avoid forced login.
- Work offline where technically feasible, such as PWA caching.
- Avoid making the Android app dependent on web infrastructure.

## V1 Boundary

Do not build:

- Web reader.
- Web routing.
- Web styling.
- PWA.
- Backend API.
- Cloud sync.
- User accounts.

Only create placeholders and contracts.
