# IndoPak Font Rendering Audit

This document details the character and font asset audit performed for the IndoPak script layout.

## 1. IndoPak Rendering Path Details
* **Source Field**: `display_text` column from the `quran_texts` table filtered by `script_type = 'INDOPAK'`.
* **Seeded Model**: Populated in `MushafLineEntity` columns (including `lineText` and `startAyahKey`).
* **UI Delivery**: Fed into `MushafLineText` via `MushafPageFrame` styling parameters.
* **Text Styling**: Uses compose `Text` with layout direction `LayoutDirection.Rtl`, `PlatformTextStyle(includeFontPadding = false)`, alignment centered, and a dedicated IndoPak font family mapping.

---

## 2. Active Fonts & Mappings
* **IndoPak Font Family**: `indopak_nastaleeq.ttf` (derived from `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/Indopak_Nastaleeq_Hanafi_With_Waqf_Lazmi.ttf`).
* **Uthmani Font Family**: `digital_khatt_v2.otf` (derived from `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/DigitalKhattV2.otf`).
* **Font Family Configuration**:
  ```kotlin
  object QuranFonts {
      val IndoPak = FontFamily(
          Font(R.font.indopak_nastaleeq, weight = FontWeight.Normal)
      )

      val Uthmani = FontFamily(
          Font(R.font.digital_khatt_v2, weight = FontWeight.Normal)
      )
  }
  ```

---

## 3. Glyph Coverage & Support Audit
* **Total Unique Unicode Code Points in IndoPak Text**: 85.
* **Cmap Check Results for `indopak_nastaleeq.ttf`**:
  * **100% of all printable text characters and Stop/Sajda signs are fully supported**.
  * Only one formatting code point (`U+202E` - RIGHT-TO-LEFT OVERRIDE) was not mapped in the cmap tables. Since `U+202E` is a directional text engine formatting mark that lacks a visual outline glyph (non-printing control code), it is automatically bypassed and handled by the Android text shaping engine.
* **Tofu/Missing Box Status**: Resolved. No missing glyph outlines remain.

---

## 4. Checked Pages & Verification
* **Pages Tested**: Page 1, Page 2, Page 43, Page 44, Page 46, Page 47, Page 73.
* **Blocks/Surahs Verified**: Surah Al-Fatihah, Surah Al-Baqarah start, Ayat al-Kursi (2:255), Surah Ali 'Imran transitions.
* **Visual Density**: Center vertical centering and comfortable line-by-line sizing are applied, occupying 80-88% of available body area.

---

## 5. Remaining Blockers
* **Licensing**: All font assets in `sourcedata/8/` are undergoing manual license compliance checks prior to public release. The V1 implementation is technically fully prepared once the license gate is marked green.
