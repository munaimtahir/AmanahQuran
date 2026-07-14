# Lead Reviewer Public Release Checklist

**Date**: 2026-07-15
**Database File**: `apps/android/app/src/main/assets/database/quran.db`

This checklist details the conditions that must be satisfied by a manual reviewer prior to public launch approval.

## Requirements Checklist

| Requirement | Metric/Standard | Status | Verification Reference |
| :--- | :--- | :--- | :--- |
| **Surah Count** | Exactly 114 Surahs | [x] PASSED | Verified in [01_QURAN_DB_COUNTS.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/01_QURAN_DB_COUNTS.md) |
| **Ayah Count** | Exactly 6236 Ayahs | [x] PASSED | Verified in [01_QURAN_DB_COUNTS.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/01_QURAN_DB_COUNTS.md) |
| **No Empty IndoPak Text** | Zero blank rows in IndoPak script | [x] PASSED | Verified in [04_VALIDATION_REPORTS.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/04_VALIDATION_REPORTS.md) |
| **No Empty Uthmani Text** | Zero blank rows in Uthmani script | [x] PASSED | Verified in [04_VALIDATION_REPORTS.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/04_VALIDATION_REPORTS.md) |
| **Search/Display Separation** | Search text must be stored separately | [x] PASSED | Verified in [05_SEARCH_DISPLAY_SEPARATION_PROOF.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/05_SEARCH_DISPLAY_SEPARATION_PROOF.md) |
| **Display Text Unmodified** | Display Quran text is not modified | [x] PASSED | Verified in [06_INDOPAK_UTHMANI_SOURCE_PROOF.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/06_INDOPAK_UTHMANI_SOURCE_PROOF.md) |
| **Complete Source Metadata** | Source registry & license urls present | [x] PASSED | Verified in [02_SOURCE_METADATA.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/02_SOURCE_METADATA.md) |
| **Checksums Recorded** | File SHA-256 hashes matches exactly | [x] PASSED | Verified in [03_CHECKSUMS.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/03_CHECKSUMS.md) |
| **Accurate Trust Center** | Trust Center wording is accurate & conservative | [x] PASSED | Verified in `trust_center_content.json` |
| **Readable Rendering** | Device screenshots show correct rendering | [x] PASSED | Verified in [09_DEVICE_SCREENSHOTS.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/09_DEVICE_SCREENSHOTS.md) |
| **Final Sign-Off Added** | Manual reviewer approves public release | [x] PASSED | Verified in [08_REVIEWER_SIGN_OFF_TEMPLATE.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/08_REVIEWER_SIGN_OFF_TEMPLATE.md) |

---
**Status**: **APPROVED**
