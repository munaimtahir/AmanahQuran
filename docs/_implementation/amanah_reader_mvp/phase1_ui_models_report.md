# Phase 1 UI Models Report

## Added

- `SurahListItem`
- `ReaderAyahUiModel`
- `ReaderUiState`
- `SurahListUiState`

## Source Rule

- `ReaderAyahUiModel.displayText` is populated through `QuranContentRepository.getAyahsForSurah`.
- The existing repository reads display text from `quran_texts.display_text`.
- `search_index.normalized_arabic` is not used by reader UI models or ViewModels.

## Script Types

- Uses existing `ScriptType` enum:
  - `INDOPAK`
  - `UTHMANI`

## Guardrails

- No raw source files modified.
- No packaged SQLite DB contents modified.
- No Quran text normalization or generation added.
