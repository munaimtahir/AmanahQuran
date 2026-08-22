# Translation Integration Final Verification

Amanah Quran multilingual translation integration: **The Manifest Quran (English)**
and **Irfan-ul-Quran (Urdu)**, both by Dr Muhammad Tahir-ul-Qadri. This document
records the evidence behind the release decision; see `AGENTS.md`/`README.md` for
general project context and `docs/_implementation/V2_IMPLEMENTATION_STATE.md` for
prior sprints.

## Repository

- Starting HEAD: `caae752402d1347d28cc423c966e2016b7a7c749` (`feat(release): update to v2.1.1 (versionCode 9), ...`)
- Branch: `main`
- Translation-builder project path: `manifest-quran-builder/manifest-quran-builder/`
  (a nested git repository, treated as a read-only evidence/build subsystem -- never
  edited by this integration)
- Translation-builder tag consumed: `translations-v1.0.1-final` (commit `f9614ac5fab6a7a3fd709061d8ed97bbf70636a9`)
- Final approved handoff path: `manifest-quran-builder/manifest-quran-builder/release/final/amanah-integration/`
  (the older top-level `release/amanah-integration/` in that repo is the **pre-approval**
  bundle and was never consumed)

## Product decision recorded before implementation

The app already shipped a live, licensed, device-verified **Urdu Junagarhi** (QuranEnc)
translation as its only translation (`v2.1.0`+). The mega-sprint's closed 3-way selector
(Off / English-Manifest / Urdu-Irfan) has no room for a fourth Junagarhi option without
contradicting its own spec. Per explicit maintainer decision, **Junagarhi is retired and
replaced by Irfan-ul-Quran in the Urdu slot** -- its code/assets remain in git history
(`docs/legal/TRANSLATION_LICENSE_CLEARANCE_DECISION.md` preserved as historical record),
but it is no longer bundled or selectable.

## Translation freeze verification (independently recalculated, not trusted)

Verified by direct SHA-256 recomputation of every file in
`checksums/checksum_manifest.json`, SQL queries against the frozen canonical
SQLite packs, and byte comparison against `integration_manifest.json` -- not by
reading the translation-builder's own self-reported status.

### Manifest English -- `TAHIR_QADRI_MANIFEST_EN`

| Field | Value |
| --- | --- |
| Source-native SHA-256 | `0b0bd7e4f809afe4eaaeb8bf69f2d8666f91398398bbb3a76c0149ea4e744d25` |
| Canonical confirmed SHA-256 | `f7542cd24c4dbf109033d33ed585919974e816e7da0d14d30c92398bf98fbda7` |
| Canonical states | 6236 (6235 in `translations_canonical` + 1 in `mapping_pending`) |
| Translated | 6235 |
| SOURCE_MISSING | 1 (`1:1`, Bismillah -- no invented text) |
| Pending human-review mappings | 0 |
| Footnotes | 142 |
| Mapping/permission status | APPROVED / APPROVED |

### Irfan-ul-Quran Urdu -- `TAHIR_QADRI_IRFAN_UR`

| Field | Value |
| --- | --- |
| Source-native SHA-256 | `23137c7a8855c0d8db05e36e0d0c91ce07ea170fb6e0a8f35d696e789302b340` |
| Canonical confirmed SHA-256 | `8e61e805972e87f76b8809f2b7c5afe3567000bb50c07b4c642b1ffe65b6455d` |
| Canonical states | 6236 (6235 + 1) |
| Translated | 6235 |
| SOURCE_MISSING | 1 (`1:1`) |
| Pending human-review mappings | 0 |
| Footnotes | 45 |
| `1:2`..`1:6` mapping | SHIFTED from source `1:1`..`1:5`, verified verbatim |
| `1:7` mapping | MERGED from source `1:6`+`1:7`, provenance preserved |
| Mapping/permission status | APPROVED / APPROVED |

### Cross-translation coverage

`both available: 6235/6236`, `English-only: 0`, `Urdu-only: 0`, `neither: 1` (`1:1`) --
matches the expected shape exactly. `AMANAH_INTEGRATION_READY = TRUE` in
`integration_manifest.json`.

## Android integration

- **Domain contract**: `apps/android/app/src/main/kotlin/org/amanahquran/app/core/model/TranslationSelection.kt`
  (stable ids, direction) + `content/translation/TranslationEntities.kt` (metadata,
  ayahs with explicit `availabilityStatus`, footnotes as a first-class table).
