# Phase 0 Preflight Report

Task: Amanah Quran V1 Reader MVP wiring from verified content database.

## Discovery

- Android project path: `apps/android`.
- Existing root Android project was not moved.
- Packaged content database asset exists at `app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite`.
- Trust Center JSON asset exists at `app/src/main/assets/trust/trust_center_content.json`.
- `QuranContentRepository` exists and reads Surah, Ayah, and Quran text data.
- DAOs exist for Surah, Ayah, QuranText, SearchIndex, ContentSource, and ContentValidation.
- `ContentProofScreen` exists at route `content-proof`.
- Home screen does not expose `ContentProofScreen`, so it remains internal/debug-only by route knowledge.
- Compose Navigation is present through `AmanahQuranNavHost` and `AppRoute`.

## Baseline Validation

Commands run from `apps/android` before app source changes:

- `./gradlew test` - passed.
- `./gradlew :app:assembleDebug` - passed.
- `./gradlew :app:lintDebug` - passed.

Existing content tests confirm:

- 114 Surahs.
- 6236 ayahs.
- 6236 Uthmani text rows.
- 6236 IndoPak text rows.
- 6236 search index rows.
- Sample ayahs `1:1` and `2:255` load in both scripts.
- Search-normalized rows are separate from display text.

## Guardrails

- No raw source files modified.
- No packaged SQLite contents modified.
- No Quran text regenerated.
- No fonts bundled.
- No runtime permissions added.
- No network-dependent reader dependency found.

## Verdict

GO.

Current app builds and content DB access exists.
