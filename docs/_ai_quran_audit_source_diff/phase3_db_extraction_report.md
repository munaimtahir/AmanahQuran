# Phase 3 - DB Extraction Report

## Packaged DB

- Read-only extraction target: `apps/android/app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite`

## Extracted Counts

| Dataset | Rows | Status |
| --- | ---: | --- |
| Uthmani display rows | 6,236 | PASS |
| IndoPak display rows | 6,236 | PASS |

## Extracted Fields

- `ayah_key`
- `surah_number`
- `ayah_number`
- `script_type`
- `display_text`

## Notes

- Export was performed read-only.
- Search-normalized text was not used for display comparison.

## Verdict

PASS
