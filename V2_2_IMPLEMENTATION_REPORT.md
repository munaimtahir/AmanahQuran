# Amanah Quran V2.2 Implementation Report

## Completed

- Repaired clean-checkout font validation by adding a tracked manifest fallback while retaining SHA-256 enforcement.
- Added a single persisted Daily Ayah engine with canonical `ayah_key`, deterministic daily selection, 30-entry local history, translation identity capture, and reviewed-pool extension points.
- Added Home Daily Ayah presentation and Daily Ayah History navigation.
- Added a responsive Android App Widget provider, local rendering, resize metadata, offline operation, and exact ayah deep links.
- Added source-neutral authentic-audio contracts with an honest unavailable implementation until a source is approved.
- Preserved existing dual translations, reader modes, activity/streak/calendar/reminder/statistics/search/Elder Mode/Trust Center/backup behavior.

## Files changed

See `git diff --stat` and the traceability matrix. New implementation packages are under `apps/android/app/src/main/kotlin/org/amanahquran/app/core/daily`, `core/audio`, `feature/daily`, and `widget`.

## Tests run

- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon` — PASS, 270 unit tests.
- `./gradlew :app:lintDebug --no-daemon` — PASS.
- Daily selector unit tests — included in the 270-test suite and PASS.
- `./gradlew :app:assembleRelease -PamanahReleaseTrack=public --no-daemon` — DEFERRED at signing guard; no credentials.

## Known issues

- Audio is not activated without approved source/licence evidence.
- Curated Daily Ayah mode awaits a reviewed eligibility dataset; sequential mode is active and deterministic.
- No physical device or AVD is available in the current environment.
- Release pipeline source workspace is ignored and absent from this clean checkout; see release gate report.

## Scope guardrail

No ads, analytics, tracking, login, cloud sync, monetization, AI-generated religious content, or network-dependent reader behavior was added. Quran and translation display text were not modified.
