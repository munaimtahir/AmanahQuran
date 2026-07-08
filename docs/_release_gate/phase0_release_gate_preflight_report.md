# Phase 0 - Release Gate Preflight Report

## Scope

Preflight only. No Quran source files were modified and no packaged DB contents were changed.

## Current Project Status

- Android app project exists at `apps/android`.
- Debug APK artifact exists at `apps/android/app/build/outputs/apk/debug/app-debug.apk`.
- Real-device ADB validation has already been completed on TECNO CH6i, Android 13, SDK 33, offline/airplane mode.
- Public release is still blocked by manual review and wording/license gates.

## Release-Gate Inputs

| Input | Status | Evidence |
|---|---|---|
| App builds from available source | GO | Android project and prior debug artifacts present; build/test revalidated in this sprint |
| ADB validation report exists | GO | `docs/_implementation/amanah_adb_device_validation/` artifacts present |
| Public release blockers file exists | GO | `docs/content/PUBLIC_RELEASE_BLOCKERS.md` |
| Trust Center content JSON exists | GO | `apps/android/app/src/main/assets/trust/trust_center_content.json` |
| Trust Center UI exists | GO | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt` |
| Packaged Quran DB exists | GO | `apps/android/app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite` |
| Source inventory/checksum metadata exists | GO | `content_sources` and `content_validation` tables in packaged DB |
| Reviewer package can be regenerated | GO | Created in `docs/_release_gate/manual_quran_review/` |
| No public release claim exists | GO | Docs remain explicitly internal/pending |

## Verdict

GO

This is a preflight GO only. It does not mean public release is approved.
