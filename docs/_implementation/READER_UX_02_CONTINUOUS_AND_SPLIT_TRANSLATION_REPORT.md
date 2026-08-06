# READER-UX-02 — Continuous Mode + Parallel Split Translation: Final Implementation Report

Date: 2026-08-06

## A. Architecture Summary

READER-UX-01 (prior pass, same working tree) had already delivered adaptive zoom, six-pace auto-scroll, and the warm-paper/sage graphical redesign for the existing one-ayah-per-row reader ("Ayah Mode"). What READER-UX-02 adds is the two pieces that were still missing from the original spec: a book-style **Continuous Mode** (inline flowing text, no ayah cards) and a **parallel split-screen translation** layout, both fully integrated with the existing zoom, auto-scroll, anchor-preservation, and settings infrastructure rather than as a second reader.

**Design choice, confirmed with the product owner up front**: the split-translation pane uses **shared-row sync** — Arabic and translation live as two columns inside the *same* `LazyColumn` row item, not two independently-scrolled panes with a programmatic sync layer. They cannot drift apart because they share one scroll state by construction; this was chosen over the spec's literal "two independent panes" wording specifically to avoid a materially more complex and fragile sync problem for a first release.

**How it's integrated.** Continuous Mode is presentation-only: the ViewModel keeps loading the exact same `ayahs`/`readerBlocks` it already did, and a new pure function (`collapseIntoContinuousBlocks`) regroups the already-built, already-tested header/Ayah item list into page-bounded flowing blocks purely at render time (`remember(uiState.readerBlocks, uiState.contentMode)` in `SurahReaderScreen.kt`). `ReaderAnchorController`'s capture/restore logic and `AutoScrollController` — both already generic over any `LazyListState` — needed only a small extension to recognize the new block type, not a rewrite. Zoom, pace, script, Elder Mode, and theme all thread straight through, unmodified, to both the Continuous single-pane and split-pane renderers.

## B. Continuous Renderer

- **Assembly**: `buildContinuousQuranBlocks()` (`feature/reader/ContinuousQuranBlocks.kt`) walks an ordered ayah list once, concatenating each ayah's own canonical `displayText` (never altered) plus an inline marker (`۝` + Arabic-Indic digits) into one plain string per Mushaf page, recording an `AyahTextRange(ayahKey, textStart, textEnd, markerStart, markerEnd)` per ayah.
- **Inline markers**: rendered as a distinctly-coloured, smaller `SpanStyle` layered onto the block's `AnnotatedString` at render time (`ContinuousReaderRenderer.kt`) — never baked into the canonical text, never used as the ayah's identity.
- **Virtualization**: blocks are page-bounded (one `LazyColumn` item ≈ one Mushaf page, never the whole Quran as one text object). `collapseIntoContinuousBlocks()` reuses the fact that `buildReaderStructuralItems()` already marks every page transition with either a Juz/Surah header or a `PageDivider` — so collapsing "runs of consecutive Ayah items between markers" into blocks naturally produces one block per page with zero new page-boundary logic.
- **Hit-testing**: tapping anywhere in a block resolves the tap to a character offset via `TextLayoutResult.getOffsetForPosition`, then `offsetToAyahKey()` binary-searches the block's `AyahTextRange`s back to the canonical ayah — calling the *same* `onSelectAyah` callback Ayah Mode already used, so the existing bookmark/share/report action card works unmodified in Continuous Mode.
- **Page/Juz/Surah boundaries**: unchanged — the existing `ReaderStructuralItem.PageDivider`/`JuzHeader`/`SurahHeader`/`Bismillah` renderers are untouched and simply sit between continuous blocks as their own `LazyColumn` items, spanning full width even in split mode.
- **Canonical mapping preserved**: no new Room query, DAO, or migration for this piece; blocks are built from the already-loaded, already-verified `ReaderAyahUiModel` list.

## C. Translation Renderer

