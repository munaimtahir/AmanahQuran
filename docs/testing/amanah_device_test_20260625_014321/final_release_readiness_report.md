# Amanah Quran Device Test Report

## 1. Test Summary

- Date/time: 2026-06-25 01:43-03:04 PKT
- Tester/agent: OpenAI Codex device-test agent
- Device: TECNO CH6i, physical device
- Android version: 13, SDK 33
- CPU/display: arm64-v8a, 1080x2460, 480 dpi
- APK: `/home/munaim/Documents/github/AmanahQuran/apps/android/app/build/outputs/apk/debug/app-debug.apk`
- APK version: 0.1.0 (1), debug, 23,441,314 bytes
- APK SHA-256: `2b83fc6bd2e9316891a065f46ef01b1f4d4d5f77576327d12af342385b753574`
- Network state: airplane mode enabled; Wi-Fi and mobile data disabled
- Overall result: **FAIL**

The standard Surah/Juz/Page reader is fast and offline, but public release is blocked by wrong canonical ayah navigation, a 13.7-second Continue Reading path, and Trust Center data that explicitly states Quran content is not verified/pending review.

## 2. Installation Result

- Clean uninstall/install: PASS.
- Package present: PASS.
- First cold launch: PASS.
- First launch TotalTime: 2,923 ms.
- Cold relaunch after reinstall: 2,254 ms.
- Login, permission prompt, ad, donation popup: none observed.

## 3. Offline Result

- Offline reading: PASS for standard Surah/Juz/Page reader.
- Offline search: PARTIAL. Results load offline, but `Yaseen` returns nothing and `2:255` opens the wrong location.
- Offline bookmarks: PARTIAL. Storage/add/remove work; ayah bookmark navigation opens the wrong location.
- Offline Trust Center: loads offline, but its verification state is a release blocker.

## 4. Performance Result

| Test | Script | Mode | TTFQC ms | Target | Result |
| ---- | ------ | ---- | -------: | -----: | ------ |
| Al-Fatihah | IndoPak | Surah | 284 | <300 | PASS |
| Al-Baqarah | IndoPak | Surah | 285 | <700 | PASS |
| At-Tawbah | IndoPak | Surah | 230 | <500 | PASS |
| Juz 1 | IndoPak | Juz | 267 | <1000 | PASS |
| Juz 30 | IndoPak | Juz | 191 | <1000 | PASS |
| Page 1 | IndoPak | Page | 288 | <300 | PASS |
| Page 59 | IndoPak | Page | 171 | <300 | PASS |
| Page 76 | IndoPak | Page | 160 | <300 | PASS |
| Page 532 | IndoPak | Page | 152 | <300 | PASS |
| Page 540 | IndoPak | Page | 144 | <300 | PASS |
| Page 540 bookmark | Uthmani | Page | 138 | <300 | PASS |
| Al-Ikhlas search-open | Uthmani | Surah | 287 | <300 | PASS |
| Page 559 search-open | Uthmani | Page | 259 | <300 | PASS |
| Continue Reading Page 540 | Uthmani | Mushaf | ~13,720 | <300 | FAIL |

Standard reader DB queries returned in 28-91 ms and block mapping completed quickly. The major performance defect is isolated to the separate Mushaf/Continue Reading path and its script-state initialization.

## 5. Quran Reader Layout Result

- Surah headings present: yes for observed Al-Fatihah, Al-Baqarah, At-Tawbah, Al-Ikhlas.
- Bismillah present where expected: yes in observed cases.
- Al-Fatihah duplicate Bismillah: no duplication observed.
- At-Tawbah Bismillah incorrectly present: no.
- Juz/Para headings present: yes for observed Juz 1.
- Juz 30 boundaries: not verified by a valid screenshot; the existing named screenshot contains launcher content.
- Page boundaries correct: observed tested pages showed expected page number and canonical start content.
- Normalized search text rendered as Quran display text: no evidence observed.

## 6. Script Result

- IndoPak render: PASS on standard reader.
- Uthmani render: PASS on standard reader and Mushaf after load.
- Script switch identity preserved: PARTIAL. Global Settings reload of Page 540 preserves page/ayah identity.
- In-reader script switch: unavailable.
- Missing glyph/tofu observed: no.
- Continue Reading script behavior: FAIL. The Mushaf screen initially loads stale IndoPak state for 13,392 ms before Uthmani state replaces it.

## 7. Functional Result

