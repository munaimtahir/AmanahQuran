# Phase 0 - Preflight Report

## Verification Summary

| Check | Status | Notes |
| --- | --- | --- |
| Packaged SQLite DB exists | PASS | `apps/android/app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite` |
| AI audit folder exists | PASS | `docs/_ai_quran_audit/` |
| Release gate folder exists | PASS | `docs/_release_gate/` |
| `sourcedata/` exists | FAIL | No `sourcedata/` directory found at repo root |
| `projectdata/managed/` exists | FAIL | No `projectdata/managed/` directory found at repo root |
| Source inventory/checksum report exists | PASS | Provenance metadata exists in packaged DB tables `content_sources` and `content_validation` |
| Uthmani source candidate exists | FAIL | Expected raw candidate file not present on disk |
| IndoPak source candidate exists | FAIL | Expected raw candidate file not present on disk |
| Current DB counts are intact | PASS | 114 surahs, 6,236 ayahs, 6,236 Uthmani rows, 6,236 IndoPak rows, 6,236 search rows |

## Verdict

BLOCKED

## Missing Paths

- `sourcedata/`
- `projectdata/managed/`

## Notes

- The packaged DB is present and internally consistent.
- Exact source-to-DB comparison cannot proceed without the raw/staging source files.
