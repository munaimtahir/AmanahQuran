# AMANAH QURAN UI REFINEMENT DESIGN SPECIFICATION

> **Numbering note:** PROP IDs in this spec pre-date the canonical backlog numbering. Mapping to `COMPETITOR_UI_BENCHMARK/AMANAH_UI_OPPORTUNITY_BACKLOG.md`: spec PROP-01→backlog PROP-01, 02→02, 03→03, 04→04, 05→05, 06 (Daily Ayah history) and 07 (Trust Center disclosure) are extra spec items, 08 (immersive chrome) is from the Mushaf-reader row of `AMANAH_VS_COMPETITORS.md`, 09→backlog PROP-08. Backlog PROP-06 (jump dialog) and PROP-07 (OLED black) are specified in `docs/ui/UI_REFINEMENT_REVIEW_AND_PLAN.md`.


## Amanah Design Tokens
- **Spacing**: 4dp baseline grid (4, 8, 16, 24, 32, 48).
- **Corner Radius**: 16dp for cards (as implemented in `AmanahShapes`), Material default for bottom sheets, pill chips.
- **Typography**: IndoPak font (Digital Khatt), Uthmani font (KFGQPC), Sans for UI.
- **Surfaces**: Material 3 surfaces.
- **Colors**: Green primary, Gold accents.
- **Themes**: System, Light (warm paper white), Dark (deep green-black #101714), Sepia (warm paper), and optional **Black (OLED)** (#000000 background, #E6E4DE text).
- **Touch Target**: Minimum 48dp (larger in Elder Mode).
- **Animations**: Minimal fade/slide.

## PROP-01 — Independent Quran & Translation Typography
- **Problem**: Arabic and translation texts require different scaling for readability.
- **Behavior**: Independent sliders for Arabic size and Translation size. Safe line spacing adjustments for Nastaliq.
- **Components**: `ReaderSettingsSheet`, `AyahView`, `ContinuousView`.
- **Theme/Elder**: Direct access to larger fonts in Elder Mode.

## PROP-02 — Compact Fully Tappable Continue Reading Card
- **Problem**: Current resume card has redundant nested CTA and ambiguous progress.
- **Behavior**: Entire card is tappable. Shows "Continue Reading", Surah name, Ayah/Juz/Page.
- **Components**: `HomeContinueReadingCard`.

## PROP-03 — Contextual Reader Settings Bottom Sheet
- **Problem**: Settings take user away from reading context.
- **Behavior**: Compact bottom sheet in reader with Script, Translation, Size sliders, Reading Mode, Theme.
- **Components**: `ReaderSettingsBottomSheet`.

## PROP-04 — Search Discovery & Direct Navigation
- **Problem**: Empty search is blank. References like 2:255 require searching and then tapping.
- **Behavior**: Suggestion chips. Direct routing for `2:255`, `Juz 30`, `Page 1`.
- **Components**: `SearchScreen`, `SearchViewModel`.

## PROP-05 — Continue Reading vs Bookmark Semantics
- **Problem**: Terminology overlap.
- **Behavior**: "Continue Reading" for auto last-read. "Bookmarks" for explicit saves.
- **Components**: `BookmarkScreen`, `HomeContinueReadingCard`.

## PROP-06 — Daily Ayah History Redesign
- **Problem**: Internal labels like `reviewed_random` shown.
- **Behavior**: Clean card with Surah, Ayah ref, Arabic, Translation, human-readable date.
- **Components**: `DailyAyahHistoryScreen`.

## PROP-07 — Trust Center Progressive Disclosure
- **Problem**: Too much technical data upfront.
- **Behavior**: High-level verified status on top. Technical hashes/sources under "Technical verification details".
- **Components**: `TrustCenterScreen`.

## PROP-08 — Immersive Reader Chrome
- **Problem**: Screen clutter during active reading.
- **Behavior**: Top/bottom bars disappear on scroll or tap, reappear on tap.
- **Components**: `QuranReaderScreen`.

## PROP-09 — Translation Visibility / Language Selector
- **Problem**: Hard to toggle translations during reading.
- **Behavior**: Toggle in Reader Settings (Arabic Only, Arabic+English, Arabic+Urdu).
- **Components**: `ReaderSettingsBottomSheet`.

