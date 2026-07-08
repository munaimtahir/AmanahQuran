# Phase 2: Identify Exact Comparison Sources

This report identifies the exact source files selected for comparison against the target candidate SQLite database.

## Selected Sources

### A. Uthmani Script Display Source
- **Path**: `sourcedata/1/quran-uthmani.xml`
- **Source Type**: Tanzil Uthmani XML
- **Use Case**: Primary Uthmani display source for comparison with the candidate SQLite database.

### B. IndoPak Script Display Source
- **Path**: `sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.db.zip` (unzipped to `scratch_temp/sourcedata_3_extracted/digital-khatt-indopak-ayah-by-ayah-script.db`)
- **Source Type**: SQLite Database (`verses` table, `text` column)
- **Use Case**: Primary IndoPak display source.
- **Selection Rationale**: Both the SQLite database and the JSON source contain the same content (6,236 verses mapped by `verse_key` or `surah` and `ayah`). The SQLite database was selected because it cleanly exposes exact ayah-by-ayah display text and can be efficiently queried using standard SQL without parsing large JSON strings.

### C. Search Cross-Check Source
- **Path**: `sourcedata/2/quran-simple-clean.xml`
- **Source Type**: Tanzil Simple Clean XML
- **Use Case**: Used strictly for search normalization cross-checks, not for visual/display Quran text.

### D. Metadata and Page Layout Sources
- **Path**: `sourcedata/5/` (QUL Metadata JSON/SQLite files)
- **Path**: `sourcedata/6/` (Page layout references, specifically mushaf layout mappings)
- **Use Case**: Validating structural properties (surah info, juz info, page mapping).

## Verdict

**GO**
Both Uthmani and IndoPak primary display sources are identified and verified as accessible. The audit process can continue.
