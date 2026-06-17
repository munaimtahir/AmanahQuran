# Phase 10 Tests Report

Added `AmanahContentDatabaseTest`.

Covered:

- DB asset exists.
- Trust JSON asset exists.
- Room opens the prepackaged DB.
- Surah count = 114.
- Ayah count = 6236.
- Uthmani rows = 6236.
- IndoPak rows = 6236.
- Search index rows = 6236.
- Ayah `1:1` loads in Uthmani and IndoPak.
- Ayah `2:255` loads in Uthmani and IndoPak.
- Search returns matching ayah keys while display text is loaded from `quran_texts`.
- Trust Center JSON loads.
- No prohibited tables exist.
- Font inventory is present and no font file extensions are bundled in `assets/fonts`.

Also updated route and validation-rule tests for the imported schema.
