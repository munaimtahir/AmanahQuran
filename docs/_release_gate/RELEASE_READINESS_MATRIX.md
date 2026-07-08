# Release Readiness Matrix

| Gate | Status | Evidence | Blocking? | Notes |
|---|---|---|---|---|
| Content source traceability | GO | Packaged DB `content_sources` / `content_validation` tables | No | Source rows and checksums exist |
| Content DB validation | GO | `content_validation` rows, DB counts | No | 114 surahs, 6236 ayahs |
| Android import validation | GO | Existing content import reports and packaged DB | No | Import assets present |
| Reader functionality | CONDITIONAL | `docs/testing/amanah_device_test_20260625_014321/` | Yes | Standard Surah/Juz/Page paths are fast; Continue Reading path failed |
| User state / bookmarks | CONDITIONAL | Focused ADB verification on 2026-06-26 | Yes | Ayah bookmark `2:255` now opens `2:255`; full regression still required |
| Search | CONDITIONAL | Focused ADB verification on 2026-06-26 | Yes | `2:255` now opens `2:255`; aliases pass unit tests; full regression still required |
| Page / Juz navigation | CONDITIONAL | `docs/testing/amanah_device_test_20260625_014321/` | Yes | Page timings passed; Juz 30 boundary screenshot must be recaptured |
| Real-device validation | FAIL | `docs/testing/amanah_device_test_20260625_014321/` | Yes | Latest valid non-empty run blocks public release |
| AI structural audit | GO | `docs/_ai_quran_audit/phase1_structural_content_audit.md` | No | Structural completeness passed |
| AI suspicious-character audit | GO | `docs/_ai_quran_audit/phase4_suspicious_character_audit.md` | No | No suspicious display-text issues found |
| AI search/display separation | GO | `docs/_ai_quran_audit/phase5_search_display_separation_audit.md` | No | Display text and normalized search text remain separate |
| Exact source-to-DB comparison | GO | `docs/_ai_quran_audit_source_diff_restored/` | No | Restored sources match candidate DB exactly (100%) |
| Manual Quran text review | PENDING | `docs/_release_gate/human_signoff/SIGNOFF_EVIDENCE_REQUIRED.md` | Yes | Required fields are placeholders |
| Font / license review | CONDITIONAL | `FONT_LICENSE_REVIEW.md` | Yes | Inventory documented; release approval must be explicit |
| Trust Center wording review | FAIL | Device report and Trust Center metadata | Yes | Verification wording contradicts app promise |
| Privacy / permission audit | GO | `PRIVACY_PERMISSION_AUDIT.md` | No | No dangerous or ad/tracking permissions |
| Final build / test / lint | GO | `phase10_final_build_test_lint_report.md` | No | Assemble, tests, and lint all passed |
| Play Store content / privacy declarations | PENDING | `PLAY_STORE_READINESS_NOTES.md` | Yes | Not ready for submission |
| Final release approval | BLOCKED | This matrix | Yes | Public release is blocked until P0 fixes pass a fresh device run |

Note: `docs/testing/amanah_device_test_after_p0_fixes_20260625_061944/` contains
empty placeholder files and is not valid post-fix evidence.
