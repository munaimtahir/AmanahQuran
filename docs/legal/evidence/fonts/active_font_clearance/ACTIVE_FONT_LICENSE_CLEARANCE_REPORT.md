# Amanah Quran — Active Font License Clearance Report

Generated: 2026-06-27T01:45:02+05:00

## Purpose

This report focuses on active/bundled Android font files. It does not clear Quran text content and does not replace scholar/manual Quran review.

## Active/Bundled Font Files

| Role | File | Exists | Size | SHA-256 |
|---|---|---:|---:|---|
| Active IndoPak reader font | `apps/android/app/src/main/res/font/indopak_nastaleeq.ttf` | NO | MISSING | `MISSING` |
| Active/Bundled Uthmani font | `apps/android/app/src/main/res/font/uthmanic_hafs_v22.ttf` | NO | MISSING | `MISSING` |
| Bundled DigitalKhatt IndoPak fallback/reference | `apps/android/app/src/main/res/font/digital_khatt_indopak.otf` | YES | 494828 bytes | `59a5e78c530de236a365354d558b37706f37d782f7ee95c3c9b7fe9e0fad042a` |
| Source candidate KFGQPC Nastaleeq | `sourcedata/8/extracted/KFGQPCNastaleeq-Regular.ttf` | YES | 254720 bytes | `de174e33ae14cb581097940de298c8bb9cfa60f7a34d7d0ab0ba2bd5126f912c` |
| Source candidate Uthmanic Hafs | `sourcedata/8/extracted/UthmanicHafs_V22.ttf` | YES | 297700 bytes | `aa68bffce289b4c0ebac68e90502eb69e42356abcd1603cb2b8e99c2c723f145` |
| Source DigitalKhatt IndoPak | `sourcedata/8/extracted/DigitalKhattIndoPak.otf` | YES | 494828 bytes | `59a5e78c530de236a365354d558b37706f37d782f7ee95c3c9b7fe9e0fad042a` |
| Source DigitalKhatt V2 | `sourcedata/8/extracted/DigitalKhattV2.otf` | YES | 521832 bytes | `0935c48269a57c9808e52dfae47864c189396452901c689977156036a72dd217` |
| Evidence DigitalKhatt IndoPak font | `docs/legal/evidence/fonts/DigitalKhattIndoPak.otf` | YES | 494828 bytes | `59a5e78c530de236a365354d558b37706f37d782f7ee95c3c9b7fe9e0fad042a` |
| Evidence DigitalKhatt V2 font | `docs/legal/evidence/fonts/DigitalKhattV2.otf` | YES | 521832 bytes | `0935c48269a57c9808e52dfae47864c189396452901c689977156036a72dd217` |

## Checksum Comparisons

| Comparison | Result | Meaning |
|---|---|---|
| app indopak_nastaleeq.ttf vs sourcedata KFGQPCNastaleeq-Regular.ttf | MISSING | If different, do not assume they are the same licensed file. |
| app uthmanic_hafs_v22.ttf vs sourcedata UthmanicHafs_V22.ttf | MISSING | If match, sourceData copy is same file; still needs license evidence. |
| app digital_khatt_indopak.otf vs evidence DigitalKhattIndoPak.otf | MATCH | If match, OFL evidence likely applies to this file. |
| sourcedata DigitalKhattIndoPak.otf vs evidence DigitalKhattIndoPak.otf | MATCH | Confirms source/evidence identity. |

## Preliminary Clearance Status

| Font | Status | Action |
|---|---|---|
| `indopak_nastaleeq.ttf` | BLOCKING/PENDING | Active IndoPak font. Must locate exact official source and license allowing Android app bundling/distribution. |
| `uthmanic_hafs_v22.ttf` | BLOCKING/PENDING | Must locate exact official source and license allowing Android app bundling/distribution. |
| `digital_khatt_indopak.otf` | CLEARED ONLY IF USED | Evidence collected separately, but it does not clear the active Nastaleeq font. |
| `DigitalKhattV2.otf` | CANDIDATE ONLY | May be used as replacement only after license check and rendering QA. |

