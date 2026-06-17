# Reader MVP Status

Status: internal reader testing enabled.

Public release is not approved.

## Surah List

- Status: implemented.
- Route: `surah-list`.
- Data source: packaged Room database.
- Shows Surah number, Arabic name, simple name, ayah count, and revelation type when available.

## Surah Reader

- Status: implemented.
- Route: `reader/surah/{surahNumber}`.
- Data source: packaged Room database.
- Displays ayahs vertically from the selected Surah.
- Shows ayah number markers.

## Script Switch

- Status: implemented locally in `ReaderViewModel`.
- Supported scripts:
  - IndoPak
  - Uthmani
- Default: IndoPak.
- Persistence: deferred until DataStore/settings architecture exists.

## Display Source Rule

- Reader display text must come from `quran_texts.display_text`.
- Uthmani and IndoPak display text remain separate rows by `script_type`.
- Quran display text is not generated, converted, normalized, or modified at runtime.

## Search/Display Separation

- `search_index.normalized_arabic` is search-only.
- Normalized search text is not displayed in the reader.
- Tests verify reader output matches `quran_texts.display_text`, not `search_index.normalized_arabic`.

## Not Added

- No fonts bundled.
- No translations added.
- No tafsir added.
- No audio added.
- No tajweed, qiraat, morphology, or word-by-word feature added.
- No accounts, login, sync, analytics, ads, tracking, monetization, prayer times, Qibla, or Islamic calendar added.
- No runtime permissions added.
- No network dependency added for reading.

## Public Release Blockers

- Manual Quran text review.
- Font/license decisions.
- Real-device page navigation verification.
- Trust Center wording review.
