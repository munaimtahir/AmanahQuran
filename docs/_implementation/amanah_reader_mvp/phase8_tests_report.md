# Phase 8 Tests Report

## Added

- `MainDispatcherRule`
- `ReaderMvpViewModelTest`

## Coverage

- Surah list loads 114 Surahs.
- Reader loads Surah 1 with 7 ayahs.
- Reader loads Surah 2 with Uthmani text.
- Reader loads Surah 2 with IndoPak text.
- Script switch preserves `ayahKey`.
- Reader display text matches `quran_texts.display_text`.
- Sample `2:255` loads in both Uthmani and IndoPak.
- Search index normalized text is not used for reader display.

## Test Source Rule

- Tests compare reader display output with `QuranTextDao.getTextByAyahAndScript`.
- Tests compare against `SearchIndexDao.getSearchRow` to confirm normalized text is not rendered.

## Result

- Initial test harness failed because ViewModel state was asserted before asynchronous Room-backed loads completed.
- Fixed by awaiting non-loading `StateFlow` values in tests.
- `./gradlew test` passed after the fix.
