# Phase 2 Surah List ViewModel Report

## Added

- `SurahListViewModel`

## Behavior

- Loads all Surahs from `QuranContentRepository`.
- Exposes loading, Surah list, and error state through `SurahListUiState`.
- Handles open-Surah action by invoking a navigation callback with the selected Surah number.
- Does not hardcode Surah names except fallback text when a DB field is blank.

## Data Source

- Surah data comes from the packaged Room database through existing DAO/repository wiring.

## Guardrails

- No prohibited features added.
- No network dependency added.
- No Quran text display behavior added in this phase.
