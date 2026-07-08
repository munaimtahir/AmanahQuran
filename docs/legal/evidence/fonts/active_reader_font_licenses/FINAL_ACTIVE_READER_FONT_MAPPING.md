# Final Active Reader Font Mapping

Date: 2026-06-27

## Scope

This document records the final approved Android reader font mapping for Amanah Quran.

## Active Fonts

| Script | Active app font resource | Exact app font file path | Exact source evidence file path | SHA-256 checksum | Source page evidence path | License/terms evidence path | Code files where referenced | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| IndoPak | `R.font.indopak_nastaleeq` | `apps/android/app/src/main/res/font/indopak_nastaleeq.ttf` | `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/Indopak_Nastaleeq_Hanafi_With_Waqf_Lazmi.ttf` | `a6463e24e36651404e9eff52dae26e18e9ef0718eb620636a66a20026a75c563` | `docs/legal/evidence/fonts/active_reader_font_licenses/source_pages/QUL_INDOPAK_NASTALEEQ_FONT_PAGE.html` | `docs/legal/evidence/fonts/active_reader_font_licenses/licenses/QUL_REPOSITORY_LICENSE_MIT.txt`; `docs/legal/evidence/fonts/active_reader_font_licenses/source_pages/QUL_FAQ_RESOURCE_LICENSE_NOTICE.html` | `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/QuranFonts.kt`; `apps/android/app/build.gradle.kts`; `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/mushaf/MushafPageScreen.kt` | CLEARED | Active IndoPak reader font; coverage helper flags `U+034F` as unsupported |
| Uthmani | `R.font.digital_khatt_v2` | `apps/android/app/src/main/res/font/digital_khatt_v2.otf` | `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/DigitalKhattV2.otf` | `0935c48269a57c9808e52dfae47864c189396452901c689977156036a72dd217` | `docs/legal/evidence/fonts/active_reader_font_licenses/source_pages/QUL_DIGITAL_KHATT_V2_FONT_PAGE.html` | `docs/legal/evidence/fonts/active_reader_font_licenses/licenses/QUL_REPOSITORY_LICENSE_MIT.txt`; `docs/legal/evidence/fonts/active_reader_font_licenses/source_pages/QUL_FAQ_RESOURCE_LICENSE_NOTICE.html` | `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/QuranFonts.kt`; `apps/android/app/build.gradle.kts`; `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/mushaf/MushafPageScreen.kt` | CLEARED | Active Uthmani reader font; coverage helper flags `U+06EA` and `U+06EB` as unsupported |

## Retired Or Not Active

| Resource / file | Source evidence path | Status | Notes |
|---|---|---|---|
| `digital_khatt_indopak.otf` | `docs/legal/evidence/fonts/DigitalKhattIndoPak.otf` | REMOVED | Not bundled in the APK and not used as the active IndoPak reader font |
| `uthmanic_hafs_v22.ttf` | `sourcedata/8/extracted/UthmanicHafs_V22.ttf` | REMOVED | Not bundled in the APK; do not reintroduce without separate source/license clearance |
| `KFGQPCNastaleeq-Regular.ttf` | `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/KFGQPCNastaleeq-Regular.ttf` | NOT BUNDLED | Source-only audit copy unless explicit separate clearance is obtained |
| `QPC_V2_Hafs.ttf` | `docs/legal/evidence/fonts/active_reader_font_licenses/source_pages/QUL_QPC_V2_FONT_PAGE.html` | BLOCKING | Direct evidence download failed; do not use as a cleared font |

## Release Notes

- The active Android reader fonts are now fixed to `indopak_nastaleeq.ttf` for IndoPak and `digital_khatt_v2.otf` for Uthmani.
- Retired bundled fonts have been removed from `apps/android/app/src/main/res/font`.
- Quran display text, ayah order, surah order, page mapping, and juz mapping were not modified.
