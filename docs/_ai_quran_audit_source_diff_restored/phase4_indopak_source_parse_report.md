# Phase 4: IndoPak Source Parse Report

This report documents the parsing of the QUL Digital Khatt IndoPak display source.

## Parser Details & Configuration

- **Source Files Inspected**:
  1. `sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.db.zip` (Extracted `digital-khatt-indopak-ayah-by-ayah-script.db`)
  2. `sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.json.zip` (Extracted `digital-khatt-indopak-ayah-by-ayah-script.json`)
- **Comparison & Selection**:
  - The JSON source contains exactly **6,236** verse entries, keyed by canonical verse reference (e.g. `"104:2"`).
  - The SQLite source contains exactly **6,236** verse rows in its `verses` table.
  - Both sources contain identical Quran text strings, including Unicode formatting characters and end-of-ayah markers (e.g., `۝`).
  - **Selected Source**: SQLite database (`digital-khatt-indopak-ayah-by-ayah-script.db`).
  - **Selected Fields**: Table `verses`, columns `surah` (for surah number), `ayah` (for ayah number), and `text` (for `sourceDisplayText`).
  - **Selection Rationale**: The SQLite database allows structured query operations and sequential parsing without loading a single large JSON string into memory, making the parser more robust and efficient.
- **Trimming / Modifications**: None. Extracted strings were copied byte-for-byte. No whitespace stripping (such as `.strip()`), whitespace normalization, or Unicode normalization (such as NFC/NFD) was applied.

## Parser Execution Summary

- **Source File**: `scratch_temp/sourcedata_3_extracted/digital-khatt-indopak-ayah-by-ayah-script.db`
- **Output File**: `docs/_ai_quran_audit_source_diff_restored/indopak_source_extract.csv`
- **Expected Ayah Rows**: 6,236
- **Actual Parsed Rows**: 6,236
- **Status**: Complete Success

## Verdict

**GO**
Exactly 6,236 IndoPak rows were successfully parsed and extracted from the SQLite source without any errors or warnings. The audit process can continue.
