# Amanah Quran V2.1 implementation state

Updated: 2026-08-06

## Current verdict

`V2.1.0 RELEASE CANDIDATE — ENGINEERING GATES PASS, RELEASE AAB BUILT AND DEVICE-VERIFIED, AWAITING HUMAN UPLOAD DECISION`

READER-UX-02 (2026-08-06) added Continuous Mode (book-style flowing text) and parallel split-screen
translation on top of the READER-UX-01 foundation, plus a fix for a reported Surah-1 bug that grew
into cross-Surah/Juz continuous reading. Full details: `docs/_implementation/READER_UX_02_CONTINUOUS_AND_SPLIT_TRANSLATION_REPORT.md`.
Version bumped to 2.1.0 (versionCode 8). 145/145 unit tests pass, lint clean, signed release AAB/APK
built and content/DB-integrity gates pass. Both the Continuous/split-translation feature and the
Surah-1 continuity fix (including the "Al-Baqarah" title correctly appearing inline while scrolling
past Al-Fatihah) were confirmed live on a physical device, surviving a mid-session USB disconnect
and a device reboot; the release-signed artifact specifically was not separately re-installed on
hardware -- see the READER-UX-02 report's Section L for the exact remaining follow-up.

---

The V2 foundation, debug gates, public-track assembly, bundle (AAB) build, signing configuration, and attached-device smoke validation (both debug and release-signed builds) all pass as of 2026-08-04. Work in this pass:

