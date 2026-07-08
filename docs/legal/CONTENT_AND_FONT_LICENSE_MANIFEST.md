# Amanah Quran — Content and Font License Evidence Manifest

## Purpose

This folder records legal/source evidence for Quran text, Quran metadata, fonts, and other bundled resources used in the Amanah Quran APK.

Important: this manifest is evidence documentation, not a scholar/content approval. Quran text still requires manual content review and release sign-off.

---

## Evidence Files Collected

### QUL / Quranic Universal Library

- QUL repository license: `docs/legal/evidence/quran/QUL_REPOSITORY_LICENSE_MIT.txt`
- QUL FAQ resource-license notice: `docs/legal/evidence/quran/QUL_FAQ_RESOURCE_LICENSE_NOTICE.html`
- QUL home/developer resources page: `docs/legal/evidence/quran/QUL_HOME_DEVELOPER_RESOURCES.html`
- QUL all resources page: `docs/legal/evidence/quran/QUL_ALL_RESOURCES_PAGE.html`
- QUL Quran script resource page: `docs/legal/evidence/quran/QUL_QURAN_SCRIPT_RESOURCE_PAGE.html`
- QUL docs index: `docs/legal/evidence/quran/QUL_DOCS_INDEX.html`

### Active Reader Fonts

- IndoPak font page: `docs/legal/evidence/fonts/active_reader_font_licenses/source_pages/QUL_INDOPAK_NASTALEEQ_FONT_PAGE.html`
- IndoPak font file evidence: `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/Indopak_Nastaleeq_Hanafi_With_Waqf_Lazmi.ttf`
- Uthmani font page: `docs/legal/evidence/fonts/active_reader_font_licenses/source_pages/QUL_DIGITAL_KHATT_V2_FONT_PAGE.html`
- Uthmani font file evidence: `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/DigitalKhattV2.otf`

---

## Detected Project Evidence

- Scan targets: `docs/legal/evidence/scan/SCAN_TARGETS.txt`
- Detected URLs: `docs/legal/evidence/scan/DETECTED_SOURCE_URLS.txt`
- Downloaded detected source pages: `docs/legal/evidence/source_pages/`
- Detected bundled data/font files: `docs/legal/evidence/scan/DETECTED_CONTENT_AND_FONT_FILES.txt`
- Detected font files requiring explicit clearance: `docs/legal/evidence/scan/DETECTED_FONT_FILES.txt`
- SHA-256 checksums: `docs/legal/LEGAL_EVIDENCE_SHA256SUMS.txt`

---

## Clearance Table

| Resource | Source | License Evidence | Status | Notes |
|---|---|---|---|---|
| QUL repository/source pages | QUL / TarteelAI | MIT license + FAQ/resource pages saved | Evidence collected | Resource-level terms still need exact matching to used files |
| IndoPak Quran text | QUL resource, exact file to be confirmed from sourceData/projectData | QUL license/resource pages saved | Pending exact-file mapping | Fill exact resource name, export format, download date, checksum |
| Uthmani Quran text | QUL resource, exact file to be confirmed from sourceData/projectData | QUL license/resource pages saved | Pending exact-file mapping | Fill exact resource name, export format, download date, checksum |
| Quran page/juz metadata | QUL or project source, exact file to be confirmed | Source pages/checksums saved if URL detected | Pending exact-file mapping | Page mapping must match selected Mushaf/script |
| `apps/android/app/src/main/res/font/digital_khatt_indopak.otf` | QUL / DigitalKhatt evidence copy | OFL 1.1 license evidence saved | CLEARED | SHA-256 matches `docs/legal/evidence/fonts/DigitalKhattIndoPak.otf`; active IndoPak reader font; coverage helper now passes |
| `apps/android/app/src/main/res/font/digital_khatt_v2.otf` | QUL / active reader evidence copy | QUL page plus repository/FAQ terms evidence saved | CLEARED | SHA-256 matches `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/DigitalKhattV2.otf`; active Uthmani reader font; fallback helper uses `indopak_nastaleeq.ttf` for rare marks |
| `apps/android/app/src/main/res/font/indopak_nastaleeq.ttf` | QUL / active reader evidence copy | QUL page plus repository/FAQ terms evidence saved | CLEARED | SHA-256 matches `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/Indopak_Nastaleeq_Hanafi_With_Waqf_Lazmi.ttf`; bundled as Uthmani fallback for rare Quranic stop marks |
| `apps/android/app/src/main/res/font/uthmanic_hafs_v22.ttf` | Retired bundled font copy | Historical evidence retained only | REMOVED / NOT BUNDLED | Do not use without separate source/license clearance |
| `apps/android/app/src/main/res/font/KFGQPCNastaleeq-Regular.ttf` | Source-only audit candidate | License review exists in evidence folder | NOT BUNDLED | Keep out of APK unless explicit separate clearance is obtained |
| `apps/android/app/src/main/res/font/QPC_V2_Hafs.ttf` | Source-only audit candidate | Download path captured in evidence folder | NOT INTEGRATED | Direct evidence download failed; do not use as cleared font |

---

## Required Manual Completion

For each Quran text or metadata file actually imported into the app database, add:

- Exact source name
- Exact QUL/resource URL
- Export format: JSON / SQLite / CSV / other
- Download date
- App import date
- Version or resource ID
- License/attribution requirement
- SHA-256 checksum
- Validation result: 114 Surahs / 6236 ayahs / no duplicates / no empty display text
- Manual Quran review status
- Reviewer name/sign-off date

---

## Release Rule

Public release is blocked until every bundled Quran text, metadata file, and font has:

1. source URL,
2. license evidence,
3. checksum,
4. app import mapping,
5. validation status,
6. manual review status.

---

## Active Font Correction

The Android APK now bundles the current runtime reader fonts:

- `digital_khatt_indopak.otf` for IndoPak display.
- `digital_khatt_v2.otf` for primary Uthmani display.
- `indopak_nastaleeq.ttf` as a bundled Uthmani fallback for rare stop marks.

Retired or risky fonts are not bundled:

- `uthmanic_hafs_v22.ttf`
- `KFGQPCNastaleeq-Regular.ttf`
- `QPC_V2_Hafs.ttf`

Public release still depends on the non-font release gates listed elsewhere in the project, including Quran source-to-database mapping and manual review sign-off.
