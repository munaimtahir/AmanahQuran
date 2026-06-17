# Phase 3 Entities Report

Room entities were aligned to the actual candidate SQLite schema:

- `SurahEntity`
- `AyahEntity`
- `QuranTextEntity`
- `SearchIndexEntity`
- `ContentSourceEntity`
- `ContentValidationEntity`
- `MushafLayoutReferenceEntity`
- `FontInventoryEntity`

Schema notes:

- Candidate table names are preserved, including `quran_texts` and `content_validation`.
- Display text remains only in `quran_texts.display_text`.
- Search-normalized text remains only in `search_index.normalized_arabic`.
- `search_index.display_safe` uses the asset default value `0`.
- `search_index` and `quran_texts` foreign keys to `ayahs(ayah_key)` are represented.
- Imported content `id` primary keys are nullable in Kotlin because the SQLite schema uses `id INTEGER PRIMARY KEY` without an explicit `NOT NULL`; this was required for Room asset validation.

No translation, tafsir, audio, morphology, user, account, sync, analytics, or tracking entities were created.