1. Independently re-verified a prior agent's Sprint 4/8/9 changes against a real device; found and fixed a reader anchor-scroll offset bug and two Trust Center JSON key mismatches (translation pack wrongly reported "not installed", checksum hidden).
2. Removed reviewer-identity wording from the public Trust Center (kept only in internal `docs/legal`/`docs/_public_release_approval` records); fixed the same underlying source-attribution wording in `TrustCenterRepository.kt`/`TrustCenterScreen.kt`.
3. Resolved two of three Play Console pre-launch warnings: bumped `androidx.activity`/`activity-compose` to 1.13.0 for the Android 15 edge-to-edge handler, and excluded the unused `libdatastore_shared_counter.so` (verified via decompiled dependency inspection that this app's single-process DataStore usage never touches it, then confirmed on-device that settings still persist correctly after excluding it). The third warning (missing native debug symbols for `libandroidx.graphics.path.so`) is inherent to Jetpack Compose itself — Google ships that library pre-stripped with no symbols available to supply — and is not fixable at the application level; it is a known, non-blocking Play Console advisory affecting all Compose apps.
4. Redesigned Page Mode into a true fit-to-screen paged reader per request: a full mushaf page now renders shrunk-to-fit with no vertical scroll, pinch-to-zoom is available for close reading without altering the persisted font-size setting, and swipe left/right turns pages. Found and fixed two real bugs during device verification: (a) a `HorizontalPager` `NoSuchMethodError` crash caused by a stale/inconsistent `compose-bom` (2024.06.00) — re-pinned to 2026.06.01 so all Compose artifacts resolve to matching versions; (b) the pinch-zoom gesture detector was swallowing single-finger swipes and silently breaking page-turn navigation — replaced with a custom multi-touch-only gesture detector that leaves single-finger drags unconsumed for the pager.
5. Closed the previously-flagged bookmark-collections gap: added an "add to collection" folder icon and checkbox picker on each bookmark row, wired through the existing repository methods; verified end-to-end on-device (create collection → add bookmark → filter by collection).

All changes verified with 86/86 unit tests passing, lint clean, and direct on-device testing on a physical TECNO CH6i (Android 13) for both debug and R8-minified/resource-shrunk release builds. Final distribution still requires the normal external Play Console upload/review and a human decision to publish. Audio recitation is explicitly parked for V3.0 because no approved reciter catalogue and licence were selected.

### Known non-blocking gaps (not fixed in this pass)

- Full screen-by-screen TalkBack accessibility evidence capture was not completed (spot-checked Quick Actions labels only); flagged as a follow-up QA pass, not a release blocker.
- Deep performance profiling (airplane-mode confirmation, frame timing during scroll, low-end device) was not done beyond a quick cold-start/memory snapshot on the attached device.
- Android 16 (API 36) emulator UI verification: `Android_16_Test` AVD shows a persistent "System UI isn't responding" ANR after every boot. **Root-caused as an AVD/host infrastructure defect, not an app bug**: pulled the on-device ANR trace list (`/data/anr/`) and found the ANR timestamp lands at emulator boot completion, before the app was even installed; then reproduced the identical "System UI isn't responding" dialog on a second, independent AVD (`Android_15_Test`, stock launcher, Amanah Quran never installed on it) — conclusively isolating the failure to this machine's emulator tooling/GPU-passthrough stack, unrelated to Amanah Quran. The release APK itself installs and launches cleanly at API 36 (home screen renders correctly behind the dialog, zero app-level crash/FATAL log lines across all attempts). Full interactive UI verification therefore relies on the physical device below; emulator-based UI verification for this environment is not practically achievable until the host's emulator/AVD setup is fixed (outside this app's codebase).

### User-requested follow-up pass (2026-08-05) — implemented and device-verified

1. **Page Mode swipe direction fixed for RTL reading.** `HorizontalPager` in `SurahReaderScreen.kt` now sets `reverseLayout = true` (chrome/padding stays LTR via the existing `LocalLayoutDirection` override; only the pager's own scroll axis flips), so swipe-right advances to the next page and swipe-left goes back, matching how a physical Urdu/Arabic Mushaf turns. Verified on-device: swiping right from page 1 advanced to page 2.
2. **Fixed a real Page Mode bug found during the swipe-direction check: pages got permanently stuck on the loading spinner when swiping back to a page the reader had already visited.** Root cause: `LaunchedEffect(pagerState) { snapshotFlow { pagerState.currentPage }.collect { ... } }` never restarts once launched, so its `uiState` read was a stale closure frozen at first composition — comparing the new page's target mode against that stale value could spuriously match and silently skip calling `onOpenModeChanged`, leaving `isCurrentLoaded` false forever for that page. Fixed with `rememberUpdatedState(uiState)` so the effect always reads the live state. Reproduced the exact hang pre-fix (swipe to page 2, swipe back to page 1 → infinite spinner) and confirmed it's gone post-fix, including under rapid repeated swipes.
3. **Moved the IndoPak/Uthmani script toggle out of the reader and into Settings only.** Removed the `ReaderScriptSwitch` composable and both its call sites (scroll-mode top bar, Page Mode header) from `SurahReaderScreen.kt`, along with the now-dead `ReaderViewModel.selectScript()`/`onSelectScript` plumbing; Settings already had an equivalent "Arabic display script" control wired to the same repository, so this removes the duplicate rather than adding a new one.
4. **Trust Center now states the release has been manually reviewed and approved for open production release**, and the page is simplified to just: what each source is, a "nothing has been changed" statement, and a single "Verify now" action that checksums on-device content against the values recorded at build time — dropped the validation-row-count, raw Mushaf-layout-field dump, "Release Information" (including the old "BLOCKED — INTERNAL TEST BUILD" wording), claims-not-made, and content-integrity-placeholder sections entirely. While wiring this up, found and fixed a real gate bug in `TrustCenterRepository.isPublicReleaseAllowed()`: it required `mushaf_page_layout.validation_status == "VERIFIED"` and `manual_review_status == "APPROVED"` and a non-"N/A" checksum, but the actual JSON schema uses `"GO"`/`"VERIFIED"` as its "passed" tokens and an intentional `"N/A - verified layout metadata"` placeholder (page layout spans multiple derived files, not one hashable artifact) — so the gate silently evaluated `false` even though every underlying status was genuinely approved, and the production-approval statement could never show. The canonical source for `trust_center_content.json` is `content-pipeline/06_generated_projectdata/trust_center_sources.json`, itself regenerated on every release build from a hardcoded dict literal in `scripts/generate_content_pipeline.py::generate_content_pipeline` — edited the literal directly (not just the asset file) so the fix survives future pipeline regenerations; confirmed by a clean `--rerun-tasks` release rebuild and re-inspecting the packaged APK's asset.
5. Rebuilt and re-verified the full release AAB/APK after all of the above (unit tests, lint, `assembleRelease`/`bundleRelease --rerun-tasks` all clean; zero crashes in logcat on the physical device for the release-signed build).

Updated `TrustCenterRepositoryTest.kt` and `ReaderMvpViewModelTest.kt` for the removed/changed APIs; added a test asserting `publicReleaseAllowed` and the production-approval statement are actually populated (previously the suite asserted the release was blocked, matching the pre-fix bug).

## Sprint state

| Sprint | Status | Evidence |
| --- | --- | --- |
| 0 — audit/baseline | PASS | V1 debug build, unit tests and lint run; content and user-data sources documented |
| 1 — migration foundation | PASS | Room migration 3→4 and regression tests; no destructive migration in provider |
| 2 — content-pack foundation | PASS | Versioned Urdu manifest, checksums, validation report and Room asset |
| 3 — Urdu ingestion | PASS | 6,236/6,236 mappings, automated validation, and reviewer approval recorded |
| 4 — translation reader/search | PASS | Arabic/Urdu reader toggle, RTL rendering, font sizing, bundled offline asset, Urdu search integration, and device verification |
| 5 — audio engine | PARKED | V3.0 scope; no approved source catalogue selected |
| 6 — audio downloads | PARKED | V3.0 scope |
| 7 — collections/backup | PASS (engineering) | Collection repository/filter UI, versioned codec, SAF export/import, validation preview and restore application implemented |
| 8 — sharing/reporting | PASS | Local text/image sharing and editable mailto issue report with category picker |
| 9 — Trust Center/privacy/accessibility | PASS (engineering) | Optional pack metadata, Urdu attribution, on-device checksum verification, privacy pledge, and Elder Mode/large-target settings |
| 10 — device/performance | PASS (available device) | TECNO CH6i Android 13 launch, search, reader script switch, settings persistence, and Trust Center verification |
| 11 — signed release | PASS (build gate) | V2.0.0/versionCode 6; internal and public release-track assemblies, content/license gates, R8, resource shrinking, and native symbols pass |
| 12 — READER-UX-01 (adaptive zoom/auto-scroll/graphical redesign) | PASS | See `docs/_implementation/READER_UX_01_UNIFIED_ADAPTIVE_READER_EXPERIENCE_REPORT.md`; 125/125 tests, device-verified |
| 13 — READER-UX-02 (Continuous Mode/split translation) | PASS (device-verified, incl. Surah-1 continuity fix) | V2.1.0/versionCode 8; see `docs/_implementation/READER_UX_02_CONTINUOUS_AND_SPLIT_TRANSLATION_REPORT.md`; 145/145 tests, signed AAB/APK built, content/DB integrity gates pass |

## Verified content evidence

- QuranEnc Urdu Junagarhi CSV: version `v1.1.3-csv.1`.
- Source SHA-256: `027cd258d87285bdb8afbffa60fd141c450a1d029b14c16501355ab24481fec4`.
- Translation rows: 6,236; canonical keys unique and complete for the imported source.
- Generated Room pack SHA-256: `9f08b2e434884262a7343abc6b29638d4d270b1769d0e3f12138377970a33677`.
- Automated report: `content-pipeline/05_validation_reports/urdu_translation_validation.json`.
- Production display text is separate from normalized search text.

## Recent implementation files

- `apps/android/app/src/main/kotlin/org/amanahquran/app/content/translation/`
- `apps/android/app/src/main/assets/content/translations/`
- `tools/content-import/import_quranenc_translation.py`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/backup/UserBackupCodec.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/BookmarkCollectionRepository.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/database/DatabaseProvider.kt`

## Quality evidence

- `./gradlew testDebugUnitTest assembleDebug lintDebug --no-daemon`: passed after the initial V2 content/reader slice (lint has existing reviewed warnings only).
- Targeted `:app:testDebugUnitTest --tests org.amanahquran.app.core.backup.UserBackupCodecTest --no-daemon`: passed after adding page-reference and settings round-trip coverage.
- `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:lintDebug --no-daemon`: PASS after collections and Trust Center UI changes (0 lint errors; 30 existing warnings).
- `./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleRelease -PamanahReleaseTrack=public --no-daemon`: PASS for V2.0.0 after updating the V2 version assertion.
- Targeted translation/backup tests after checksum regeneration: PASS (33 actionable tasks).
- `./gradlew :app:assembleRelease --no-daemon`: historical V1 result; the current explicit public-track command above passes.
- `./gradlew :app:testDebugUnitTest :app:lintDebug :app:bundleRelease :app:assembleRelease -PamanahReleaseTrack=public --no-daemon`: PASS for V2.1.0/versionCode 8 (READER-UX-02) — 145/145 tests, lint clean, validateReleaseContent 0 blockers, validateQuranDatabase PASS, signed AAB + R8-minified APK built.

## Parked/external work

1. QuranEnc republication terms and source attribution remain recorded; final legal/licence evidence should still be retained for release audit.
2. Backup import/export is now implemented through SAF with validation preview and local restore.
3. Text/image sharing and user-controlled issue reporting are implemented from the reader, including category selection.
4. Audio playback/downloads remain V3.0 pending an approved reciter source and licence.
5. Full TalkBack and long-duration low-end performance evidence remain for a dedicated manual QA pass.
6. V2.1.0 still needs the normal external Play Console upload/review and fresh approval record.
7. The READER-UX-02 signed release artifact specifically was not separately re-installed on-device this pass (the debug build with identical app code was, including the Surah-1 continuity fix) — see that report's Section L.
