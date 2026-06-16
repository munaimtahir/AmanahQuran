# Quran Content Contract

## Overview
This document defines the platform-neutral contract for Quranic content within the Amanah Quran project. All implementations (Android, Web, etc.) must adhere to these rules.

## Content Integrity
1. **Immutable Display Text:** Quran display text must never be modified or derived from other text forms (like normalized text) at runtime.
2. **Separation of Concerns:** Search-normalized text must be stored separately and must NEVER be used for display.
3. **Canonical References:** Use `surahNumber:ayahNumber` (e.g., `2:255`) and `pageNumber` as the primary keys for all content referencing (bookmarks, last-read, etc.).

## Data Model
- **Surah:** `surahNumber`, `nameArabic`, `nameSimple`, `ayahCount`.
- **Ayah:** `ayahKey`, `surahNumber`, `ayahNumber`, `juzNumber`, `pageNumber`.
- **QuranText:** `ayahKey`, `scriptType` (INDOPAK/UTHMANI), `displayText`, `checksum`.
- **SearchIndex:** `ayahKey`, `normalizedArabic`.

## Validation
Every content pack must pass a validation suite including:
- 114 Surahs.
- 6236 Ayahs.
- Matching checksums.
- No blank display text.
