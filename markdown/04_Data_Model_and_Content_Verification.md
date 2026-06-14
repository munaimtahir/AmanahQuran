# Data Model and Content Verification
# Amanah Quran - V1 Sacred Reader MVP

## 1. Core Principle

Quran display text is sacred content and must be treated as immutable after verified import. Search optimization, normalization, indexing, and highlighting must never modify or replace the displayed Quran text.

## 2. Data Strategy

Use a prepackaged Room SQLite database bundled with the app. The database should be produced through a controlled import pipeline and frozen for release after verification.

## 3. Required Tables

## 3.1 `surahs`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | 1 to 114 |
| `name_arabic` | Text | Yes | Verified Arabic name |
| `name_simple` | Text | Yes | Search/display name |
| `name_urdu` | Text | No | Optional V1 metadata |
| `revelation_type` | Text | No | Only if verified |
| `ayah_count` | Integer | Yes | Count per Surah |

## 3.2 `ayahs`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Internal primary key |
| `ayah_key` | Text | Yes | Format: `surah:ayah`, e.g. `2:255` |
| `surah_number` | Integer | Yes | 1 to 114 |
| `ayah_number` | Integer | Yes | Ayah number within Surah |
| `juz_number` | Integer | Yes | 1 to 30 |
| `page_number` | Integer | Yes | Based on selected metadata source |
| `hizb_number` | Integer | No | Optional metadata |
| `ruku_number` | Integer | No | Optional metadata |
| `sajdah_type` | Text | No | Optional marker |

## 3.3 `quran_texts`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `ayah_key` | Text | Yes | Links to `ayahs.ayah_key` |
| `script_type` | Text | Yes | `INDOPAK` or `UTHMANI` |
| `display_text` | Text | Yes | Immutable verified text |
| `source_id` | Integer | Yes | Links to `content_sources.id` |
| `checksum` | Text | No | Optional per ayah; pack checksum required |

## 3.4 `search_index`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `ayah_key` | Text | Yes | Links to ayah |
| `normalized_arabic` | Text | Yes | Search only |
| `normalized_uthmani` | Text | No | Optional separate script search |
| `normalized_indopak` | Text | No | Optional separate script search |
| `surah_search_terms` | Text | No | For Surah lookup |

## 3.5 `bookmarks`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `bookmark_type` | Text | Yes | `AYAH` or `PAGE` |
| `ayah_key` | Text | Conditional | Required for ayah bookmark |
| `page_number` | Integer | Conditional | Required for page bookmark |
| `created_at` | Long | Yes | Timestamp |
| `updated_at` | Long | Yes | Timestamp |

## 3.6 `last_read`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Single row or DataStore equivalent |
| `ayah_key` | Text | Yes | Canonical last ayah |
| `surah_number` | Integer | Yes | Redundant but useful |
| `ayah_number` | Integer | Yes | Redundant but useful |
| `juz_number` | Integer | Yes | Redundant but useful |
| `page_number` | Integer | Yes | Redundant but useful |
| `script_type` | Text | Yes | Current selected script |
| `scroll_offset` | Integer | No | Optional |
| `updated_at` | Long | Yes | Timestamp |

## 3.7 `content_sources`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `content_type` | Text | Yes | Quran text, metadata, font, etc. |
| `script_type` | Text | Conditional | Required for script text |
| `source_name` | Text | Yes | Public source name |
| `source_url` | Text | Yes | Public source URL |
| `license` | Text | Yes | License/reuse status |
| `version` | Text | Yes | Source version |
| `checksum` | Text | Yes | Pack checksum |
| `import_date` | Text | Yes | Date imported |
| `validation_status` | Text | Yes | Pass/fail/pending |
| `reviewer_status` | Text | Yes | Manual review state |

## 3.8 `content_validation`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `validation_name` | Text | Yes | e.g. `surah_count` |
| `expected_value` | Text | Yes | Expected value |
| `actual_value` | Text | Yes | Observed value |
| `passed` | Boolean | Yes | Result |
| `severity` | Text | Yes | Critical/warning/info |
| `checked_at` | Long | Yes | Timestamp |

## 4. Content Import Pipeline

1. Select source after license review.
2. Download source outside the app build.
3. Record source metadata.
4. Generate source file checksum.
5. Import into staging database.
6. Validate Surah count = 114.
7. Validate total ayah count = 6236.
8. Validate ayah keys are complete.
9. Validate no duplicate ayah keys.
10. Validate no empty display text.
11. Validate script-specific text exists for IndoPak and Uthmani.
12. Generate normalized search text separately.
13. Generate final Room SQLite database.
14. Generate content manifest JSON.
15. Run checksum verification.
16. Submit for manual content review.
17. Freeze database for release.

## 5. Required Validation Rules

| Rule | Expected | Severity |
|---|---|---|
| Surah count | 114 | Critical |
| Ayah count | 6236 | Critical |
| Duplicate ayah keys | 0 | Critical |
| Missing IndoPak text | 0 | Critical |
| Missing Uthmani text | 0 | Critical |
| Missing page mapping | 0 | Critical |
| Missing Juz mapping | 0 | Critical |
| Missing source metadata | 0 | Critical |
| Missing license field | 0 | Critical |
| Missing checksum | 0 | Critical |
| Search index row count | Must match ayah count | Critical |

## 6. Build Gate

Release build must fail if any critical validation fails. The app should never ship with placeholder source data, missing checksums, missing license fields, incomplete Trust Center metadata, or unverified Quran display text.

## 7. Search Normalization Rules

Allowed only for search index:

1. Remove tatweel.
2. Normalize common Arabic letter variants.
3. Optionally remove/ignore harakat for search.
4. Optionally create FTS-compatible search text.

Forbidden:

1. Displaying normalized text as Quran text.
2. Replacing verified display text with normalized text.
3. Modifying Quran display text to improve search highlighting.
4. Saving user-visible ayah text from search index.

## 8. Content Manifest Requirements

Each content pack must include:

1. Pack ID.
2. Content type.
3. Script type, if applicable.
4. Source name.
5. Source URL.
6. License.
7. Source version.
8. Import date.
9. File checksum.
10. Ayah count.
11. Surah count.
12. Validation status.
13. Manual review status.
14. Reviewer name/role, if available.
15. Notes.

## 9. Manual Review Workflow

Before public release:

1. Freeze imported text.
2. Generate validation report.
3. Export representative samples from every Juz.
4. Review beginning, middle, and end of selected Surahs.
5. Review special cases: Sajdah ayahs, Bismillah handling, long ayahs, page boundaries.
6. Document reviewer status in `content_sources`.
7. Do not release if review status is pending.

## 10. Recommended Source Review Checklist

Before using any Quran text/font/source:

1. Is the source reputable?
2. Is the license compatible with free public app distribution?
3. Is redistribution inside APK allowed?
4. Is modification prohibited, restricted, or allowed only for technical formatting?
5. Does the source provide versioning?
6. Can checksums be recorded?
7. Are IndoPak and Uthmani scripts separate assets?
8. Are fonts licensed for embedding in Android apps?
9. Are page mappings tied to a known mushaf standard?
10. Can source attribution be shown inside Trust Center?
