# 05 — Content and Data Contracts

## Core Rule

Quran display text and search-normalized text must be separate.

## Canonical Reference

Use:

```text
ayahKey = "{surahNumber}:{ayahNumber}"
```

Example:

```text
2:255
```

## Surah

Required fields:

| Field | Type | Required |
|---|---|---|
| id | Int | Yes |
| number | Int | Yes |
| nameArabic | String | Yes |
| nameSimple | String | Yes |
| nameUrdu | String? | No |
| revelationType | String? | No |
| ayahCount | Int | Yes |

Validation:

- Exactly 114 Surahs.
- Surah numbers must be unique.
- Ayah count must be positive.

## Ayah

Required fields:

| Field | Type | Required |
|---|---|---|
| id | Int | Yes |
| ayahKey | String | Yes |
| surahNumber | Int | Yes |
| ayahNumber | Int | Yes |
| juzNumber | Int | Yes |
| pageNumber | Int | Yes |
| hizbNumber | Int? | No |
| rukuNumber | Int? | No |
| sajdahType | String? | No |

Validation:

- Exactly 6236 ayahs.
- No duplicate ayah keys.
- No missing ayah keys.
- Every ayah belongs to valid Surah.
- Every ayah has valid Juz and page mapping.

## QuranText

Required fields:

| Field | Type | Required |
|---|---|---|
| id | Int | Yes |
| ayahKey | String | Yes |
| scriptType | String | Yes |
| displayText | String | Yes |
| sourceId | Int | Yes |
| checksum | String? | Optional |

Allowed `scriptType`:

- `INDOPAK`
- `UTHMANI`

Validation:

- Display text must not be empty.
- IndoPak and Uthmani text must be stored separately.
- Display text must not be generated from search-normalized text.
- Display text must not be altered at runtime.

## SearchIndex

Required fields:

| Field | Type | Required |
|---|---|---|
| id | Int | Yes |
| ayahKey | String | Yes |
| normalizedArabic | String | Yes |
| surahSearchTerms | String? | Optional |
| pageSearchTerms | String? | Optional |
| juzSearchTerms | String? | Optional |

Validation:

- Search index count should match ayah count.
- Normalized text is only for search.
- Normalized text must never be rendered as Quran display text.

## Bookmark

Required fields:

| Field | Type | Required |
|---|---|---|
| id | Int | Yes |
| bookmarkType | String | Yes |
| ayahKey | String? | Required for ayah bookmark |
| pageNumber | Int? | Required for page bookmark |
| createdAt | Long | Yes |
| updatedAt | Long | Yes |

Allowed `bookmarkType`:

- `AYAH`
- `PAGE`

Validation:

- Ayah bookmark must have ayahKey.
- Page bookmark must have pageNumber.
- Bookmark identity must not depend on display script.

## LastRead

Required fields:

| Field | Type | Required |
|---|---|---|
| id | Int | Yes |
| ayahKey | String | Yes |
| surahNumber | Int | Yes |
| ayahNumber | Int | Yes |
| juzNumber | Int | Yes |
| pageNumber | Int | Yes |
| scriptType | String | Yes |
| scrollOffset | Int? | Optional |
| updatedAt | Long | Yes |

Validation:

- Must reference valid ayah.
- Must work offline.
- Must survive app restart.

## ContentSource

Required fields:

| Field | Type | Required |
|---|---|---|
| id | Int | Yes |
| contentType | String | Yes |
| scriptType | String? | Required for script-specific Quran text |
| sourceName | String | Yes |
| sourceUrl | String | Yes |
| license | String | Yes |
| version | String | Yes |
| checksum | String | Yes |
| importDate | String | Yes |
| validationStatus | String | Yes |
| reviewerStatus | String | Yes |

Validation:

- No source field may be blank.
- License must be reviewed before release.
- Checksum must match packaged content.

## ContentValidation

Required fields:

| Field | Type | Required |
|---|---|---|
| id | Int | Yes |
| validationName | String | Yes |
| expectedValue | String | Yes |
| actualValue | String | Yes |
| passed | Boolean | Yes |
| checkedAt | Long | Yes |

Required validation names:

- `surah_count`
- `ayah_count`
- `ayah_keys_complete`
- `no_duplicate_ayah_keys`
- `indopak_text_complete`
- `uthmani_text_complete`
- `search_index_count`
- `content_sources_complete`
- `checksum_verified`

## ContentManifest

Required top-level fields:

```json
{
  "app": "Amanah Quran",
  "project_identity": "Amanah-e-Kisa",
  "content_manifest_version": "",
  "generated_at": "",
  "packs": [],
  "validation_summary": {}
}
```

Each content pack must include:

- Pack ID.
- Content type.
- Script type if applicable.
- Source name.
- Source URL.
- License.
- Version.
- Checksum algorithm.
- Checksum.
- Import date.
- Ayah count.
- Surah count.
- Validation status.
- Manual review status.
