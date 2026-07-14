# Font Coverage Proof Report

**Date**: 2026-07-15
**Validation Command**: `python3 tools/validate_quran_font_coverage.py`

This report verifies that all Unicode code points utilized in the display text database (`quran.db`) are fully supported by the bundled fonts.

## Summary of Font Coverage Validation

The font coverage validation script audits every character of the `display_text` column in `quran_texts` for both scripts:

```bash
python3 tools/validate_quran_font_coverage.py
```

### Audit Results

1. **IndoPak Script Font Coverage**
   - **Target Font**: `apps/android/app/src/main/res/font/digital_khatt_indopak.otf`
   - **Total Unique Quran Code Points**: 85
   - **Unsupported Characters**: **0**
   - **Verdict**: **PASS**

2. **Uthmani Script Font Coverage**
   - **Target Fonts**: `apps/android/app/src/main/res/font/digital_khatt_v2.otf` (Primary), `apps/android/app/src/main/res/font/indopak_nastaleeq.ttf` (Fallback)
   - **Total Unique Quran Code Points**: 69
   - **Unsupported Characters**: **0**
   - **Verdict**: **PASS**

## Unicode Character Inventories

The full glyph reports are written to:
- IndoPak Report: `build/reports/indopak_glyph_coverage_report.txt`
- Uthmani Report: `build/reports/uthmani_glyph_coverage_report.txt`

### Sample Verified Code Points

| Unicode Code Point | Glyph | Character Name | Used In (Example Ayah) | Coverage Status |
| :--- | :--- | :--- | :--- | :--- |
| `U+0627` | `ا` | ARABIC LETTER ALEF | 1:1 | Covered |
| `U+0644` | `ل` | ARABIC LETTER LAM | 1:1 | Covered |
| `U+0647` | `ه` | ARABIC LETTER HEH | 1:1 | Covered |
| `U+0651` | `ّ` | ARABIC SHADDA | 1:1 | Covered |
| `U+0654` | `ٔ` | ARABIC HAMZA ABOVE | 2:4 | Covered |
| `U+0670` | `ٰ` | ARABIC LETTER SUPERSCRIPT ALEF | 1:1 (IndoPak) | Covered |
| `U+06DD` | `۝` | ARABIC END OF AYAH | 1:1 (IndoPak) | Covered |

---
**Status**: verified by automated fontTools scan.
