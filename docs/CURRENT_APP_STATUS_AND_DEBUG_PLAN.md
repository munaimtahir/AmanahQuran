# Amanah Quran Current Status and Debug Plan

Date: 2026-06-26

## Current Status

Overall status: **BLOCK public release**.

Focused P0 anchor fix status: **implemented and focused-device verified**.

Latest valid device evidence:

- `docs/testing/amanah_device_test_20260625_014321/`

Invalid/non-evidence folder:

- `docs/testing/amanah_device_test_after_p0_fixes_20260625_061944/`
- Reason: report, log, CSV, and audit files are empty.

## What Works

- Clean install and offline launch passed.
- No login, ads, donation popup, permission prompt, or network requirement observed.
- Standard Surah/Juz/Page reader opens offline.
- IndoPak and Uthmani render without tofu observed.
- Privacy/permission audit passed: no dangerous permissions or ad/tracking SDK evidence.
- No crash, ANR, or OOM observed in the valid device run.
- `assembleDebug`, unit tests, and `lintDebug` passed in the valid run.

## Key Performance Evidence

| Action | TTFQC | Result |
|---|---:|---|
| Al-Fatihah | 284 ms | PASS |
| Al-Baqarah | 285 ms | PASS |
| At-Tawbah | 230 ms | PASS |
| Juz 1 | 267 ms | PASS |
| Juz 30 | 191 ms | PASS |
| Page 540 | 144 ms | PASS |
| Al-Ikhlas search-open | 287 ms | PASS |
| Continue Reading Page 540 | ~13,720 ms | FAIL |

## Release Blockers

P0:

- Full post-fix physical-device regression is still required before changing
  release readiness.
- Continue Reading Page 540/Uthmani must be re-tested specifically; the focused
  post-fix run verified Continue Reading to `2:255`, not the original Page 540
  scenario.
- Trust Center/release metadata must remain conservative until signed review and
  release evidence are complete.

P1:

- `Yaseen` was previously reported as no-result; current repository tests pass
  `Yaseen`, `Yasin`, `Ya-Sin`, `Ya Sin`, `يس`, and `36`.
- Juz 30 boundary screenshot must be recaptured.
- In-reader script switch is unavailable; switching is through Settings.
- Elder Mode can make some Settings controls harder to access.

## Post-Fix Focused Evidence

Device:

- TECNO CH6i, serial `08357252AE006901`.
- APK installed with `adb install -r apps/android/app/build/outputs/apk/debug/app-debug.apk`.

Search `2:255`:

- UIAutomator result row: `Al-Baqarah`, `2:255`.
- Route log: `nav_click search->reader anchor=ExactAyah(ayahKey=2:255)`.
- Reader mode: `AyahTarget(surahNumber=2, ayahKey=2:255)`.
- DB query: `rows=1`.
- First content composed: `+156ms`.
- UI showed `2:255` and `Current`; no `2:1` opening observed.

Bookmark `2:255`:

- UIAutomator bookmark row: `Al-Baqarah`, `2:255`.
- Route log: `nav_click bookmark->reader anchor=ExactAyah(ayahKey=2:255)`.
- Reader mode: `AyahTarget(surahNumber=2, ayahKey=2:255)`.
- First content composed: `+210ms` from cache.
- UI showed `2:255` and `Current`; no `2:1` opening observed.

Continue Reading `2:255`:

- Route log: `continue_click ayah=2:255 page=40`.
- Reader mode: `AyahTarget(surahNumber=2, ayahKey=2:255)`.
- First content composed: `+262ms` from cache.
- UI showed `2:255` and `Current`.

Coordinates used where UIAutomator provided bounds:

- Search card: bounds `[552,1802][1032,2057]`, tap `792,1930`.
- Search field: bounds `[48,348][1032,553]`, tap `540,450`.
- `2:255` result row: bounds `[48,765][1032,1120]`, tap `540,942`.
- Bookmark ayah button: bounds `[876,928][1020,1072]`, tap `948,1000`.
- Bookmarks card: bounds `[48,2081][528,2232]`, tap `288,2156`.
- Bookmark row: bounds `[48,324][1032,665]`, tap `500,520`.
- Continue Reading button: bounds `[96,652][984,808]`, tap `540,730`.

## Files Changed In This Debug Pass

- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ReaderViewModel.kt`
- `apps/android/app/src/test/kotlin/org/amanahquran/app/feature/reader/ReaderMvpViewModelTest.kt`
- Documentation/status files updated under `docs/`.

## Tests Run In This Debug Pass

- `./gradlew testDebugUnitTest --tests org.amanahquran.app.feature.reader.ReaderMvpViewModelTest`: PASS.
- `./gradlew testDebugUnitTest --tests org.amanahquran.app.core.repository.SearchRepositoryTest`: PASS.
- `./gradlew testDebugUnitTest --tests org.amanahquran.app.core.repository.TrustCenterRepositoryTest`: PASS.
- `./gradlew assembleDebug`: PASS.
- `./gradlew test`: PASS.
- `./gradlew lintDebug`: PASS.

## Debugging Plan

1. Run fresh full physical-device regression.
   - Exact ayah anchors are fixed in focused verification; now repeat the broader
     release suite with new non-empty artifacts.
   - Include Search `2:255`, Bookmark `2:255`, Continue Reading, script switch,
     Page 540 in both scripts, Trust Center, and Juz 30 boundaries.

2. Re-test Continue Reading Page 540/Uthmani.
   - Reproduce the original Page 540 scenario after the `AyahTarget` fix.
   - Confirm no stale IndoPak state appears after Uthmani is selected.
   - Target Page/Continue Reading TTFQC under 300 ms after warmup.

3. Fix Trust Center release metadata if release wording still conflicts.
   - Remove prototype/not-verified contradictions from any release candidate.
   - Keep conservative wording unless signed reviewer evidence and checksums are complete.
   - Add tests for Trust Center status parsing if practical.

4. Recapture Juz 30 boundary evidence.
   - The previous named screenshot was invalid and must not be cited.

5. Prepare release-gate evidence package.
   - Use a new timestamped folder.
   - Ensure reports/logs/CSV/screenshots are non-empty before citing them.

## Scope Guardrails

- Do not modify Quran display text to make tests pass.
- Do not render normalized search text as Quran display text.
- Do not add ads, analytics, tracking, login, cloud sync, donations, audio, translations, tafsir, AI, or other non-V1 features.
- Keep all timing instrumentation guarded by `BuildConfig.DEBUG`.
