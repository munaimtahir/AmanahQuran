# Amanah Quran V2 Reader Experience Preparation

Updated: 2026-08-02

## Objective

Improve the offline reader experience while preserving Quran text integrity,
canonical `surah:ayah` references, existing local-only behavior, and the V1
scope guardrails.

## Implemented in this preparation pass

| Area | Implementation | Verification |
| --- | --- | --- |
| Continue Reading | Opens the containing surah/page and scrolls to the saved ayah instead of loading a single verse. | Reader ViewModel regression test. |
| Reading context | Shows the current ayah position, total ayahs, saved canonical location, and previous/next controls. | Kotlin compilation, unit tests, and lint pass. |
| Jump to ayah | Adds an offline ayah-number dialog for the current reader dataset. | Input is range-validated before opening. |
| Selection feedback | Selected ayah remains highlighted and becomes the scroll anchor when selected. | Reader state behavior covered by existing reader tests. |
| Reading progress | Displays ayah progress in continuous mode and page progress in Book Mode. | UI implementation; manual visual verification required. |
| Script continuity | IndoPak/Uthmani switching retains the canonical ayah anchor and reading position. | Existing script/anchor tests. |
| Reading comfort | Adds persisted Arabic line-spacing and horizontal-margin controls. | Settings persistence and backup compatibility are covered by repository/codec gates. |
| Bookmarks and sharing | Keeps existing exact-ayah bookmarks, page bookmarks, local sharing, and issue reporting. | Existing repository/UI-state tests. |
| Search flow | Keeps search results opening surrounding context with the matched ayah selected. | Existing search and reader anchor tests. |
| Accessibility | Preserves Elder Mode, larger targets, contrast-aware themes, and descriptive control labels. | Manual TalkBack and large-font QA remains required. |

## Remaining manual QA checklist

- Verify Previous, Next, and Jump to ayah with a physical touch device.
- Verify the selected ayah is visible after Continue Reading, search, bookmark,
  and script-switch flows.
- Verify Book Mode page progress and page-turn gestures in both scripts.
- Verify Arabic/Urdu typography at the smallest and largest supported sizes.
- Verify TalkBack announces the current position and all reader controls.
- Verify rotation/process recreation does not lose the canonical position.
- Repeat the release APK smoke test on the TECNO CH6i when ADB reconnects.

## Guardrail confirmation

This work adds no network dependency, ads, tracking, accounts, analytics,
monetization, audio, or altered Quran display text. Reading settings remain
local and backup-compatible; normalized search content remains separate from
rendered Quran text.
