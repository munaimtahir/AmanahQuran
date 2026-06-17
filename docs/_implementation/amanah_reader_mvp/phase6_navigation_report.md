# Phase 6 Navigation Report

## Routes

- Surah list route: `surah-list`.
- Surah reader route: `reader/surah/{surahNumber}`.
- Concrete Surah reader route helper: `AppRoute.surahReader(surahNumber)`.

## Wiring

- Home reader entry opens `SurahListScreen`.
- `SurahListScreen` opens `SurahReaderScreen` for the selected Surah.
- `SurahReaderScreen` supports back navigation through `NavController.popBackStack()`.
- `ContentProofScreen` remains on route `content-proof`.
- `ContentProofScreen` is not linked from Home and remains internal/debug-only by route knowledge.

## Guardrails

- No public-facing proof/debug entry was added.
- No prohibited feature routes were added.
