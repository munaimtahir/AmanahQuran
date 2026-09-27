# UI Refinement R1–R5 — Implementation Report

Branch: `claude/using-credits-nbqbb1`. Plan: `docs/ui/UI_REFINEMENT_REVIEW_AND_PLAN.md`. Evidence: `UI_AUDIT_VNEXT/`.

## Features completed

**R1 — Reader stabilisation (review findings C1–C7)**
- Reader settings sheet rebuilt (`ReaderSettingsBottomSheet.kt`):
  - "Link Arabic and translation size" switch, which had become unreachable.
  - Sliders apply on release and keep the reading position (same anchor capture as pinch-zoom).
  - "Reset text size" button.
  - Wrapping chip rows with the same labels as Settings, Elder-sized targets, and the sheet scrolls at large font scales.
- Immersive chrome (`SurahReaderScreen.kt`):
  - The toolbar now overlays the text, so hiding it no longer reflows the page.
  - It hides when scrolling forward and returns on scroll up, on any tap (the first tap only reveals), or at the top of the reader.
  - It never auto-hides in Elder Mode or with TalkBack, and TalkBack still reads the toolbar first.
- Removed the unused `ReaderTypographyPanel` (~110 lines).

**R2 — Reader interactions**
- Tapping the reader title (now with a ▾ affordance) or the ayah counter opens "Jump to ayah". It accepts `2:255` or a bare ayah number and opens other surahs when needed (`ReaderViewModel.jumpToReference`, `core/util/AyahReferenceParser.kt`). Invalid references show an error instead of navigating.
- One-time hint that ayahs are tappable, shown after the pinch hint (`ayahTapHintShown` preference).
- Selected-ayah bar:
  - Labelled Bookmark and Share buttons; share-as-image and reports stay in the overflow menu.
  - Elder-sized targets, and it sits clear of the navigation bar.
  - The list reserves space so the last ayah can scroll above the bar.

**R3 — Home and bookmarks**
- Home order: header → Continue Reading → Browse (Surah / Juz / Page / Search / Bookmarks) → streak → Daily Ayah → activity → Trust. Spacing is tightened. In Elder Mode, Search and Bookmarks come first.
- The Bookmarks screen has a "Reading position" section (auto-saved, opens the exact ayah) above "Saved bookmarks". It is a UI-only change; nothing in the stored data changed.

**R4 — Accessibility**
- Urdu Nastaliq translation line height is 1.85× (English 1.65×, never below 1.4×). The "missing translation" text no longer has fixed 20sp leading, which used to clip at large sizes.
- Advanced Reader Settings in Elder Mode: full-width mode chips, whole-row switch toggles, and 56dp slider height.
- Bookmark ayah previews are laid out RTL.

**R5 — Owner-approved options**
- Optional **Black (OLED)** theme: #000000 background, #E6E4DE text (WCAG AAA). It is available in Settings and in the reader sheet, and it survives backup and restore.
- Live typography preview in Settings. It uses verified ayah 1:2 from the bundled database and follows the current script, sizes, spacing and translation.

**Other fixes from the review:**
- The keyboard Search action opens a result directly only when there is exactly one.
- Daily Ayah history respects translation Off.
- Streak copy changed to "Read any ayah today to begin a streak".
- The instrumented smoke test now looks for "Start Reading" instead of "Open Mushaf Page", which PR #1 had removed.

**Housekeeping**
- Removed `COMPETITOR_UI_BENCHMARK.zip` and `UI_AUDIT.zip` (105 MB of duplicates) and ignored them in `.gitignore`.
- Stripped reviewer names, avatars and review IDs from 1,476 scraped reviews.
- Corrected unsupported claims in the benchmark docs (see `UI_AUDIT_VNEXT/EVIDENCE_INTEGRITY_REPORT.md`).
- Filled the empty `UI_AUDIT_VNEXT/` files.
- `AGENTS.md` scope and the release gates now include the Black theme.

## Tests run
- `./gradlew :app:testDebugUnitTest`: **300 tests, 0 failures**. This includes the new Robolectric Compose UI tests (`app/src/test/.../ui/`), which needed `ui-test-junit4` added as a `testImplementation`.
- `./gradlew :app:lintDebug`: passes. The only warnings in touched files are pre-existing `UseKtx` hints in the share-image code.
- `./gradlew :app:compileDebugAndroidTestKotlin`: compiles. The instrumented tests were **not run**: there is no emulator in this environment.

New or extended tests:
- `ReaderSettingsSheetLogicTest`, `ReaderSettingsSheetUiTest` (200% font scale, Elder Mode)
- `AyahReferenceParserTest`, `ReaderMvpViewModelTest.jumpToReference_*`
- `HomeSectionOrderTest`, `BookmarksViewModelTest.readingPositionIsShownSeparatelyFromSavedBookmarks`
- `TranslationLineHeightTest`, `ThemeModeTest`, `ReaderSettingsRepositoryTest` (+2), `UserBackupCodecTest` (+1)
- `TypographyPreviewCardUiTest`, `SearchImeActionTest`

## Known issues / not done
- **No device screenshots or videos.** The on-device checklist is in `UI_AUDIT_VNEXT/EVIDENCE_MANIFEST.md`.
- Robolectric can't inject touches into the sheet's popup, so the UI tests click through the accessibility action. That is the same `onClick` wiring, but not a real gesture.
- The Elder Mode reader toolbar still fits back + title + three text buttons into 64dp. On narrow phones the title truncates; a device check is needed.
- Landscape side-by-side layout was not built. Continuous mode already shows translation side-by-side, and there's no evidence a separate landscape layout helps.
- Benchmark videos and screenshots stay in the working tree. The zips and videos remain in git history until the owner rewrites it (below).

## History clean-up (owner action)
Deleting the zips only shrinks future checkouts. Removing them from history rewrites `main` and needs a force-push, so it's left for the owner:

```bash
pip install git-filter-repo
git clone --mirror https://github.com/munaimtahir/AmanahQuran.git && cd AmanahQuran.git
git filter-repo --invert-paths --path COMPETITOR_UI_BENCHMARK.zip --path UI_AUDIT.zip
# optional: also drop videos after uploading them to a GitHub Release
# git filter-repo --invert-paths --path-glob 'COMPETITOR_UI_BENCHMARK/VIDEOS/*' --path-glob 'UI_AUDIT/VIDEOS/*'
git push --force --mirror
```
Everyone must re-clone afterwards.

## Scope guardrail confirmation
Everything here is presentation-layer work on shipped features, plus one owner-approved theme option. There is:
- no network, accounts, analytics, ads or tracking;
- no tafsir, word-by-word, hifz tools, audio or new content;
- no new permissions.

Quran and translation display text is untouched: the Settings preview reads verified text from the bundled database, and a test checks that. Bookmarks, last-read and jump-to-ayah all use canonical `surah:ayah` keys.
