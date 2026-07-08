# Phase 5: Database Display Extract Report

This report documents the extraction of display texts from the packaged candidate SQLite database.

## Extraction Scope & Rules

- **Database File**: `apps/android/app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite`
- **Read-Only Enforced**: Yes. The database connection was opened and queried in read-only mode using python's `sqlite3` driver.
- **Table Queried**: `quran_texts` (joined with the `ayahs` table to obtain `surah_number` and `ayah_number` columns).
- **Target Fields Extracted**:
  - `ayah_key` (canonical reference, e.g., `1:1`)
  - `surah_number`
  - `ayah_number`
  - `script_type` (`UTHMANI` or `INDOPAK`)
  - `display_text`
- **Exclusion of Search Normalization**: The `search_index` table was strictly avoided, and only the visual display texts from `quran_texts.display_text` were extracted.

## Extraction Results

- **Uthmani Script**:
  - Expected Rows: 6,236
  - Extracted Rows: 6,236
  - Output File: `docs/_ai_quran_audit_source_diff_restored/db_uthmani_display_extract.csv`
- **IndoPak Script**:
  - Expected Rows: 6,236
  - Extracted Rows: 6,236
  - Output File: `docs/_ai_quran_audit_source_diff_restored/db_indopak_display_extract.csv`

## Status

**COMPLETE SUCCESS**
Both Uthmani and IndoPak scripts were extracted from the candidate database, matching the target size of 6,236 rows each. The comparison process can proceed.
