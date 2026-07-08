# Font License Audit

## Active runtime fonts

The app currently loads these reader fonts at runtime:

- `apps/android/app/src/main/res/font/indopak_nastaleeq.ttf`
- `apps/android/app/src/main/res/font/digital_khatt_v2.otf`

Evidence in code:

- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/QuranFonts.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/mushaf/MushafPageScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/search/SearchScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/bookmarks/BookmarksScreen.kt`

## Active font status

| Font | Runtime use | Evidence status | Release status | Notes |
|---|---|---|---|---|
| `indopak_nastaleeq.ttf` | IndoPak display | CLEARED for internal testing | UNCLEAR / BLOCKED for public release | Legal evidence exists, but live Trust Center still says IndoPak public-release source is unresolved |
| `digital_khatt_v2.otf` | Uthmani display | CLEARED for internal testing | CLEARED for public release path in the legal docs | Still subject to the wider release gate, not just font clearance |

## Evidence and doc trail

- `docs/legal/CONTENT_AND_FONT_LICENSE_MANIFEST.md`
- `docs/legal/FINAL_LICENSE_CLEARANCE_DECISION.md`
- `docs/_release_gate/FONT_LICENSE_REVIEW.md`
- `docs/RUNTIME_FONT_AUDIT.md`
- `docs/SOURCE_FONT_PAIRING.md`
- `apps/android/app/src/main/assets/trust/trust_center_content.json`

## Unused or retired fonts

These are present in evidence/history, but are not the active runtime reader fonts:

- `digital_khatt_indopak.otf`
- `uthmanic_hafs_v22.ttf`
- `KFGQPCNastaleeq-Regular.ttf`
- `QPC_V2_Hafs.ttf`

## Tofu / coverage risk

The legal decision notes rare-code-point coverage warnings:

- `indopak_nastaleeq.ttf` does not cover `U+034F`.
- `digital_khatt_v2.otf` does not cover `U+06EA` and `U+06EB`.

That does not prove tofu in normal reader pages, but it does mean the font audit is not zero-risk.

## Bottom line

- Runtime font wiring is real and intentional.
- Active fonts are documented.
- Public-release font clearance is still not a simple green light because the live Trust Center asset is stricter than some of the legal summary docs.

