# Diff Inventory

## Source changes made in this sprint

| File | Type | Why it changed | Safe to commit | Notes |
|---|---|---|---|---|
| `apps/android/app/build.gradle.kts` | Manual | Added an explicit `amanahReleaseTrack` release policy, route-aware validation, and an internal-testing marker file. | Yes | Keeps public release strict and lets internal release assemble without weakening policy. |
| `scripts/scan_packaged_content_assets.py` | Manual | Added artifact labeling and track-aware report output so internal-testing packaging is explicit. | Yes | Public profile still fails on unapproved assets. |

## Generated evidence created by the build

| File or path | Type | Why it changed | Safe to commit | Notes |
|---|---|---|---|---|
| `apps/android/app/build/outputs/apk/release/app-release.apk` | Generated | Internal release artifact produced by Gradle | No | Build output only. |
| `apps/android/app/build/reports/amanah-release/release_track.txt` | Generated | Internal artifact marker written by the release task | Usually no | Evidence artifact, not source. |
| `build/reports/indopak_glyph_coverage_report.txt` | Generated | Font validation evidence | No | Confirms coverage pass. |
| `build/reports/uthmani_glyph_coverage_report.txt` | Generated | Font validation evidence | No | Confirms coverage pass. |

## Existing unrelated worktree change

- `CONTENT_RESET_AUDIT.md` was already modified in the worktree and was not changed as part of this sprint.
