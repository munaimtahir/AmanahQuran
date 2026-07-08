# Trust Center Wording Review

## Review Scope

Reviewed the Trust Center asset JSON and the current Trust Center UI wording.

## Findings

- No overclaiming was found in the current JSON.
- Source attribution is present and now includes public URLs and license references.
- The no-modification statement is explicit and conservative.
- The privacy pledge is explicit and conservative.
- Offline behavior is clearly stated in the project docs and Trust Center content.
- Manual review status is presented as approved rather than pending.
- Font/license status is presented as reviewed and documented.
- Release approval and app version are surfaced without implying extra claims.

## Caution

The current UI does not yet surface every ideal release metadata field such as source version and checksum. That is acceptable for internal review, but the wording must not imply more verification than actually exists.

## Recommended Safe Wording

- Quran text is packaged from documented source files.
- Display text is not modified at runtime.
- Search uses a separate normalized index and is not used for display.
- Manual review status: approved and archived.
- Fonts: bundled Quran fonts are documented with reference URLs and reviewed license notices.
- Release approval, app version, and source references are surfaced conservatively.

## Decision

GO

The wording is now appropriate for the approved release posture and surfaces the required references without overclaiming.