- Surah navigation: PASS for observed cases.
- Juz navigation: timing PASS; Juz 30 visual-boundary evidence incomplete.
- Page navigation: PASS for pages 1, 2, 59, 76, 532, 540 and packaged last page 559.
- Search: FAIL due `2:255` opening at 2:1 and `Yaseen` returning no result. `1`, `36`, `Ikhlas`, `Juz 30`, and `Page 540` work offline.
- Bookmarks: FAIL for ayah navigation; PASS for page bookmark and removal.
- Last-read: identity persists, but performance FAIL due ~13.7-second load.
- Settings: script and themes persist.
- Elder Mode: larger layout persists; Sepia was not reliably exposed in the initial Elder Mode settings hierarchy.
- Themes: Light, Dark, and Sepia applied; Sepia persisted after force-stop.
- Trust Center: FAIL for release consistency and verification status.

## 8. Privacy/Permission Result

- Dangerous permissions: none.
- Runtime permission prompts: none.
- Internet permission/dependency: none identified; core functions operated offline.
- Ads/tracking SDK evidence: none in manifest/dependencies/app process.
- Login/account requirement: none.
- Internal AndroidX signature permission only: `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`.

Privacy scope result: PASS.

## 9. Stability Result

- Crash observed: no.
- ANR observed: no.
- OOM observed: no.
- Major app log errors: Mushaf load cancellation after 13,392 ms.
- Frame pacing: several 31-67 skipped-frame events.
- Gradle tests: PASS.
- Android lint: PASS.

## 10. Blockers

### P0 Blockers

1. Wrong canonical ayah navigation: Search result `2:255` and bookmark `2:255` both open Al-Baqarah at 2:1. The route logs carry the correct canonical key, but the reader ignores the anchor.
2. Trust Center contradiction: checksum is `N/A - prototype data`, validation is `NOT VERIFIED`, manual review is `PENDING REVIEW`, while release information says `APPROVED`. This conflicts with the app's verified-content promise.
3. Continue Reading/Mushaf reader shows a loading screen for approximately 13.7 seconds and starts with stale IndoPak state after Uthmani was selected.

### P1 Issues

1. `Yaseen` returns no search result while `36` correctly returns Ya-Sin.
2. No in-reader script switch; the user must leave the reader and use Settings.
3. IndoPak Trust Center source type is labeled `Search normalization` while notes describe it as a display candidate.
4. Elder Mode can make the Sepia theme control inaccessible/off-screen in the initial Settings hierarchy.
5. Several skipped-frame events indicate main-thread stalls.
6. Juz 30 visual-boundary evidence must be re-captured; the existing named screenshot is invalid.

### P2 Issues

1. Compiler warnings for unused `page` parameter and unused test variable.
2. Cold launch was 2.25-2.92 seconds; no explicit launch target was supplied, but optimization is desirable.

## 11. Recommendation

**BLOCK public release**

The build is suitable for continued internal debugging only. Do not claim verified Quran content publicly while the bundled Trust Center reports `NOT VERIFIED` and `PENDING REVIEW`. Fix canonical ayah anchors and the Continue Reading script/load race before another release-readiness run.

## 12. Evidence Links

- `device_info.txt`
- `install_log.txt`
- `app_launch_log.txt`
- `full_logcat.txt`
- `performance_raw.csv`
- `performance_summary.md`
- `functional_test_matrix.md`
- `privacy_permission_audit.txt`
- `crash_anr_summary.txt`
- `screenshots/`
- `screenrecordings/`

Important screenshot caveat: `screenshots/08_juz_30_boundaries.png` is not valid Juz 30 evidence and must not be cited as such.

## Instrumentation Change

Debug-only reader timing logs are guarded by `BuildConfig.DEBUG`.

Changed instrumentation file:

- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ReaderPerfLogger.kt`

The logger uses tag `AMANAH_PERF_READER` at INFO level so timestamps are visible on the physical device. No Quran text was modified.

## Test Gates

- `./gradlew assembleDebug`: PASS.
- `./gradlew test`: PASS, `BUILD SUCCESSFUL in 5m 44s`.
- `./gradlew lintDebug`: PASS, `BUILD SUCCESSFUL in 1m 23s`.
- Lint report: `apps/android/app/build/reports/lint-results-debug.html`.

## Coverage Limitations

- Arabic phrase search was not executed because ADB text injection could not reliably enter Arabic on this device.
- Juz 4 and Juz 15 were not measured in the interrupted initial run and were not repeated in the resumed pending-only scope.
- Surahs Aal-e-Imran, Ya-Sin, Al-Mulk, Al-Falaq, and An-Nas do not have completed screenshot/timing evidence in this folder.
- Ayah bookmark persistence across a force-stop was not separately repeated before removal; page bookmark persistence was observed.
