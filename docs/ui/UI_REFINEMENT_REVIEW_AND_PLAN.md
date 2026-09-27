# UI Refinement — Review of `feature/ui-refinement-evidence-driven` and Forward Plan

> **Status (2026-09-27):** R1–R5 and housekeeping are implemented. The optional Black (OLED) theme was approved by the owner. See `UI_REFINEMENT_IMPLEMENTATION_REPORT.md` for what shipped, tests and open items.

Reviewed: merge commit `e69bf23` (PR #1), which brought in `2194f7e` WAVE A → `5378fc6` WAVE D and `989a10a`.
Baseline for comparison: `9739d44` (v2.2.0).
Date: 2026-09-27.

---

## 1. What the branch delivered

### Evidence (read-only benchmark)
- `UI_AUDIT/`: emulator audit of Amanah v2.2.0 (screens, workflows, 130% font scale, landscape, logcat, perf).
- `COMPETITOR_UI_BENCHMARK/`: 7 competitor apps (Quran for Android, Greentech, Glorious Quran, Aasan Tarjuma, Quran Majeed, Tarteel, Quranly) with screenshots, videos, Play Store review dumps and a P0–P3 opportunity backlog.
- `docs/ui/AMANAH_UI_REFINEMENT_DESIGN_SPEC.md`: PROP-01..09 spec.

### Code (13 files, +545 / −168)
| Wave | Change | Backlog item |
|---|---|---|
| A | `ReaderSettingsBottomSheet` (script, translation, Arabic/translation size, mode, theme); toolbar gear replaces inline typography panel; tap-to-toggle chrome; `AnimatedVisibility` top bar; reader column capped at 800dp; Elder Mode text buttons in toolbar | PROP-01, 03, 08, 09 |
| B | Hero card: whole card tappable, "Continue Reading" label + Surah name + "Page N · Ayah N"; nested button removed | PROP-02 |
| C | Search suggestion chips (Yasin, Al-Kahf, Al-Mulk, 2:255, Juz 30, Page 1); IME Search action; Daily Ayah history cards (Arabic + translation + localized date); bookmark row redesign; bookmark empty-state copy; calendar "min" floor of 1; streak copy | PROP-04, 05, 06 |
| D | Trust Center translation cards collapse technical details behind a tap | PROP-07 |

**Verification in this review:** `./gradlew :app:testDebugUnitTest :app:lintDebug` passes on current `main`. No new unit or UI tests were added by the branch, and `UI_AUDIT_VNEXT/` (the "after" evidence) contains only empty files.

---

## 2. Findings

### 2.1 Code — regressions and defects (fix before next release)

| # | Severity | Where | Issue |
|---|---|---|---|
| C1 | High | `SurahReaderScreen.kt` / `ReaderSettingsBottomSheet.kt` | **Linked-zoom toggle is now unreachable.** It lived only in `ReaderTypographyPanel`, which is no longer called (dead code, ~200 lines). Users who had linked zoom on can't turn it off; the sheet sliders also ignore `linkedZoomEnabled`, so pinch and sheet behave differently. |
| C2 | High | `ReaderSettingsBottomSheet` → `onSelectZoomLevel` | **Size changes from the sheet lose the reading position.** Toolbar zoom used to call `beginZoomAnchorCapture()` first; the sheet passes the raw callbacks, so the list jumps when font size changes. |
| C3 | Medium | Reader `Scaffold` top bar in `AnimatedVisibility` | Hiding the bar changes `Scaffold` padding, so the whole text column **shifts up by the toolbar height** on every hide/show. Immersive mode should overlay, not reflow. |
| C4 | Medium | Reader tap-to-toggle | `detectTapGestures` on the parent only fires on gaps between ayahs (ayah rows consume taps). Result: toggling is hit-and-miss, and a stray tap in a gap hides the back button with no hint how to bring it back. Chrome alpha 0 inside `AnimatedVisibility` is also redundant. |
| C5 | Medium | Sheet sliders | `onValueChange` writes to DataStore on every drag tick. Should update local state while dragging and persist in `onValueChangeFinished`. |
| C6 | Low | Sheet translation chips | Label logic checks `name == "NONE"` but the enum is `OFF`; chips read "Off / Manifest En / Irfan Ur". Theme chips ignore the existing `ThemeMode.displayName`. Use the same labels as Settings. |
| C7 | Low | Sheet layout | Chips rows are plain `Row`s; with Elder Mode or 200% font scale the 4 theme chips overflow. Use `FlowRow`. No Elder Mode sizing in the sheet. |
| C8 | Low | `SearchScreen` IME action | "Search" key opens the first result immediately. Fine for `2:255`, surprising for a word query with many hits. Restrict to single/reference results. |
| C9 | Low | `DailyAyahRepository.historyContent` | Falls back to the record's translation when the user has translation **Off**, so history shows a translation the user turned off. |
| C10 | Low | `BookmarksScreen` preview | `TextAlign.Right` + `headlineSmall` for Quran preview; should use the reader's Quran typography tokens and `TextAlign.Start` in an RTL `CompositionLocal`, consistent with other ayah previews. |
| C11 | Low | `ReadingStreakScreen` | "No current reading streak" is more negative than the previous copy; contradicts the benchmark's own "calm, non-punitive" principle. |
| C12 | Process | All waves | **No tests added** (AGENTS.md requires tests or a written reason) and no implementation report per wave. |

### 2.2 Evidence quality — treat with care

| # | Issue |
|---|---|
| E1 | **Repo bloat:** `COMPETITOR_UI_BENCHMARK.zip` (78 MB) and `UI_AUDIT.zip` (27 MB) duplicate their folders (70 MB + 31 MB incl. MP4s). `.git` is now 280 MB. Move evidence to a release asset / external storage or Git LFS. |
| E2 | **Third-party personal data:** `COMPETITOR_UI_BENCHMARK/LOGS/*_reviews.json` contain Play Store reviewer names and avatar URLs. For a privacy-first project these should be stripped to text + rating, or removed. |
| E3 | **Unverified or wrong claims:** "Greentech chips drive 60% of search traffic" has no source; PROP-07 says Dark is `#121212` but `DarkBackground` is `#101714`; the design spec says cards are 12dp and Dark is "true black OLED" while the code uses 16dp and the backlog treats OLED as optional; "Habits: no streaks" while the app ships a streak screen; Amanah's "9.5/10" and "200% compliant" were self-scored — only 130% was actually tested on Amanah. |
| E4 | Backlog says "READ-ONLY, nothing to be implemented" yet code shipped in the same branch; spec and backlog PROP numbers don't match (e.g. PROP-06 is the jump dialog in the backlog, Daily Ayah history in the spec). |
| E5 | Post-change evidence (`UI_AUDIT_VNEXT/`) is empty — no before/after proof that the refinements helped. |

### 2.3 What is solid
- Scope: nothing prohibited was added (no network, accounts, tracking, tafsir, etc.). Quran/translation display text untouched.
- Hero card, search chips, Daily Ayah history cards and Trust Center progressive disclosure are real improvements and match the evidence.
- Existing search already resolves `2:255`, `Juz 30`, `Page 1`, so chips route correctly.

---

## 3. Refinement plan (evidence-backed, in scope)

Ordering: fix regressions first, then close evidence-backed gaps, then polish. Each sprint ends with tests + a short report + fresh screenshots into `UI_AUDIT_VNEXT/`.

### Sprint R1 — Stabilise the reader (fixes C1–C7)
1. Move the "Link Arabic & translation size" switch into `ReaderSettingsBottomSheet`; honour it for sheet sliders. Delete `ReaderTypographyPanel` once nothing calls it.
2. Wrap every size change from the sheet in `beginZoomAnchorCapture()`; persist on `onValueChangeFinished`.
3. Immersive chrome: put the top bar in a `Box` overlay (or keep Scaffold padding constant) so text never reflows; reveal on any tap (including ayah taps when hidden — first tap reveals, second selects) and on scroll-up; keep auto-hide only during auto-scroll.
4. Sheet: `FlowRow` chips, shared labels (`ThemeMode.displayName`, translation display names), Elder Mode touch targets, scroll if taller than screen.
- **Tests:** ViewModel tests for linked/unlinked size changes; Compose UI tests: sheet updates script/theme/translation instantly, chrome hide/show doesn't change first-visible-item offset, sheet usable at 200% font scale.

### Sprint R2 — Reader interactions (audit O003/O004, benchmark "Reader Contextual Actions")
1. First-run one-time hint that ayahs are tappable (stored flag, like `firstZoomHintShown`).
2. Move ayah actions from centred card to a compact bottom action bar/sheet that doesn't cover the selected ayah.
3. Surah:Ayah jump dialog from the reader title (backlog PROP-06) reusing search's reference parser; bounds 1:1, 2:286, 114:6.
- **Tests:** parser boundary tests; UI test for jump + action sheet.

### Sprint R3 — Home & navigation density (audit O007)
1. Tighten Home so Search and Bookmarks sit above the fold on 1080×1920 at 100% scale (compact greeting, combine Daily Ayah + streak line).
2. Bookmarks: separate "Reading position (auto-saved)" header row from "Saved ayahs" (PROP-05) — UI only, no schema change.
- **Tests:** screenshot/UI test for above-fold items; bookmark list test that last-read isn't shown as a bookmark.

### Sprint R4 — Accessibility proof
1. Run Amanah itself at 200% font scale, Elder Mode on/off, landscape, TalkBack; record results in `UI_AUDIT_VNEXT/`.
2. Urdu Nastaliq translation: minimum line-height ≥1.4× at all translation sizes (PROP-01 remainder); verify no clipping at max size.
3. Deep settings rows in Elder Mode (audit interaction issue #4).
4. Landscape wide layout: keep 800dp cap; side-by-side Arabic/translation only if it tests well — optional.

### Sprint R5 — Optional, needs owner decision
- **OLED true-black** variant (PROP-07): small, in scope (theme tokens only), but adds a 5th theme option — owner to approve.
- Live typography preview in Settings.

### Housekeeping (separate PR, owner approval needed since it touches history/storage)
- Remove the two `.zip` duplicates; move MP4s/PNGs to LFS or a release asset.
- Strip reviewer names/avatars from review JSON.
- Correct E3 claims in the backlog/spec and reconcile PROP numbering into one list.

---

## 4. Scope guardrail confirmation
Every item above is presentation-layer work on shipped V1/V2.2 features. None adds network, accounts, analytics, ads, tafsir, word-by-word, hifz tools, audio, or new content. Quran and translation display text remain untouched; bookmarks and last-read stay on canonical `surah:ayah` / page references.