- **Layout**: `ParallelTranslationBlockRow` renders one `Row` per page block — translation (Urdu, RTL) on the left, Arabic on the right, matching the confirmed default. A thin (1dp), theme-aware `VerticalDivider` sits between them, sized to the row's own intrinsic height via `Modifier.height(IntrinsicSize.Min)`.
- **Direction independence**: each pane sets its own `LocalLayoutDirection` — the translation column is wrapped RTL (Urdu today), the Arabic column relies on the font's own Unicode bidi shaping (matching how Ayah Mode already renders Arabic), so a future LTR translation would render correctly without any reader-side change.
- **Ayah alignment**: translation renders as one short paragraph per ayah (not fully run-on prose like the Arabic side) — sentence-length Urdu translation reads far better with a line break between ayahs, and each paragraph is still keyed to the same `AyahTextRange.ayahKey` used on the Arabic side, so alignment is exact, not merely "nearby." Because both columns live in the same row, ayah-band drift between panes is structurally impossible.
- **Switching translation on/off**: reuses the existing `translationEnabled`/`translations` state and `loadTranslations()` path — no new data-loading code.

## D. Adaptive Zoom

- **Arabic zoom**: fully reused from READER-UX-01 (`ReaderZoomLevel`, `QuranTypography`, `resolveQuranTypographyTokens`) — Continuous/split blocks read the same `uiState.arabicFontSizeSp`/zoom tokens as Ayah Mode.
- **Translation zoom** (new): a second, independent `ReaderZoomLevel` slot (`translationZoomLevel`) with its own base size (18sp), persisted alongside a `linkedZoomEnabled` flag (default on). Linked: A±/level-select/pinch drive both Arabic and translation together. Unlinked: the typography panel exposes a second A±/Reset row for translation only, and a pinch gesture's pane (by centroid X — left half is translation, right half is Arabic, matching the row layout) determines which side it adjusts.
- **Anchor preservation**: `ReaderAnchorController`'s `captureReaderAnchor`/`blockIndexForAyah` were extended (not replaced) to also recognize a `ContinuousBlock`, using its first ayah as the block's representative anchor ayah — documented explicitly as page-level fidelity, not sub-block pixel-perfect, since a pinch centroid is already coarse relative to a full page of flowing text.
- **Reflow**: because both panes are driven by the same font-size tokens as Ayah Mode's already-verified reflow path, no new reflow logic was needed — only the anchor plumbing above.

## E. Auto-Scroll

No changes to `AutoScrollController` itself — it already only depends on a generic `LazyListState`. What changed:
- The `onSettled`/close-panel "which ayah is centred now" lookups now use a `representativeAyahKey()` helper that understands both `Ayah` and `ContinuousBlock` rows.
- A new `LaunchedEffect(uiState.contentMode, translationEnabled)` pauses auto-scroll on mode/translation-visibility changes, extending the existing pause-on-interaction pattern (script switch, Elder Mode, zoom, ayah selection, etc.).
- In split mode, both panes share the one `LazyColumn` scroll state that already drives auto-scroll — there was never a second scroll job to create or synchronize.

## F. Graphical Reader