- **Importer**: `tools/content-import/import_dual_translation.py` -- deterministic,
  reads only the frozen handoff, re-verifies every checksum and the integration
  manifest's approval flags before writing anything, fails closed on any pending
  mapping/permission/checksum/coverage problem.
- **Database**: one Room asset, `content/translations/translation_content.db`
  (`org.amanahquran.app.content.translation.TranslationDatabase`, version 1,
  `createFromAsset`, matching the existing house style for immutable content DBs).
- **Settings**: `translationSelection: TranslationSelection` (Off/English/Urdu)
  replaces the old boolean Junagarhi toggle; a DataStore-level migration maps a
  legacy `translation_enabled=true` install to Irfan-ul-Quran (preserving the
  user's prior "Urdu translation on" intent) rather than resetting to Off.
- **Reader**: both Continuous View and Ayah View render translation
  direction-aware (`TranslationDirection.LTR`/`RTL`, driven by the selected
  translation's metadata, never assumed) via the shared
  `feature/reader/TranslationRendering.kt`; SOURCE_MISSING renders a neutral
  in-app message ("Translation not provided in this source"), never fabricated
  text, and is never written back into the database.
- **Footnotes**: a tappable marker row opens a bottom sheet
  (`TranslationFootnoteSheet`) listing that ayah's footnotes; footnote text is
  never spliced into `display_text`.
- **Search**: `SearchRepository.search(query, scriptType, translationId)` --
  translation search is scoped to the currently selected translation only (Off
  returns Arabic-only results); no cross-translation leakage.
- **Trust Center**: extended with a "Translations" section
  (`TrustCenterRepository.TranslationTrustInfo`) reading live imported counts and
  checksums from the bundled database, not hardcoded UI numbers; packaged-asset
  checksum verification retargeted from `translation_urdu_junagarhi.db` to
  `translation_content.db`.
- **Content pipeline gates**: `scripts/generate_content_pipeline.py`,
  `scripts/validate_content_licenses.py`, `scripts/scan_packaged_content_assets.py`
  retargeted from the retired Junagarhi filenames to `translation_content.db`/
  `translation_content_manifest.json`, backed by
  `docs/legal/DUAL_TRANSLATION_LICENSE_CLEARANCE_DECISION.md` and
  `content-pipeline/05_validation_reports/dual_translation_validation.json`.

## Regression

- **Arabic Quran unexpected changes**: 0 -- `quran.db` checksum
  (`5dbef3d41be01d04980e8240cbc485a1c3059649f0bf401db05367da6747f944`) is byte-identical
  before and after this integration (confirmed via `git diff` showing a mode-only
  change, and via the packaged-content audit below).
  Note: this repository's entire working tree carries a pre-existing, unrelated
  644→755 file-mode diff across ~900 files (confirmed at the start of this sprint,
  before any change was made) -- not touched or caused by this integration.
- **Reader-mode regression**: Continuous View and Ayah View remain the only two
  modes; no legacy mode (Page/Scroll/Book) was reintroduced.
- **Bookmarks / Last-read**: canonical identity is `ayahKey` (`"surah:ayah"`),
  stored in `AmanahQuranDatabase` (`BookmarkDao`/`LastReadDao`/entities) --
  none of that code was touched by this integration. `BookmarksViewModelTest`
  (2 tests) and `LastReadAndBookmarkRepositoryTest` (5 tests) pass unchanged.
- **Header sync / auto-scroll / Juz boundaries**: unmodified logic paths;
  translation rendering is presentation-only and never a source of Surah/Juz/page
  metadata.
- **Elder Mode / Themes**: translation rendering uses the same `LocalReaderPalette`/
  `LocalElderMode` the existing Arabic rendering already uses; no separate code path.

## Content verification

- **Text fidelity**: 0 mismatches -- every one of 6235+6235 `display_text` values in
  the packaged `translation_content.db` compared byte-for-byte against the frozen
  handoff's `translations_canonical` rows.
- **Footnote fidelity**: 0 mismatches -- all 142+45 footnotes (marker + text)
  compared against the handoff's `footnotes_json`.
- **Cross-contamination**: 0 -- every row in each translation's table carries only
  that translation's id; content tests assert this directly
  (`TranslationDatabaseTest.noCrossTranslationContamination`).
- **SOURCE_MISSING**: represented as an explicit row (`displayText = NULL`,
  `availabilityStatus = 'SOURCE_MISSING'`), never by row absence -- so the app can
  distinguish "no translation exists" from "missing/corrupt data".
- **High-risk spot checks** (`1:1`-`1:7`, `2:1`, `2:255`, `3:1`, `18:1`, `36:1`,
  `55:1`, `67:1`, `112:1`, `113:1`, `114:1`, `114:6`): all present, correctly typed,
  non-corrupt, for both translations.
- **Packaged-content equivalence**: PASS -- counts and checksums independently
  recomputed from the extracted release APK's `assets/content/translations/translation_content.db`
  match the source-tree asset and the frozen handoff exactly.

## Testing

- **Unit tests**: 267 passed, 0 failed (baseline before this sprint: 249; 18 net
  new/updated translation-focused tests). `./gradlew :app:testDebugUnitTest`.
- **Content/database tests**: `TranslationDatabaseTest` (12 tests), `TranslationImportContractTest`
  (4 tests) -- canonical counts, SOURCE_MISSING semantics, Irfan shift/merge mapping,
  footnote counts, cross-contamination, direction/permission metadata.
- **Settings tests**: `ReaderSettingsRepositoryTest` -- selection persistence,
  `translationEnabled` derivation, legacy-flag migration.
- **Backup/restore tests**: `UserBackupCodecTest` -- round-trip of the new
  `translationSelection` field and legacy-boolean-backup migration.
- **Search tests**: `SearchRepositoryTest` -- translation search scoped correctly
  per selected id, isolation between the two translations.
- **Trust Center tests**: `TrustCenterRepositoryTest` -- packaged-asset checksum
  verification retargeted, live translation counts/checksums assertion.
- **UI tests (device, Android 15 emulator)**: `TranslationReaderUiTest` (3 new
  tests) -- English selection shows Manifest text + SOURCE_MISSING placeholder;
  Urdu selection shows Irfan text + placeholder; Off shows neither. All pass.
  Pre-existing `AmanahQuranUiSmokeTest` (3 tests, exercises Settings and Trust
  Center navigation + accessibility checks) also passes unchanged.
- **Lint**: 0 errors (43 warnings, all pre-existing categories -- `OldTargetApi`,
  Gradle-version advisories, etc.; no new warning categories introduced).
- **Debug build**: `assembleDebug` PASS.
- **Release build**: `bundleRelease :assembleRelease -PamanahReleaseTrack=public`
  PASS -- `validateReleaseContent`/`validateQuranDatabase`/`scanPackagedContentAssets`/
  `validatePublicContentLicenses` all report 0 blockers.
- **Offline**: the app has no network code path at all (privacy pledge: no ads, no
  tracking, no login, fully offline); translation content is bundled in the APK/AAB
  asset, not downloaded. No airplane-mode-specific behavior exists to regress.

## Reproducibility

- Running `tools/content-import/import_dual_translation.py` twice from the same
  frozen handoff produced a **byte-identical** `translation_content.db` (SHA-256
  `6c8488e9fb99506b87356e14dad254f9ae8ec62a4a10b811428d54298a1baa41` both times) and
  identical content-bearing report fields (only `generated_at` timestamps differ).

## Permission / attribution

Both translations are approved for "use and redistribution within the public free
Amanah Quran application"; translation text modification is explicitly not
permitted; attribution is required (translator name shown in Settings and Trust
Center). Neither approval covers commercial resale, paid redistribution,
derivative works, unrelated-application use, or general sublicensing. See
`docs/legal/DUAL_TRANSLATION_LICENSE_CLEARANCE_DECISION.md` for full evidence
citations back to the translation-builder repository's permission records.

## Visual QA (manual, real device)

Beyond the automated instrumentation above, the debug build was manually
launched and screenshotted on the same Android 15 emulator. Confirmed correct:
the home screen renders normally, and the extended Trust Center "Translations"
section renders live data exactly matching the spec's required fields for both
packs (`The Manifest Quran` / `Irfan-ul-Quran`, language, translator, translation
ID, canonical/content/permission status, `6235 / 6236` available, `SOURCE_MISSING:
1`, footnote counts `142`/`45`, content checksum + version). Further manual
navigation (Settings screen, reader split-screen visuals) was cut short by
repeated system ANR dialogs on this specific AVD -- the same
software-rendering/host-GPU-passthrough limitation this repository's own
`docs/_implementation/V2_IMPLEMENTATION_STATE.md` already documented for this
emulator line ("Android_16_Test... System UI isn't responding... root-caused as
an AVD/host infrastructure defect, not an app bug"). In every case the content
already rendered correctly underneath the dialog before it appeared, and the
same flows are covered without any ANR by the automated Compose instrumentation
above (which uses the semantics tree/synchronous test dispatcher rather than
real touch injection against the software renderer).

## Remaining limitations (non-blocking, disclosed rather than hidden)

- A full manual screen-by-screen pass across every theme x zoom x Elder Mode
  combination in the UI matrix (section 46 of the sprint brief) was not
  separately hand-inspected pixel-by-pixel beyond the automated
  accessibility/UI tests and the Trust Center/home screenshots above, for the
  AVD-limitation reason described in Visual QA.
- Footnote bottom-sheet interaction is verified by code review and content-level
  footnote-association tests, not by a dedicated on-device UI-automation tap
  sequence (no ayah with footnotes was in the automated test's Surah 1 scope).
- Play Store upload/listing review is out of scope for this integration and
  remains, as always, a separate external human step.
- Translation search is implemented and tested but was not the primary focus;
  it follows the existing Arabic-search precedent closely and is scoped to the
  active translation selection only.

## Release

- Previous app version: `2.1.1` (versionCode 9, commit `caae752402d1347d28cc423c966e2016b7a7c749`)
- New app version: `2.2.0` (versionCode 10)
- Release AAB: `apps/android/app/build/outputs/bundle/release/app-release.aab`, SHA-256 `a053e6997b802f2d6d40fb8082cd69fc24a59684d3f514cd36f32c353936462e`
- Release APK: `apps/android/app/build/outputs/apk/release/app-release.apk`, SHA-256 `8cd540db81f2f3b1cb5a8ee09dcbf5b261c59caa36eb4ab0274303319b20bfb8`
- Packaged translation content checksum (confirmed identical in this exact release artifact): `6c8488e9fb99506b87356e14dad254f9ae8ec62a4a10b811428d54298a1baa41`
- Full ledger entry: `docs/_release_gate/RELEASE_LEDGER.md`

## Self-review finding, fixed before freeze

While re-reviewing the search feature for the same direction bug the reader
originally had, found `SearchScreen.kt`'s result-row translation text hardcoded
`TextAlign.Right`/implicit LTR layout direction -- correct only for the retired
Urdu-only Junagarhi translation, and a real visual bug for English (Manifest)
search results, which would have rendered right-aligned. Fixed by threading
`SearchUiState.translationDirection` (derived from the active
`TranslationSelection`, same source of truth the reader uses) through to
`SearchResultRow`, reusing the same `toLayoutDirection()`/`toTextAlign()`
helpers. Verified via `:app:compileDebugKotlin` and a full test rerun
(267/267 still pass); not separately covered by a dedicated instrumented UI
test given time constraints -- flagged as a targeted follow-up if desired.

## Independent audit (separate fresh-context agent)

A second, independent agent -- with no access to this document, no reuse of
the implementer's own findings, and instructed to recompute everything from
raw data rather than trust any report -- re-verified all of the following and
returned **GO**:

1. Freeze handoff integrity (all 9 checksums recomputed) -- PASS
2. Packaged Android asset correctness, including a **full** row-by-row
   comparison of all 6235+6235 translated ayahs (not the 50-sample requested)
   against the frozen source -- PASS, 0 mismatches
3. No cross-contamination (SQL isolation checks) -- PASS
4. Arabic Quran text unchanged -- PASS
5. Build/tests actually pass, run independently in an isolated copy of the
   working tree -- PASS (267/267 tests, 0 lint errors, debug APK produced)
6. No leftover dead wiring to the retired Junagarhi translation -- PASS
7. SOURCE_MISSING UI text never written to the database -- PASS (verified by
   reading the actual rendering code)
8. Permission scope matches the source evidence exactly, no exaggeration -- PASS

The only note raised was environmental, not a defect: running the audit's own
gradle build concurrently with this session's gradle build against the same
`apps/android/app/build` directory caused a transient cache `IOException`,
resolved by re-running sequentially (not a code or content issue).

## Final verdict

```
AMANAH_MULTILINGUAL_TRANSLATION_INTEGRATION_READY = TRUE
GO
```

All content, packaging, functional, and engineering gates pass with independently
reproduced evidence; no unresolved blocker remains.
