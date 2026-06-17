# Phase 3 Reader ViewModel Report

## Added

- `ReaderViewModel`

## Behavior

- Loads ayahs for the selected Surah.
- Loads display text for the selected script.
- Defaults to IndoPak because no persisted script setting exists yet.
- Supports switching between:
  - `INDOPAK`
  - `UTHMANI`
- Preserves the current Surah when script changes.
- Preserves ayah identity by keeping `ayahKey`, Surah number, and ayah number from the `ayahs` table.

## Display Text Rule

- Reader display text comes from `quran_texts.display_text` through `QuranContentRepository`.
- `search_index.normalized_arabic` is not used for reader display.

## Deferred

- Bookmarks and last-read were not added in this sprint.
- Script persistence is handled in phase 7 depending on existing settings infrastructure.
