# Phase 0 Preflight Report

## Project Path
- Android project root: `apps/android`

## Current Project Status
- Baseline Android app builds successfully before any new sprint changes.
- Existing reader MVP, settings, search, bookmarks, trust center, theme persistence, elder mode, and last-read support are already present from the prior sprint.
- The packaged Quran content database is present as a read-only asset and is used by the app.

## Baseline Validation
- `./gradlew test` - passed
- `./gradlew :app:assembleDebug` - passed
- `./gradlew :app:lintDebug` - passed

## V1 Scope
- Offline Quran reading.
- Surah navigation.
- Juz navigation.
- Page navigation.
- Last-read.
- Bookmarks.
- Offline search.
- Elder Mode.
- Light / Dark / Sepia / System themes.
- Trust Center.
- Source attribution and verification.

## Existing Files Relevant to This Sprint
- Content DB and Room entities/DAOs under `apps/android/app/src/main/kotlin/org/amanahquran/app/core/database/`
- Repositories under `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/`
- Reader UI under `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/`
- Navigation under `apps/android/app/src/main/kotlin/org/amanahquran/app/core/navigation/`
- Search UI under `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/search/`
- Bookmarks UI under `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/bookmarks/`
- Settings UI under `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/`
- Trust Center UI under `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/`

## Proposed Files To Modify
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/database/dao/AyahDao.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/database/dao/MushafLayoutReferenceDao.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/QuranContentRepository.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/BookmarkRepository.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/SearchRepository.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/navigation/AppRoute.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/navigation/AmanahQuranNavHost.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/*`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/bookmarks/*`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/search/*`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/home/*`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/*`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/*`
- `docs/content/*`
- `docs/_implementation/amanah_page_juz_non_adb/*`

## Tests To Run
- `./gradlew test`
- `./gradlew :app:assembleDebug`
- `./gradlew :app:lintDebug`
- Sprint-specific unit tests for repository, navigation, reader, bookmarks, search, and trust center behavior

## Verdict
- GO
- Baseline passes and the page/juz metadata needed for navigation is available in the packaged database.
