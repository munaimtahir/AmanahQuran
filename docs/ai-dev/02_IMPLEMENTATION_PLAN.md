# 02 — Implementation Plan

## Stage 0 — Repository and Android Project Setup

### Status
**COMPLETED**

## Stage 1 — Data Foundation (Mega Sprint)

### Status
**COMPLETED**

### Objective
Create Room database schema, data contracts, validation engine, manifest structures, repository scaffolding, and test coverage.

### Likely Files
- `/settings.gradle.kts`
- `/build.gradle.kts`
- `/apps/android/settings.gradle.kts` if monorepo style is selected
- `/apps/android/app/build.gradle.kts`
- `/apps/android/app/src/main/AndroidManifest.xml`
- `/apps/web/README.md`
- `/packages/shared/README.md`

### Acceptance Criteria
- Android app builds.
- Package namespace is stable.
- Placeholder screens can be added.
- Future web placeholder exists but contains no implementation.
- Existing `/docs` files are untouched.

### Tests Required
- Gradle sync/build.
- Basic unit test placeholder.

### Do-Not-Cross Boundaries
- Do not implement Quran data.
- Do not add network.
- Do not add analytics, ads, login, or cloud sync.
- Do not build web app yet.

## Stage 1 — Design System and Navigation Shell

### Objective
Create Compose theme, navigation routes, and empty placeholder screens.

### Likely Files
- `core/theme`
- `core/navigation`
- `feature/home`
- `feature/reader`
- `feature/search`
- `feature/bookmarks`
- `feature/settings`
- `feature/trust`

### Acceptance Criteria
- Home, Reader, Search, Bookmarks, Settings, and Trust Center screens exist.
- Navigation works.
- Theme system supports System, Light, Dark, and Sepia placeholders.
- No external SDKs added.

### Tests Required
- Compose smoke test if feasible.
- Navigation route unit tests if implemented as pure route definitions.

### Do-Not-Cross Boundaries
- Do not add Quran text yet.
- Do not add audio/translation/tafsir.

## Stage 2 — Room Database Schema

### Objective
Create local schema for Surah, Ayah, QuranText, SearchIndex, Bookmarks, LastRead, ContentSource, and ContentValidation.

### Acceptance Criteria
- Room database compiles.
- DAO tests cover basic insert/read where applicable.
- QuranText display fields are clearly immutable in code convention.
- SearchIndex is separate from QuranText.

### Tests Required
- Room DAO tests.
- Schema export enabled.

### Do-Not-Cross Boundaries
- Do not use live network APIs.
- Do not fetch Quran data at runtime.

## Stage 3 — Content Manifest and Validation Pipeline

### Objective
Prepare content manifest and validation report structure.

### Acceptance Criteria
- Content manifest template exists.
- Validation report template exists.
- Validation checks include 114 Surahs and 6236 ayahs.
- Build/release gate can fail if validation metadata is missing.

### Tests Required
- Validation function tests.
- Manifest parsing tests.

### Do-Not-Cross Boundaries
- Do not accept unlicensed or unknown sources.
- Do not import normalized text as display Quran text.

## Stage 4 — Quran Reader

### Objective
Implement offline reader UI connected to local database/stub data until verified content is imported.

### Acceptance Criteria
- Reader supports Surah/Juz/Page modes.
- Reader renders Arabic text safely.
- Reader works without internet.
- No popups or ad spaces exist.

### Tests Required
- Reader ViewModel tests.
- UI smoke tests.

### Do-Not-Cross Boundaries
- Do not modify Quran display text.
- Do not add interpretation, tafsir, or translation.

## Stage 5 — Script Switching

### Objective
Allow switching between IndoPak and Uthmani display text.

### Acceptance Criteria
- Script selection persists.
- Bookmarks and last-read remain canonical.
- Search previews use current script.

### Tests Required
- DataStore setting tests.
- ViewModel script selection tests.

### Do-Not-Cross Boundaries
- Do not convert one script into another at runtime.

## Stage 6 — Last-Read Position

### Objective
Save and restore reading position locally.

### Acceptance Criteria
- Continue Reading opens last ayah/page.
- Works offline.
- Survives app restart.
- Uses canonical references.

### Tests Required
- Repository tests.
- ViewModel tests.

### Do-Not-Cross Boundaries
- Do not sync to cloud.

## Stage 7 — Bookmarks

### Objective
Implement local ayah and page bookmarks.

### Acceptance Criteria
- Add/remove ayah bookmark.
- Add/remove page bookmark.
- Bookmark list opens reader position.
- Script switching does not break bookmarks.

### Tests Required
- DAO tests.
- Repository tests.
- ViewModel tests.

### Do-Not-Cross Boundaries
- Do not add collections/notes unless separately approved for later phase.

## Stage 8 — Offline Search

### Objective
Implement local search for Surah, ayah reference, juz, page, and Arabic text.

### Acceptance Criteria
- Search works offline.
- Normalized search uses separate SearchIndex.
- Results render verified display text only.

### Tests Required
- Search parser tests.
- Search repository tests.
- Arabic normalization tests.

### Do-Not-Cross Boundaries
- Do not display normalized Quran text.
- Do not use server search.

## Stage 9 — Elder Mode

### Objective
Create large-text, simplified UI mode.

### Acceptance Criteria
- Larger Arabic text.
- Larger buttons.
- Simplified reader controls.
- Persists across restart.
- Works with all themes.

### Tests Required
- Settings tests.
- UI smoke tests.

### Do-Not-Cross Boundaries
- Do not create separate app flavor.

## Stage 10 — Themes

### Objective
Implement System, Light, Dark, and Sepia themes.

### Acceptance Criteria
- Theme persists.
- Reader remains readable.
- Elder Mode works with themes.

### Tests Required
- Settings tests.
- UI smoke tests.

### Do-Not-Cross Boundaries
- Do not use decorative backgrounds that reduce readability.

## Stage 11 — Trust Center

### Objective
Display source, checksum, validation, no-modification, and privacy pledge.

### Acceptance Criteria
- Trust Center works offline.
- Shows source per script.
- Shows checksum and validation status.
- Shows no ads/no tracking/no login pledge.

### Tests Required
- Manifest parsing tests.
- Trust ViewModel tests.

### Do-Not-Cross Boundaries
- Do not hide source details behind web links.

## Stage 12 — QA and Release Hardening

### Objective
Verify V1 before release.

### Acceptance Criteria
- Content validation passed.
- Offline mode passed.
- No dangerous permissions.
- No ads/tracking/login dependencies.
- Device testing completed.
- Release checklist completed.

### Tests Required
- Full test suite.
- Dependency audit.
- Permission audit.
- Manual QA checklist.
