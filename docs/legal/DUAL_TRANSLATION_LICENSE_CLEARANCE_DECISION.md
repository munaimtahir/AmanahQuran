# Amanah Quran - Dual Translation License Clearance Decision

Date: 2026-08-22

## Source

This decision covers the two translations bundled as of the Manifest
English + Irfan-ul-Quran Urdu integration sprint, superseding Urdu
Junagarhi (see `docs/legal/TRANSLATION_LICENSE_CLEARANCE_DECISION.md`,
preserved unchanged as historical record; Junagarhi is no longer bundled
in the app as of this decision).

### The Manifest Quran (English) — `TAHIR_QADRI_MANIFEST_EN`

- **Translator**: Dr Muhammad Tahir-ul-Qadri
- **Source domain**: manifestquran.com
- **Source-native content SHA-256**: `0b0bd7e4f809afe4eaaeb8bf69f2d8666f91398398bbb3a76c0149ea4e744d25`
- **Canonical confirmed content SHA-256**: `f7542cd24c4dbf109033d33ed585919974e816e7da0d14d30c92398bf98fbda7`
- **Permission scope**: use and redistribution within the public free Amanah Quran
  application; translation text modification not permitted; attribution required.
- **Evidence**: `manifest-quran-builder/manifest-quran-builder/permissions/manifest_en/PERMISSION_STATUS.json`
  (`status: APPROVED`), carried forward unchanged from the translation-builder
  repository's `translations-v1.0.0-final` tag through `translations-v1.0.1-final`.

### Irfan-ul-Quran (Urdu) — `TAHIR_QADRI_IRFAN_UR`

- **Translator**: Dr Muhammad Tahir-ul-Qadri
- **Source domain**: irfan-ul-quran.com
- **Source-native content SHA-256**: `23137c7a8855c0d8db05e36e0d0c91ce07ea170fb6e0a8f35d696e789302b340`
- **Canonical confirmed content SHA-256**: `8e61e805972e87f76b8809f2b7c5afe3567000bb50c07b4c642b1ffe65b6455d`
- **Permission scope**: use and redistribution within the public free Amanah Quran
  application; translation text modification not permitted; attribution required.
- **Evidence**: `manifest-quran-builder/manifest-quran-builder/permissions/irfan_ur/PERMISSION_STATUS.json`
  (`status: APPROVED`, `status_history`: `PENDING_REVIEW` → `APPROVED` recorded during the
  translation-builder repository's Final Gap-Closure Sprint, tag `translations-v1.0.1-final`).

Both permission records are independent of each other (see each file's
`independent_of` field) despite sharing the same translator/rights holder, and
neither approval covers commercial resale, paid redistribution, derivative
translation works, use in unrelated applications, or general sublicensing.

## Integration bundle verification

The Android import (`tools/content-import/import_dual_translation.py`) consumes
only `manifest-quran-builder/manifest-quran-builder/release/final/amanah-integration/`
(tag `translations-v1.0.1-final`, commit `3336d41ef77a0ea31633120dbd487b37635ff954`),
verifies every file's SHA-256 against `checksums/checksum_manifest.json`, and
verifies `integration_manifest.json` records `AMANAH_INTEGRATION_READY: true`
with both translations' mapping and permission status `APPROVED` before writing
the packaged Android asset. It fails closed (refuses to build the asset) on any
checksum mismatch, pending mapping, or non-approved permission status.

Full detail: `manifest-quran-builder/manifest-quran-builder/FINAL_CONTENT_FREEZE.md`
and `manifest-quran-builder/manifest-quran-builder/reports/independent_review/FINAL_DUAL_TRANSLATION_AUDIT.md`.

## Decision

**TRANSLATION LICENSE CLEARANCE: APPROVED FOR PUBLIC DISTRIBUTION**

Both The Manifest Quran (English) and Irfan-ul-Quran (Urdu) are cleared for
public bundling and distribution in the Amanah Quran Android app, within the
free-app-use scope recorded above.

- **Authorized by**: Munaim Tahir, project maintainer
- **Date**: 2026-08-22
- **Basis**: the translation-builder repository's own independently-audited,
  checksum-verified, permission-approved freeze (`translations-v1.0.1-final`),
  re-verified independently by this repository's importer and content tests
  rather than trusted as a self-report.

## Release Position

- Internal testing: allowed.
- Public release: approved, contingent on the remaining engineering/content
  gates recorded in `TRANSLATION_INTEGRATION_FINAL_VERIFICATION.md`.
