# Runtime Font Audit

This document details the text rendering path, the assigned fonts, and the composable components involved in presenting Quranic text in the Amanah Quran Android application.

## 1. Text Rendering Pathway
1. **Repository Access**: The UI queries `MushafRepository.getMushafPage(pageNumber, scriptType)`.
2. **Database Query**: Queries the SQLite database `amanah_quran_content_v1_candidate.sqlite` to load the appropriate display text:
   - Column `display_text` from `quran_texts` mapped to the specified `script_type` (`INDOPAK` or `UTHMANI`).
3. **Data Mapping**: Row results are mapped to `MushafPageUi` and `List<MushafLineUi>`.
4. **UI Delivery**:
   - [MushafPageItem](file:///home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/mushaf/MushafPageScreen.kt#L298) resolves the requested page and triggers compose recomposition when loaded.
   - [MushafPageFrame](file:///home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/mushaf/MushafPageFrame.kt) configures formatting parameters, border spacing, and resolves the correct `FontFamily` via `QuranFonts.getFontFamily(scriptType)`.
   - [MushafLineText](file:///home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/mushaf/MushafLineText.kt) renders each individual line as a single Compose `Text` element with specific styling.

## 2. Active Composables & Roles
* `MushafPageScreen`: Container displaying top bar, bottom page controls, vertical/horizontal pagers, and a semi-transparent debug overlay in debug builds.
* `MushafPageItem`: Handles asynchronous page loading, progress indicator state, error views, and invokes the page frame upon success.
* `MushafPageFrame`: Implements the paper-Mushaf style background (cream paper look, dual gold border), headers, line delimiters, footers, and bookmark ribbon.
* `MushafLineText`: Employs a single `Text` composable configured with `PlatformTextStyle(includeFontPadding = false)`, maximum lines of 1, text alignment centered, text direction RTL (`LayoutDirection.Rtl`), custom font size, and line height multiplier.

## 3. Font Loading Configuration
Located in [QuranFonts.kt](file:///home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/QuranFonts.kt):
- **IndoPak Script**: `FontFamily(Font(R.font.digital_khatt_indopak))`
- **Uthmani Script**: `FontFamily(Font(R.font.digital_khatt_v2), Font(R.font.indopak_nastaleeq))`

The Uthmani family keeps `indopak_nastaleeq.ttf` as a runtime fallback for rare Quranic stop marks that are not covered by `digital_khatt_v2.otf`.
