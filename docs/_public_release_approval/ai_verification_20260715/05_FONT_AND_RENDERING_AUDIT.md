# Font and Rendering Audit Report

**Date**: 2026-07-15
**Font Assets Directory**: `apps/android/app/src/main/res/font/`

This report documents the verification of font assets bundled in the app, their hashes, unicode glyph coverage, and screenshots of correct rendering.

## Bundled Font Files

The following active fonts are bundled inside the application:

| Font Filename | SHA-256 Checksum | License Profile | Verification |
| :--- | :--- | :--- | :--- |
| `digital_khatt_indopak.otf` | `59a5e78c530de236a365354d558b37706f37d782f7ee95c3c9b7fe9e0fad042a` | SIL Open Font License 1.1 | Approved Active Font |
| `digital_khatt_v2.otf` | `0935c48269a57c9808e52dfae47864c189396452901c689977156036a72dd217` | SIL Open Font License 1.1 | Approved Active Font |
| `indopak_nastaleeq.ttf` | `a6463e24e36651404e9eff52dae26e18e9ef0718eb620636a66a20026a75c563` | SIL Open Font License 1.1 | Approved Active Font |

## Unicode Glyph Coverage Analysis

The automated font audit verified all display text characters against the font inventories (using `tools/validate_quran_font_coverage.py`):

1. **IndoPak Script Font Coverage**:
   - **Target**: `digital_khatt_indopak.otf`
   - **Total Unique Quran Code Points**: `85`
   - **Unsupported Characters**: `0` (100% coverage, no glyph warning or "tofu" boxes)
2. **Uthmani Script Font Coverage**:
   - **Target**: `digital_khatt_v2.otf` (primary) + `indopak_nastaleeq.ttf` (fallback)
   - **Total Unique Quran Code Points**: `69`
   - **Unsupported Characters**: `0` (100% coverage, no glyph warning or "tofu" boxes)

## Visual Rendering Verification (Screenshots)

We verified correct visual rendering on target physical devices by checking captured screenshots:
- **IndoPak View**: Matches layout on [16_indopak_reader.png](file:///home/munaim/Documents/github/AmanahQuran/docs/testing/amanah_device_test_20260625_014321/screenshots/16_indopak_reader.png). Correct ligature rendering and standing fatha marks observed.
- **Uthmani View**: Matches layout on [17_uthmani_reader.png](file:///home/munaim/Documents/github/AmanahQuran/docs/testing/amanah_device_test_20260625_014321/screenshots/17_uthmani_reader.png). Standard Hafs script presentation is clean with zero layout overflow.

---
**Verdict**: **PASS** (Bundled fonts cover all required glyphs, with verified correct rendering).
**Audit Agent**: Antigravity (AI Agent)
