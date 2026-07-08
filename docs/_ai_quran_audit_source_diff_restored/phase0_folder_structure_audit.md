# Phase 0: Folder Structure Audit

This report documents the verification of the workspace structure, raw source data folders, and the candidate SQLite database for the Amanah Quran project.

## Verification of Directories & Files

| Path | Status | Details |
| :--- | :--- | :--- |
| `sourcedata/` | **Present** | Root source data directory. |
| `sourcedata/1/` | **Present** | Contains Uthmani XML display source (`quran-uthmani.xml`). |
| `sourcedata/2/` | **Present** | Contains simple clean XML search/cross-check source (`quran-simple-clean.xml`). |
| `sourcedata/3/` | **Present** | Contains IndoPak display source files (zipped DB and JSON). |
| `sourcedata/4/` | **Present** | Contains IndoPak Nastaleeq backup source files (zipped DB and JSON). |
| `sourcedata/5/` | **Present** | Contains QUL Metadata files (zipped JSON and SQLite). |
| `sourcedata/6/` | **Present** | Contains Mushaf layout references (zipped). |
| `sourcedata/7/` | **Present** | Contains Quran Meta cross-check data (zipped). |
| `sourcedata/8/` | **Present** | Contains fonts and volt/ligatures files (zipped/compressed). |
| `sourcedata/9/` | **Present** | Contains Quran Foundation HTML/PDF documentation. |
| `sourcedata/10/` | **Present** | Contains Quranic Arabic Corpus morphology zip file. |
| `projectdata/managed/` | **Present** | Contains 48 managed reports and content_sources configuration. |
| `sourcedata/managed/` | **Present** | Contains a pre-import report (`pre_android_import_audit_report.md`). |
| `apps/android/app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite` | **Present** | Candidate database file (size: 5,963,776 bytes). |

## Analysis of `managed/` Directory Location

- **`projectdata/managed/`**: Exists and is populated with 48 reports and metadata files.
- **`sourcedata/managed/`**: Exists and contains a pre-import audit report.
- **Recommendation**: The primary directory for managed reports should be `projectdata/managed/`. The folder `sourcedata/managed/` is present and usable as a secondary source, but all new audit outputs will be written to `docs/_ai_quran_audit_source_diff_restored/` as instructed.

## Verdict

**GO**
Both raw folders (`sourcedata/1` to `sourcedata/10`) and the packaged candidate database (`apps/android/app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite`) are present. The audit can continue.
