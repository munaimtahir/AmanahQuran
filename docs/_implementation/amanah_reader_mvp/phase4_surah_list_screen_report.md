# Phase 4 Surah List Screen Report

## Added

- `SurahListScreen`

## Behavior

- Shows title `Quran`.
- Loads Surah list from `SurahListViewModel`.
- Displays Surah number, Arabic name, readable/simple name, ayah count, and revelation type when available.
- Shows loading and error states.
- Tapping a Surah opens the Surah reader route through navigation callback.

## Offline Rule

- Uses the packaged Room database asset through the existing local repository.
- No network dependency was added.

## Guardrails

- No ads, banners, donations, popups, analytics, login, or tracking added.
