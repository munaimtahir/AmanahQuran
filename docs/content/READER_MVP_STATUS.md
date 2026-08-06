# Reader MVP Status

Status: released/approved in the v1.0.5 release ledger (2026-07-23).

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

- Status: implemented and release-verified.
- Supported scripts:
  - IndoPak
  - Uthmani
- Default: IndoPak.
- Persistence: local and canonical; historical stale-state findings were resolved
  before the v1.0.5 approval record.

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

## Release Evidence

The current release decision is recorded in
`docs/_release_gate/RELEASE_LEDGER.md` and the public-approval evidence folder.
Historical P0 findings remain archived in dated device-test reports. Any reader
change requires new content-integrity, offline, and physical-device regression
evidence before release.