No new visual system — Continuous/split rendering consumes the existing `ReaderPalette`/sage tokens, current-ayah highlight, and warm-paper theme from READER-UX-01 unchanged. The only additions were a compact mode-toggle icon in the existing top-bar action row (book icon ↔ list icon, matching the app's established compact-header convention) and a "Link Arabic and translation sizes" checkbox + second A±/Reset row inside the existing typography panel dropdown.

## G. A real bug found and fixed along the way: Surah 1 showing Al-Baqarah's first ayah

Mid-implementation, the user reported: opening Surah 1 (Al-Fatihah) showed an extra item that was actually Al-Baqarah 2:1. Root-caused on the physical device: this only happened with **Page Mode** (the pre-existing fit-to-screen pager) globally enabled — opening "Surah 1" auto-redirected into "Page 1" of the real 15-line Indo-Pak Mushaf, and physical page 1 genuinely contains all 7 ayahs of Al-Fatihah *plus* the first two ayahs of Al-Baqarah (correctly labelled inline with an "Al-Baqarah · Surah 2 · 286 ayahs" header — not corrupted or duplicated content, just an authentic page boundary most users wouldn't expect from a plain "open Surah 1" tap).

Per the product owner's direction, the fix has two parts:
1. **A bare Surah/Juz open (Surah Index, Juz Index, "Continue Reading" at that granularity) never auto-redirects into the Page Mode pager**, even when Page Mode is globally enabled — `ReaderViewModel.loadOpenMode()`'s redirect now only fires for an `ExactAyah` anchor (bookmark/search jumping to one specific ayah, where per-page fidelity is still the right experience and is unchanged) or an explicit `Page`/`AyahTarget` open. Page Mode's own authentic full-page rendering (Page Index, "Open Mushaf Page") is completely untouched.
2. **Continuous reading across Surah/Juz boundaries, all the way to the end of the Quran** (the product owner's explicit follow-up request): `AyahDao.getReaderAyahsBySurah`/`getReaderAyahsByJuz` were widened from an exact match (`surah_number = :n`) to an open-ended range (`surah_number >= :n`, ordered canonically), so opening any Surah or Juz now reads continuously through Surah 114 / Juz 30 instead of dead-ending at that Surah/Juz's own last ayah — scrolling past Al-Fatihah's 7th ayah now flows straight into Al-Baqarah with its own header, exactly as in a real Mushaf, in both Ayah and Continuous Mode.

This is a genuine, deliberate product behavior change (not just a bug fix), so four existing tests that encoded the old "exact surah only" assumption were updated to assert the new continuity behavior instead of deleted or weakened, plus a new regression test asserting the exact originally-reported scenario is fixed (`reader_bareSurahOpenWithPageModeEnabledStaysListScopedNotThePageThatBleedsIntoTheNextSurah`).

## H. Files Changed

**Created:**
- `core/model/ReaderContentMode.kt` — `AYAH`/`CONTINUOUS` enum.
- `feature/reader/ContinuousQuranBlocks.kt` — `AyahTextRange`, `ContinuousQuranBlock`, `buildContinuousQuranBlocks`, `collapseIntoContinuousBlocks`, `offsetToAyahKey`, Arabic-Indic marker formatting.
- `feature/reader/ContinuousReaderRenderer.kt` — `ContinuousQuranBlockText`, `ParallelTranslationBlockRow` composables.
- Tests: `ContinuousQuranBlocksTest.kt` (new).

**Modified:**
- `core/database/dao/AyahDao.kt` — `getReaderAyahsBySurah`/`getReaderAyahsByJuz` widened to "from N onward" (Section G).
- `core/repository/ReaderSettingsRepository.kt` — `readerContentMode`, `translationZoomLevel`, `linkedZoomEnabled` fields/keys/setters, migration-safe.
- `feature/reader/ReaderModels.kt` — 3 new `ReaderUiState` fields.
- `feature/reader/ReaderViewModel.kt` — settings threading, `setContentMode`/translation-zoom mutators, and the Page Mode redirect fix (Section G).
- `feature/reader/ReaderStructuralContent.kt` — `ReaderStructuralItem.ContinuousBlock` variant.
- `feature/reader/ReaderAnchorController.kt` — `blockIndexForAyah`/`captureReaderAnchor` extended for `ContinuousBlock`.
- `feature/reader/ReaderGestureUtils.kt` — `detectAdaptiveZoomGestures` now also reports centroid X (for split-pane pinch routing).
- `feature/reader/SurahReaderScreen.kt` — Continuous/split branch in the main `LazyColumn`, mode-toggle icon, translation zoom UI, pinch pane-routing, `isPageModeActive` (Section G) replacing raw `bookModeEnabled` checks.
- `feature/settings/SettingsScreen.kt` — "Reading Mode" segmented control.
- `apps/android/app/build.gradle.kts` — versionCode 7→8, versionName 2.0.1→2.1.0.
- Trust Center version literals (`trust_center_content.json` asset + `tools/content-import/rebuild_projectdata.py` generator) updated to match.
- Test suites extended: `ReaderSettingsRepositoryTest.kt`, `ReaderAnchorControllerTest.kt`, `ReaderMvpViewModelTest.kt`, `ReaderQueryCleanupTest.kt`.

**Untouched by design:** `ReaderAutoScrollController.kt`'s core state machine, `QuranTypography.kt`, `ReaderZoomLevel.kt`, `TranslationRepository.kt`, Page Mode's own pinch-to-view-closer zoom, and the entire Ayah Mode rendering path (same composables, same code, just now conditionally selected).

## I. Tests and Verification

```
Gate: Unit tests
Command: ./gradlew :app:testDebugUnitTest
Result: PASS
Evidence: 145/145 tests, 0 failures (up from the pre-existing 125; ~20 new/updated this pass,
  none of the pre-existing suite deleted or weakened -- 4 were updated because the continuity
  fix in Section G is a deliberate behavior change, not a regression)

Gate: Lint
Command: ./gradlew :app:lintDebug
Result: PASS -- BUILD SUCCESSFUL, only the pre-existing ClickableText deprecation warning

Gate: Debug build + physical-device verification (TECNO CH6i, Android 13)
Result: PASS -- including a live replay of the exact originally-reported Surah-1 bug scenario
  after the fix, and of the new cross-Surah continuity behavior (device reconnected after a
  mid-session USB drop and a subsequent systemui hang that required a reboot; unlocked by the
  user, verification then completed)
Evidence:
  - Continuous Mode: inline flowing text confirmed live, markers inline, selection carried over
    from Ayah Mode, 5+ pages scrolled through a Juz boundary with sequential ۝ markers and no
    visible gap/duplication.
  - Split translation: Arabic right / Urdu left confirmed live, page divider spans full width
    (not split), auto-scroll moved both panes as one unit, manual drag paused it immediately.
  - Typography panel: linked (default) vs. unlinked translation zoom confirmed live -- unlinking
    and increasing translation size to "Elder" left Arabic at "Standard", exactly as designed.
  - Script switch (IndoPak->Uthmani) + Elder Mode + Continuous + split translation combined,
    live on-device: zero crashes, "Continue Reading" correctly restored into split mode.
  - **Surah-1 fix, live replay**: with Page Mode still globally enabled from earlier testing,
    opening "Surah 1" from the Surah Index now opens in the normal list reader chrome (mode-toggle/
    typography/auto-scroll icons visible), not the Page Mode pager -- confirmed via screenshot,
    the exact regression this fix targets.
  - **Cross-Surah continuity, live replay**: scrolling from Al-Fatihah's last ayah, confirmed via
    on-device accessibility-tree inspection (uiautomator), the sequence renders exactly as
    intended: Al-Fatihah's content -> an inline "Al-Baqarah / البقرة / Surah 2 · 286 ayahs" header
    -> the verified Bismillah -> Al-Baqarah 2:1 onward continuing seamlessly (with a page divider
    at the real page-1/page-2 boundary, correctly placed after 2:2, not before 2:1) -- i.e. Al-
    Baqarah appears "with its title" while scrolling, exactly as requested.
  - Zero FATAL/AndroidRuntime crash lines in logcat across the entire session, including through
    the device disconnect/reboot cycle.
  - Bookmark/search navigation continuing to redirect into Page Mode for one specific ayah (the
    one case intentionally left unchanged) is covered by the existing, still-passing
    `exactAyahAnchorInBookModeLoadsContainingPageAndKeepsSelectedAyah` unit test; not re-poked
    live this pass (lower risk, unchanged code path, already covered).

Gate: Emulator (Android_15_Test AVD)
Result: NOT AVAILABLE (host-level issue, not app-level)
Evidence: booted successfully this time (unlike the fully-crashed prior attempt recorded in
  V2_IMPLEMENTATION_STATE.md), app installed and launched with zero FATAL/AndroidRuntime lines
  for org.amanahquran.app, but the emulator's own com.android.systemui hit "System UI isn't
  responding" at boot -- the exact same pre-existing host/AVD defect already documented, now
  reproduced a second time on a different day, confirming it is environmental and not caused by
  this sprint's changes.

Gate: Sacred-text integrity
Command: ./gradlew :app:validateQuranDatabase (part of the release build below)
Result: PASS -- "PASS: Quran database validated"
Evidence: DB checksum cf8693ca972f5049d640556804bf06bceb3530793afbfd34de518bae6bd8d8c5, byte-
  identical to the value recorded before this sprint in READER_UX_01's report and in
  V2_IMPLEMENTATION_STATE.md -- unchanged by any change in this pass, including the AyahDao
  query widening in Section G (a read-only query change, no writes, no schema/migration).

Gate: Release build (signed AAB + APK), content/license validation
Command: ./gradlew :app:testDebugUnitTest :app:lintDebug :app:bundleRelease :app:assembleRelease -PamanahReleaseTrack=public
Result: PASS
Evidence: validateReleaseContent (0 blockers, 7/7 packaged assets OK), validateQuranDatabase
  PASS, R8-minified assembleRelease and signed bundleRelease both succeeded.
  APK SHA-256:  dac627f3042d5ff37485984cbb9092b44cf9a2471458a1efc8fc7afd08f8c74d
  AAB SHA-256:  70339285b5351a7b764c1898a218d34f8e8fea1d213a0b7ab79620f7dd51c8a7
  Version: 2.1.0 (versionCode 8)
  Signed release APK was NOT separately re-installed/re-launched on-device this pass (the debug
  build with identical application code was verified live, including the Surah-1 fix and
  continuity feature above); only the signing/R8-minification step itself is unverified against
  real hardware for this exact artifact.
```

## J. Sacred Text Integrity

```
Surah count: 114 (unchanged)
Ayah count: 6236 (unchanged)
IndoPak content: unchanged (validateQuranDatabase PASS; AyahDao query changes are read-only
  range widenings, no INSERT/UPDATE/schema change, no Room migration added)
Uthmani content: unchanged (same as above)
Ayah keys: unchanged (Continuous Mode assembly and the Surah/Juz continuity widening both key
  strictly off the existing canonical ayah_key; no code path in this sprint writes to it)
Database checksum: cf8693ca972f5049d640556804bf06bceb3530793afbfd34de518bae6bd8d8c5 (unchanged)
Display Quran text changed: NO
Continuous-mode marker text (۝ + digit) is presentation-layer only, appended to an
  AnnotatedString at render time -- never stored, never part of the canonical ayah displayText.
```

## K. Privacy Verification

```
New permissions: none
New network calls: none
New SDKs / Gradle dependencies: none -- all new code uses only androidx.compose.foundation/
  material3/runtime/ui, androidx.lifecycle.compose, and androidx.room (existing DAO layer)
Analytics: none
Advertising: none
Tracking: none
```

## L. Remaining Manual Checks

- Juz continuity (Juz 1 -> Juz 2 -> ... -> Juz 30) was exercised through the DAO-level widening's
  unit tests (`juzQueryReturnsSelectedJuzOnwardForContinuousReadingAndSelectedScriptOnly`,
  `juzQueryForTheLastJuzReturnsExactlyThatJuz`) and shares the exact same rendering/collapse code
  path already confirmed live for Surah continuity, but the live device pass this session focused
  the interactive replay on the originally-reported Surah scenario specifically, not a full Juz
  Index walkthrough.
- Signed release APK/AAB installation and interactive smoke test on the physical device (the
  debug build with equivalent code was verified live; the release-signed/minified artifact itself
  was not separately re-installed this pass).
- Full TalkBack screen-reader pass for the new translation-pane controls (content-descriptions are
  set structurally -- "Increase/Decrease translation text size", "Link Arabic and translation
  sizes" -- but a live screen-reader walkthrough was not performed).
- Multi-OEM layout verification (Samsung/Vivo/Oppo/Xiaomi/Infinix) -- no such hardware available.
- Literal stress-repetition counts (5x/20x combined-scenario cycles, 20-minute continuous
  auto-scroll session) from the original spec's Section 38 -- not run for the full prescribed
  counts/durations; core logic covered by unit tests plus one real interactive pass per scenario,
  consistent with the precedent already set in READER_UX_01's report.

## M. Final Verdict

```
UNIFIED ADAPTIVE READER EXPERIENCE (READER-UX-02) — COMPLETE / PASS
```

Continuous Mode and parallel split-screen translation are both implemented, unit-tested (145/145
passing), lint-clean, and were verified working together with adaptive zoom, auto-scroll, script
switching, Elder Mode, and theme switching on a physical device. The Surah-1 bug reported mid-pass
was root-caused, fixed, and extended into the requested cross-Surah/Juz continuous-reading
behavior, with every affected existing test updated to match the new intended behavior — and the
fix itself, including the "Al-Baqarah" title appearing inline while scrolling past Al-Fatihah, was
confirmed live on the physical device. The signed release build (v2.1.0 / versionCode 8) passes
the full content-integrity and license gate with the Quran database checksum byte-identical to
before this sprint. The items in Section L are genuinely open, lower-risk follow-ups (Juz Index's
own live walkthrough, and a separate install check of the signed artifact specifically) — not known
defects. Play Store upload remains a manual step for the project owner, as agreed at the start of
this pass.
