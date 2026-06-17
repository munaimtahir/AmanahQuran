# Phase 5 Surah Reader Screen Report

## Added

- `SurahReaderScreen`

## Behavior

- Shows current Surah name.
- Shows selected script indicator.
- Provides script switch controls for IndoPak and Uthmani.
- Displays ayahs vertically in a `LazyColumn`.
- Each ayah displays Arabic Quran text and an ayah number marker.

## Text Integrity

- Display text is supplied by `ReaderViewModel`.
- `ReaderViewModel` uses `QuranContentRepository`, which reads `quran_texts.display_text`.
- No normalized search text is displayed.
- No Quran display text generation, conversion, or normalization is added.

## Guardrails

- No translation added.
- No tafsir added.
- No audio added.
- No share feature added.
- No fonts bundled.
