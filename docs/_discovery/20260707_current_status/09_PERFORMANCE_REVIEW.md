# Performance Review

## What the code suggests

The current architecture is performance-friendly for an offline Quran reader:

- Room database access is local.
- Search uses a separate normalized index.
- Reader blocks are cached in `ReaderBlocksCache`.
- Reader load paths log timing through `ReaderPerfLogger`.
- Settings and last-read are local DataStore reads.
- The database provider adds indices for ayah, page, and script lookups.

Relevant code:

- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ReaderViewModel.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/database/AmanahContentDatabaseProvider.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/SearchRepository.kt`

## Existing device evidence from docs

The latest device reports in `docs/testing/` show:

- Standard Surah/Juz/Page reader paths were fast.
- Search/bookmark exact-anchor issues were tracked in earlier release-gate evidence.
- Continue Reading on the old Page 540 path was slow in earlier evidence.
- Later focused evidence says exact ayah anchors were fixed for `2:255`, but that was not rerun in this discovery pass.

## Likely performance state

- Ordinary reader open paths: ACCEPTABLE to GOOD.
- Search: ACCEPTABLE.
- Script switching: ACCEPTABLE, with timing still worth rechecking.
- Continue Reading Page 540 legacy path: NEEDS VALIDATION / POSSIBLE OPTIMIZATION.

## Targets to re-check on device

- Cold start under 2 seconds.
- Reader screen under 500 ms after DB warmup.
- Search under 1 second.
- Script switch under 300 to 500 ms.

## Bottom line

The code shape is good, but this discovery pass did not rerun the physical-device timing suite. Performance is promising, not newly proven.

