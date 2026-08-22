# Amanah Quran V2.2 Discovery Report

Date: 2026-08-22  
Starting branch: `main`  
Starting HEAD: `1ab0aad` (`v2.2.0`)  
Working tree: clean before this sprint

## Current project status

The repository contains a production-shaped Android Compose application under
`apps/android/app`, backed by two prepackaged Room databases: verified Quran
content and the dual translation pack. The current app already includes the
Sacred Reader baseline, Continuous/Ayah reader behavior, dual English/Urdu
translations, bookmarks/last-read, search, Elder Mode, themes, Trust Center,
reading activity, streak/calendar, reminders, statistics, goals-adjacent
dashboard UI, and versioned local backup/restore.

The requested V2.2 gaps found during discovery are the reusable Daily Ayah
engine and history surface, an Android widget/deep-link surface, and an audio
abstraction/playback boundary. No approved reciter/audio catalogue is present
in repository evidence, so authentic audio activation must remain disabled until
source permission is supplied. Reading presets and explicit personal-history
surfaces also need consolidation with the existing local activity engine.

## Technical baseline

- Kotlin 2.1 / Compose compiler plugin; AGP 9.0.0; Gradle 9.1.0.
- `compileSdk`/`targetSdk` 36; `minSdk` 24; Java 17.
- Room 2.7.0-alpha01, DataStore Preferences 1.1.1, WorkManager 2.9.1.
- 153 main Kotlin files, 43 unit-test files, 2 instrumentation-test files at discovery.
- No ad, analytics, account, cloud-sync, or tracking dependency found.
- The app declares only the opt-in Android 13 notification permission and a private FileProvider.

## V1 scope preserved

Offline Quran reading; IndoPak/Uthmani scripts; Surah/Juz/Page navigation;
canonical ayah identity; last-read; bookmarks; offline search; Elder Mode;
System/Light/Dark/Sepia themes; Trust Center and content provenance.

## Existing files and assets

- `apps/android/app/src/main/kotlin/org/amanahquran/app/` — application code.
- `apps/android/app/src/main/assets/database/quran.db` — verified Quran/content DB.
- `apps/android/app/src/main/assets/content/translations/` — verified English/Urdu pack.
- `apps/android/app/src/main/assets/trust/trust_center_content.json` — provenance metadata.
- `scripts/` and `tools/content-import/` — offline content validation/import tooling.
- `docs/legal/`, `docs/_release_gate/`, and `TRANSLATION_INTEGRATION_FINAL_VERIFICATION.md` — source/license evidence.

## Baseline gate

`./gradlew :app:testDebugUnitTest :app:assembleDebug :app:lintDebug --no-daemon` passed after fixing a clean-checkout build defect: the font gate now falls back to the tracked `assets/trust/font_manifest.json` when ignored pipeline metadata is unavailable. Debug font glyph reports remain unavailable because Python `fontTools` is not installed; checksum enforcement still runs.

## Proposed implementation files

- `core/daily/` for Daily Ayah selection, persistence, and canonical retrieval.
- `feature/daily/` for the home/history presentation and deep-link integration.
- `widget/` plus Android widget resources and manifest registration.
- `core/audio/` for approved-source-neutral audio contracts and disabled state.
- `core/model`/`core/repository` for reading presets and personal history.
- Targeted unit tests for deterministic selection, anti-repeat, backup-safe state, and audio mapping.
- The required V2.2 evidence reports and deferred-review ledger.

No Quran display text or translation text will be edited.

## Planned tests

- Existing full unit, debug build, lint, release content gates.
- Daily selection/date rollover/anti-repeat and canonical join tests.
- Widget provider update/deep-link tests where Android test infrastructure permits.
- Audio mapping contract tests with unavailable-source behavior.
- Activity/streak/reminder/statistics/backup regression tests.
- Emulator discovery and representative UI validation; physical-device plan if no device is connected.
