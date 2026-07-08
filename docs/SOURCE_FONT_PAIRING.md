# Source Font Pairing

This document lists the Quranic scripts, their matching font assets, files, checksums, and verification status.

## 1. Font Asset Configurations

| Script Type | Font Display Name | Font Resource File | Format | SHA-256 Checksum Hash | Verification Status | Intended Use |
|-------------|-------------------|-------------------|--------|-----------------------|---------------------|--------------|
| **INDOPAK** | DigitalKhatt IndoPak | `digital_khatt_indopak.otf` | OTF | `59a5e78c530de236a365354d558b37706f37d782f7ee95c3c9b7fe9e0fad042a` | VERIFIED (glyph coverage) | Primary IndoPak Quran page text rendering |
| **UTHMANI** | Digital Khatt V2   | `digital_khatt_v2.otf` | OTF | `0935c48269a57c9808e52dfae47864c189396452901c689977156036a72dd217` | VERIFIED (glyph coverage) | Primary Uthmani Quran page text rendering |
| **UTHMANI FALLBACK** | IndoPak Nastaleeq | `indopak_nastaleeq.ttf` | TTF | `a6463e24e36651404e9eff52dae26e18e9ef0718eb620636a66a20026a75c563` | VERIFIED (glyph coverage) | Runtime fallback for rare Quranic stop marks |

## 2. Integrity Validation Gate

To prevent font corruption, accidental replacements, or script mismatches:
1. **Compilation Check**: The custom Gradle build task `validateQuranFonts` is registered as a dependency for all Kotlin compilation targets.
2. **Hash Verification**: Compares the SHA-256 fingerprint of physical font files in `app/src/main/res/font/` against the expected hashes listed above.
3. **Manifest Sync**: Audits that the entries exist and match [font_manifest.json](file:///home/munaim/Documents/github/AmanahQuran/projectdata/managed/font_manifest.json).
4. **Glyph Audit**: Runs [validate_quran_font_coverage.py](file:///home/munaim/Documents/github/AmanahQuran/tools/validate_quran_font_coverage.py) to compare glyph maps against the source text sqlite database to block any builds containing tofu (missing glyph blocks), using the runtime fallback family where applicable.
