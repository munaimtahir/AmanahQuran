# Evidence Integrity Report — UI Refinement (R1–R5)

Date: 2026-09-27. Scope: the benchmark and audit evidence merged in PR #1 (`feature/ui-refinement-evidence-driven`, merge `e69bf23`).

## Corrections made to the evidence

| # | Claim as merged | Finding | Action |
|---|---|---|---|
| 1 | Greentech suggestion chips "drive 60% of search traffic" (`AMANAH_UI_OPPORTUNITY_BACKLOG.md`) | No source anywhere in the evidence | Removed, and a note left in its place |
| 2 | Amanah's Dark theme is `#121212` (backlog PROP-07) | Code: `DarkBackground = #101714` | Corrected |
| 3 | Design spec: cards 12dp; Dark = "true black OLED" | Code: cards 16dp; Dark is green-black; OLED black was only a proposal | Spec corrected; Black (OLED) is now a separate optional theme (R5) |
| 4 | "Habits: without streaks" (`AMANAH_VS_COMPETITORS.md`) | App ships a Reading Streak screen and Home streak line | Corrected |
| 5 | Ayah actions are a "centred modal dialog" | They were always a bottom-docked card | Corrected; R2 still improved the card |
| 6 | Amanah "9.5/10 Gold Standard", "200% compliant" | Amanah was only measured at 130% font scale; its score was self-assessed | Flagged as self-assessed; 200% is now covered by the JVM UI tests listed in `EVIDENCE_MANIFEST.md` (not device screenshots) |
| 7 | Backlog "READ-ONLY, nothing implemented" while code shipped in the same PR | Contradiction | Note updated to point to the R1–R5 sprints |
| 8 | Spec and backlog PROP numbers disagreed | Two numbering schemes | Backlog numbering is canonical; spec carries a mapping |

## Privacy
- `COMPETITOR_UI_BENCHMARK/LOGS/*_reviews.json`: removed `userName`, `userImage` and `reviewId` from every record (1,476 reviews across 8 files). Review text, score, dates, app version and developer replies are kept for analysis.

## Repository size
- Deleted `COMPETITOR_UI_BENCHMARK.zip` (78 MB) and `UI_AUDIT.zip` (27 MB) from the tree; both duplicated their folders. `.gitignore` now blocks re-adding them.
- **Still in git history:** those zips and the MP4 videos remain in past commits, so clones stay large until history is rewritten. That means a force-push to `main`, which only the owner should do. Steps are in `docs/ui/UI_REFINEMENT_IMPLEMENTATION_REPORT.md` → "History clean-up (owner action)".
