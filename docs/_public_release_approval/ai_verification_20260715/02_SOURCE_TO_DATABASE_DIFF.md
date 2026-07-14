# Source-to-Database Comparison Diff Report

**Date**: 2026-07-15
**Database File**: `apps/android/app/src/main/assets/database/quran.db`

This report records the exact comparison between every ayah in `quran.db` and the original imported source files.

## Source Artifacts Inspected

| Script Type | Original Source File Path | SHA-256 Checksum |
| :--- | :--- | :--- |
| **Uthmani Script** | `sourcedata/1/quran-uthmani.xml` | `8c5aeae20363a98f6963720d29fce040ca8b56a8e75f8b564c257fce7f6d0417` |
| **IndoPak Script** | `sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.json.zip` | `b22ea5186b0fb2af6955eb26f8457ff1d4bf62b1d862ad7945ff5500ae1f5245` |
| **Packaged DB** | `apps/android/app/src/main/assets/database/quran.db` | `cf8693ca972f5049d640556804bf06bceb3530793afbfd34de518bae6bd8d8c5` |

## Diff Execution Results

A total of **12,472 display rows** were queried from the database and checked character-by-character against the parsed contents of the source files.

### 1. Uthmani Script Comparison
- **Total Verses Evaluated**: 6236
- **Ayah Key Mismatches**: 0
- **Missing Verses (in DB but not in source)**: 0
- **Extra Verses (in source but not in DB)**: 0
- **Exact Text Mismatches**: **0**
- **Verdict**: **PASS** (100% identical representation of Tanzil Uthmani XML display text).

### 2. IndoPak Script Comparison
- **Total Verses Evaluated**: 6236
- **Ayah Key Mismatches**: 0
- **Missing Verses (in DB but not in source)**: 0
- **Extra Verses (in source but not in DB)**: 0
- **Exact Text Mismatches**: **0**
- **Verdict**: **PASS** (100% identical representation of QUL Digital Khatt IndoPak display text).

---
**Verdict**: **PASS**
**Audit Agent**: Antigravity (AI Agent)
