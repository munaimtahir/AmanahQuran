# Executive Summary

Current evidence says Amanah Quran is past scaffold stage and into an internal-testing Android build with real Quran content, runtime fonts, local state, and offline navigation.

## Current project status

- Platform: Android first.
- App identity: Amanah Quran / Amanah-e-Kisa.
- Core stack: Kotlin, Jetpack Compose, Room, DataStore, Navigation Compose.
- Packaged content: `quran.db`, `trust_center_content.json`, bundled reader fonts.
- Build health: debug build, unit tests, and lint pass.
- Release health: release build is blocked by a Gradle script-path bug in the content pipeline task.
- Release approval: still blocked by Trust Center/manual-review evidence.

## V1 scope

The repo is implementing the Sacred Reader MVP only:

- Offline Quran reading.
- IndoPak and Uthmani scripts.
- Script switching.
- Surah, Juz, and Page navigation.
- Ayah-level reading.
- Last-read.
- Bookmarks.
- Offline search.
- Elder Mode.
- Themes.
- Trust Center.
- Source attribution and verification.

## Existing files

- Root docs, PRD, architecture, QA, and release-gate documents already exist.
- Android app exists under `apps/android/` with a real Compose app.
- Packaged Quran database exists at `apps/android/app/src/main/assets/database/quran.db`.
- Trust Center JSON exists at `apps/android/app/src/main/assets/trust/trust_center_content.json`.
- Active reader fonts exist in `apps/android/app/src/main/res/font/`.
- Content pipeline scripts exist at repo root in `scripts/`.
- Validation and source evidence live under `content-pipeline/`, `projectdata/managed/`, `docs/legal/`, and `docs/testing/`.

## Proposed files to modify next

These are the first code files that need attention after discovery:

- `apps/android/app/build.gradle.kts`
- `apps/android/app/src/main/assets/trust/trust_center_content.json`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/TrustCenterRepository.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/SearchRepository.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ReaderViewModel.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterViewModel.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/SettingsScreen.kt`

## Tests to run

- `./gradlew testDebugUnitTest`
- `./gradlew assembleDebug`
- `./gradlew lintDebug`
- `./gradlew assembleRelease`
- Physical-device regression for:
  - search `2:255`
  - bookmark `2:255`
  - Continue Reading
  - Page 540 in both scripts
  - Juz 30 boundary
  - Trust Center

## Bottom line

This is not a stub. It is an internal-testing build with real content and real offline functionality, but public release is still blocked.

