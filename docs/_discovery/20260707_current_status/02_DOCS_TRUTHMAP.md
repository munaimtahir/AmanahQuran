# Docs Truth Map

## Current and useful

These documents appear current and are aligned with the live code and assets:

- `docs/CURRENT_APP_STATUS_AND_DEBUG_PLAN.md`
- `docs/content/READER_MVP_STATUS.md`
- `docs/content/SEARCH_STATUS.md`
- `docs/content/BOOKMARKS_LASTREAD_STATUS.md`
- `docs/content/PAGE_JUZ_NAVIGATION_STATUS.md`
- `docs/content/SETTINGS_THEMES_ELDER_MODE_STATUS.md`
- `docs/content/TRUST_CENTER_STATUS.md`
- `docs/content/PUBLIC_RELEASE_BLOCKERS.md`
- `docs/_release_gate/FINAL_RELEASE_GATE_SUMMARY.md`
- `docs/_release_gate/RELEASE_READINESS_MATRIX.md`
- `docs/legal/CONTENT_AND_FONT_LICENSE_MANIFEST.md`
- `docs/legal/FINAL_LICENSE_CLEARANCE_DECISION.md`
- `content-pipeline/05_validation_reports/*`
- `projectdata/managed/*`

## Current but conservative

These documents are conservative and should be treated as the release floor, not as a claim that public release is ready:

- `docs/_release_gate/TRUST_CENTER_WORDING_REVIEW.md`
- `docs/_release_gate/PRIVACY_PERMISSION_AUDIT.md`
- `docs/_release_gate/FONT_LICENSE_REVIEW.md`
- `docs/content/PUBLIC_RELEASE_BLOCKERS.md`
- `apps/android/app/src/main/assets/trust/trust_center_content.json`

## Stale or superseded

These look like older implementation branches or historical audit passes:

- `docs/_implementation/amanah_*`
- `docs/_ai_quran_audit/*`
- `docs/_ai_quran_audit_source_diff/*`
- `docs/_ai_quran_audit_source_diff_restored/*`

They are useful for history, but they should not override live code or the active Trust Center asset.

## Contradictions to note

### Font/license wording conflict

- `docs/_release_gate/FONT_LICENSE_REVIEW.md` and `docs/legal/FINAL_LICENSE_CLEARANCE_DECISION.md` say the active reader fonts are cleared for internal use and document an allowed release path.
- `apps/android/app/src/main/assets/trust/trust_center_content.json` is stricter and says the IndoPak public-release source is unresolved and the mushaf page layout is not verified.

### Release wording conflict

- The app asset is the live source of truth for the Trust Center UI.
- The release-gate docs are more optimistic about fonts than the app asset is about public release.
- The safe interpretation is still: internal testing allowed, public release blocked.

## Practical reading order

If I had to trust only a few sources for the current status, I would trust them in this order:

1. Live code and packaged assets.
2. `docs/testing/` device evidence.
3. `docs/content/` status docs.
4. `docs/_release_gate/` release-gate docs.
5. Older `_implementation` and `_ai_quran_audit` branches only for history.

