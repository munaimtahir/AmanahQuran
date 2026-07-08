# Reader MVP Status

Status: internal reader testing enabled.

Public release status: blocked pending P0 fixes and fresh physical-device evidence.

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

- Status: implemented through reader/settings state, but needs post-fix verification.
- Supported scripts:
  - IndoPak
  - Uthmani
- Default: IndoPak.
- Persistence: implemented, but Continue Reading previously showed stale script state on the Mushaf path.

## Display Source Rule

- Reader display text must come from `quran_texts.display_text`.
- Uthmani and IndoPak display text remain separate rows by `script_type`.
- Quran display text is not generated, converted, normalized, or modified at runtime.

## Search/Display Separation

- `search_index.normalized_arabic` is search-only.
- Normalized search text is not displayed in the reader.
- Tests verify reader output matches `quran_texts.display_text`, not `search_index.normalized_arabic`.

## Not Added

- Fonts are bundled and documented in the Trust Center.
- No translations added.
- No tafsir added.
- No audio added.
- No tajweed, qiraat, morphology, or word-by-word feature added.
- No accounts, login, sync, analytics, ads, tracking, monetization, prayer times, Qibla, or Islamic calendar added.
- No runtime permissions added.
- No network dependency added for reading.

## Public Release Blockers

- Search/bookmark exact ayah anchors must open the selected ayah, not the Surah start.
- Continue Reading must restore the correct script/content without multi-second delay.
- Trust Center must not claim verified release status while packaged metadata is placeholder, `NOT VERIFIED`, or `PENDING REVIEW`.
- Human/scholarly review evidence must contain reviewer identity, scope, scripts, decision, date, and evidence reference.
- A fresh non-empty physical-device regression report must pass after P0 fixes.
