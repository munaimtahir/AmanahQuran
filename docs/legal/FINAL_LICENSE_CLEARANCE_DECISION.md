# Amanah Quran - Final Font License Clearance Decision

Date: 2026-06-27

## Decision

Internal testing is allowed.

Public release is still blocked.

## Actual Bundled Font Files

The current Android app source tree bundles these font files in `apps/android/app/src/main/res/font`:

- `digital_khatt_indopak.otf`
- `indopak_nastaleeq.ttf`
- `digital_khatt_v2.otf`

The following files are not present in the Android font directory:

- `uthmanic_hafs_v22.ttf`
- `KFGQPCNastaleeq-Regular.ttf`
- `QPC_V2_Hafs.ttf`

## Active Font References In Code / Resources

Current Android references point to the approved active reader font files:

- `apps/android/app/build.gradle.kts` references `digital_khatt_indopak.otf`, `digital_khatt_v2.otf`, and `indopak_nastaleeq.ttf` for font validation inputs.
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/QuranFonts.kt` uses `R.font.digital_khatt_indopak`, `R.font.digital_khatt_v2`, and `R.font.indopak_nastaleeq`.
- `apps/android/app/src/main/assets/trust/trust_center_content.json` contains source metadata for the content set, but it does not add bundled references to any retired font asset.

## Clearance Outcome

### Cleared

- `digital_khatt_indopak.otf`
- `indopak_nastaleeq.ttf`
- `digital_khatt_v2.otf`

Reason:

- The SHA-256 for `digital_khatt_indopak.otf` matches `docs/legal/evidence/fonts/DigitalKhattIndoPak.otf`.
- The SHA-256 for `indopak_nastaleeq.ttf` matches `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/Indopak_Nastaleeq_Hanafi_With_Waqf_Lazmi.ttf`.
- The SHA-256 for `digital_khatt_v2.otf` matches `docs/legal/evidence/fonts/active_reader_font_licenses/fonts/DigitalKhattV2.otf`.
- The source page and license/terms evidence are recorded in the active reader font mapping and manifest.

### Removed / Not Bundled

- `uthmanic_hafs_v22.ttf`
- `KFGQPCNastaleeq-Regular.ttf`
- `QPC_V2_Hafs.ttf`

Reason:

- They are not bundled in `apps/android/app/src/main/res/font`.
- They are not referenced by the current Android code/resources.
- They are not active reader fonts.

## Remaining Blockers

- Font coverage helper warnings remain for rare code points:
  - `indopak_nastaleeq.ttf` does not cover `U+034F` in the source database.
  - `digital_khatt_v2.otf` does not cover `U+06EA` and `U+06EB` in the source database.
- Sampled visual QA did not show tofu on the reviewed pages, but the coverage warnings need follow-up before public release.
- Non-font release blockers still remain elsewhere in the project, including Quran source-to-database mapping and manual Quran review sign-off.

## Release Position

- Internal testing: allowed.
- Public release: still blocked.
