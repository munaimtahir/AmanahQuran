# Quran Data Audit

## Packaged database

The active packaged database is:

- `apps/android/app/src/main/assets/database/quran.db`

Observed counts:

- Surahs: 114
- Ayahs: 6236
- Quran text rows: 12472
- Search index rows: 6236
- Content source rows: 120
- Content validation rows: 9
- Font inventory rows: 41
- Mushaf layout reference rows: 1118

## Schema evidence

The packaged DB includes these relevant tables:

- `surahs`
- `ayahs`
- `quran_texts`
- `search_index`
- `content_sources`
- `content_validation`
- `font_inventory`
- `mushaf_layout_references`

## Important integrity checks

- Both IndoPak and Uthmani display text fields exist in `quran_texts`.
- Search-normalized Arabic is stored separately in `search_index.normalized_arabic`.
- `search_index.display_safe` exists to keep search-only text from being treated as display text.
- Quran display text is loaded from `quran_texts.display_text`.
- Bookmarks and last-read use canonical references like `surah:ayah` and page number.

## Source and validation metadata

Evidence exists in:

- `content-pipeline/06_generated_projectdata/content_manifest.json`
- `content-pipeline/06_generated_projectdata/license_manifest.json`
- `content-pipeline/06_generated_projectdata/trust_center_sources.json`
- `content-pipeline/06_generated_projectdata/validation_summary.json`
- `projectdata/managed/content_sources.json`
- `projectdata/managed/candidate_database_validation.json`
- `projectdata/managed/checksum_report.md`
- `docs/legal/LEGAL_EVIDENCE_SHA256SUMS.txt`

## Manual sign-off status

- Manual reviewer evidence is still not complete.
- The live Trust Center asset marks the IndoPak public-release source as unresolved.
- The mushaf page layout section is marked `NOT VERIFIED` with `PENDING REVIEW`.

## What is strong

- The database is real and populated.
- Text/search separation is explicit.
- Runtime readers use canonical ayah keys.
- A validation and checksum trail exists.

## What is not safe to claim yet

- Public-release Quran content approval.
- Manual scholar/reviewer approval.
- Final approval of the IndoPak public-release source in the live Trust Center asset.

