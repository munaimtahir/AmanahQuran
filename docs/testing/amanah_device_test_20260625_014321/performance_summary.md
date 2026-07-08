# Amanah Quran Performance Summary

Test device: TECNO CH6i, Android 13 (SDK 33), arm64-v8a, 1080x2460 at 480 dpi.

Network state: airplane mode enabled, Wi-Fi disabled, mobile data disabled.

TTFQC is measured from the `AMANAH_PERF_READER` navigation click timestamp to `first_reader_content_composed`. The separate Mushaf/Continue Reading path does not emit the full performance stages, so its timing uses the navigation click and `AmanahFontAudit`/frame timestamps.

| Test | Script | Mode | TTFQC ms | Target | Result | Notes |
| ---- | ------ | ---- | -------: | -----: | ------ | ----- |
| Al-Fatihah | IndoPak | Surah | 284 | <300 | PASS | DB returned at 229 ms from tap |
| Al-Baqarah | IndoPak | Surah | 285 | <700 | PASS | 286 rows, 290 blocks |
| At-Tawbah | IndoPak | Surah | 230 | <500 | PASS | Heading present; no Bismillah |
| Juz 1 | IndoPak | Juz | 267 | <1000 | PASS | 148 rows |
| Juz 28 | IndoPak | Juz | 201 | <1000 | PASS | 137 rows |
| Juz 30 | IndoPak | Juz | 191 | <1000 | PASS | 564 rows, 639 blocks |
| Page 1 | IndoPak | Page | 288 | <300 | PASS | Slowest standard page |
| Page 2 | IndoPak | Page | 249 | <300 | PASS | |
| Page 59 | IndoPak | Page | 171 | <300 | PASS | |
| Page 76 | IndoPak | Page | 160 | <300 | PASS | |
| Page 532 | IndoPak | Page | 152 | <300 | PASS | |
| Page 540 | IndoPak | Page | 144 | <300 | PASS | |
| Search result 2:255 | Uthmani | Surah anchor | 331 | <500 | FAIL | Fast render, wrong anchor: opens 2:1 |
| Bookmark 2:255 | Uthmani | Surah anchor | 135 | <500 | FAIL | Cache hit, wrong anchor: opens 2:1 |
| Bookmark Page 540 | Uthmani | Page | 138 | <300 | PASS | Correct page/ayah identity |
| Search Al-Ikhlas | Uthmani | Surah | 287 | <300 | PASS | |
| Search Page 559 | Uthmani | Page | 259 | <300 | PASS | Last packaged page |
| Search Page 540 | IndoPak | Page | 245 | <300 | PASS | Same page after global script change |
| Continue Reading Page 540 | Uthmani | Mushaf page | 13,720 | <300 | FAIL | Loading screen >13 s |

## Fastest Operations

1. Bookmark-opened ayah render: 135 ms, but canonical target handling failed.
2. Bookmark-opened Uthmani Page 540: 138 ms.
3. IndoPak Page 540 from Page Index: 144 ms.
4. IndoPak Page 532: 152 ms.

## Slowest Operations

1. Continue Reading Page 540 through the Mushaf path: approximately 13,720 ms.
2. Search-opened 2:255: 331 ms, with wrong destination.
3. Page 1: 288 ms.
4. Al-Ikhlas Uthmani: 287 ms.
5. Al-Baqarah IndoPak: 285 ms.

## Surah Open Timings

| Surah | Script | DB duration | Mapping/build duration | UI success to visible | TTFQC |
| ----- | ------ | ----------: | ---------------------: | --------------------: | ----: |
| Al-Fatihah | IndoPak | 109 ms | 40 ms | 15 ms | 284 ms |
| Al-Baqarah | IndoPak | 45 ms | 17 ms | 12 ms | 285 ms |
| At-Tawbah | IndoPak | 84 ms | 33 ms | 14 ms | 230 ms |
| Al-Ikhlas | Uthmani | 63 ms | 19 ms | 9 ms | 287 ms |

The route-local reader work is fast. The larger variable is navigation and settings/ViewModel initialization before the DB query starts.

## Juz Open Timings

Juz 1, Juz 28, and Juz 30 all rendered in 191-267 ms from tap. Juz 30 handled 564 ayahs and 639 structural blocks without a timing regression.

Juz 4 and Juz 15 were not measured in the interrupted run and were not part of the resumed pending-test scope.

## Page Open Timings

All standard reader page opens passed the 300 ms target. Page 1 was the slowest at 288 ms; Page 540 from the index was 144 ms.

The high-page taps were based on UIAutomator bounds, not blind coordinates:

| Page | UI context | Tap |
| ---- | ---------- | --- |
| 1 | Row `[48,616][1032,843]` | `(540,729)` |
| 59 | Row `[48,424][1032,651]` | `(540,537)` |
| 76 | Row `[48,394][1032,621]` | `(540,507)` |
| 532 | Text bounds `[204,1765][383,1814]` in its clickable row | `(540,1789)` |
| 540 | Text bounds `[204,2164][383,2213]` in the visible clipped row | `(540,2180)` |

The packaged database reports page 559 as the maximum page for both configured layouts. Page 559 opened successfully via offline Search in 259 ms.

## Script Switch Timings

The app has no in-reader script switch control. Script selection is global in Settings, so a direct tap-to-same-content switch metric cannot be produced.

Objective reload evidence:

| Reload | TTFQC | Identity |
| ------ | ----: | -------- |
| Page 540 Uthmani via bookmark | 138 ms | Page 540, first visible 75:35 |
| Page 540 IndoPak via Search | 245 ms | Page 540, first visible 75:35 |
| Page 540 Uthmani via Continue Reading/Mushaf | ~13,720 ms | Page retained, initial stale IndoPak state |

The standard reader preserves page identity. The Mushaf path has a script initialization race and severe delay.

## Search Result Open Timings

- `2:255`: 331 ms to content, but opens at 2:1. Functional FAIL.
- `Ikhlas`: 287 ms to Al-Ikhlas.
- `Page 559`: 259 ms.
- `Page 540` in IndoPak: 245 ms.
- `Yaseen`: no result. `36` correctly returns Ya-Sin.
- `Juz 30`: returns offline Juz 30 ayah results.

## Bookmark And Last-Read Timings

- Ayah bookmark `2:255`: listed correctly and opens in 135 ms, but lands at 2:1.
- Page bookmark 540: listed correctly and opens in 138 ms at Page 540.
- Continue Reading persisted Page 540 after force-stop, but the Mushaf path took approximately 13.7 seconds.

## Suspected Bottlenecks

1. Mushaf script initialization race: logs show `Loading page 540 with scriptType INDOPAK`, then after 13,392 ms that composition is canceled when Uthmani state arrives.
2. Navigation/ViewModel settings initialization: standard reader DB and mapping are fast, but tap-to-DB-start ranges from 30 ms to 228 ms.
3. Main-thread work: logcat recorded several Choreographer events with 31-67 skipped frames for Amanah Quran process IDs.
4. Canonical anchor handling: search/bookmark routes carry `ayah=2:255`, but the reader composes at 2:1. This is not a database performance issue; it is navigation/scroll-state correctness.

## Build Gates

- `./gradlew assembleDebug`: PASS.
- `./gradlew test`: PASS, 5m44s.
- `./gradlew lintDebug`: PASS, 1m23s.
