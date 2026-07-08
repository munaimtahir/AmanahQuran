# Content Integrity Audit

## Core Counts

| Item | Value | Result |
|---|---:|---|
| Surahs | 114 | Pass |
| Ayahs | 6236 | Pass |
| Uthmani rows | 6236 | Pass |
| IndoPak rows | 6236 | Pass |
| Search rows | 6236 | Pass |
| `content_sources` rows | 120 | Pass |
| `content_validation` rows | 9 | Pass |
| `font_inventory` rows | 41 | Pass |
| `mushaf_layout_references` rows | 1118 | Pass |

## Separation Rules

- Quran display text comes from `quran_texts.display_text`.
- Search normalization is stored separately in `search_index.normalized_arabic`.
- Normalized search text is not used as display Quran text.
- Bookmarks and last-read use canonical identity values such as `surah:ayah` and page number.

## Prohibited Content Review

The packaged DB schema does not include translation, tafsir, audio, morphology, word-by-word, accounts, sync, analytics, or ad tables.

## Verdict

GO

The packaged content remains consistent with the sacred-reader content contract.
