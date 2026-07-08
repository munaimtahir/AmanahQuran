# Repo Map

## Top level

- `AGENTS.md`
- `README.md`
- `apps/`
- `content-pipeline/`
- `docs/`
- `graphics/`
- `projectdata/`
- `scripts/`
- `source/`
- `sourcedata/`
- `templates/`
- `tools/`
- `testing/`

## Android app

- `apps/android/build.gradle.kts`
- `apps/android/settings.gradle.kts`
- `apps/android/app/build.gradle.kts`
- `apps/android/app/src/main/AndroidManifest.xml`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/`
- `apps/android/app/src/main/assets/database/quran.db`
- `apps/android/app/src/main/assets/trust/trust_center_content.json`
- `apps/android/app/src/main/res/font/`
- `apps/android/app/src/main/res/drawable/`

## Key runtime packages

- `core/database`
- `core/datastore`
- `core/model`
- `core/navigation`
- `core/repository`
- `core/theme`
- `core/trust`
- `core/ui`
- `feature/home`
- `feature/reader`
- `feature/search`
- `feature/bookmarks`
- `feature/settings`
- `feature/trust`

## Content and evidence

- `content-pipeline/` contains pipeline stages, generated assets, and validation reports.
- `projectdata/managed/` contains generated import and validation evidence.
- `docs/legal/` contains legal/source evidence and checksums.
- `docs/testing/` contains device-run evidence folders.
- `docs/content/` contains current status notes.
- `docs/_release_gate/` contains release-gate reports.

## Current architecture

- Compose UI with Navigation Compose.
- Room-backed local content database.
- DataStore-backed settings and user state.
- Local-only bookmark and last-read persistence.
- Trust Center reads local JSON plus local DB metadata.
- Search uses a separate normalized search index.

## Current package namespace

- `org.amanahquran.app`

## Notes

- There is no web app implementation.
- There is no backend/service dependency for the core reader.
- The repo already contains real data, not just placeholders.

