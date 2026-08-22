# Deferred Items for Review

This is the single authoritative V2.2 deferment ledger.

Items are recorded here only after autonomous engineering work and automated
validation establish that the remaining action genuinely requires external
content approval, credentials, or an unavailable device.

## V22-AUDIO-001

- ID: V22-AUDIO-001
- Phase: 5 — Daily Ayah Audio Lite
- Feature: Authentic Daily Ayah recitation
- Reason: No approved reciter catalogue, audio source, or redistribution/license record exists in this checkout.
- Why automation cannot complete it: Selecting a reciter and approving religious audio redistribution is a content/legal decision.
- Work already completed: Source-neutral `Reciter`, `AudioSource`, `AudioAyah`, `AudioRepository`, `PlaybackController`, and an explicit no-audio implementation. Text/widget functionality does not depend on audio.
- Remaining action: Supply an approved source, licence/permission evidence, URI/cache policy, and mapping manifest; then implement Media3 playback and offline cache against that source.
- Exact reviewer input required: Approved reciter/source identity, licence/permission evidence, and whether Daily Ayah audio may be downloaded or must be bundled.
- Release blocking: YES for audio activation; NO for the rest of the V2.2 implementation.
- Recommended decision/options: Keep audio disabled for this RC, or approve one specific source with a deterministic `ayah_key` mapping.
- Evidence/files: `apps/android/app/src/main/kotlin/org/amanahquran/app/core/audio/AudioContracts.kt`, `V2_2_AUDIO_VALIDATION_REPORT.md`.

## V22-CURATED-001

- ID: V22-CURATED-001
- Phase: 3 — Daily Ayah Engine
- Feature: Reviewed curated Daily Ayah pool
- Reason: No reviewed eligibility dataset is present; the engine safely uses a deterministic sequential walk until one exists.
- Why automation cannot complete it: Context-sensitive religious selection requires scholarly/content approval.
- Work already completed: Persisted daily state, 30-entry history, canonical `ayah_key` retrieval, deterministic sequence, reviewed-random API, and anti-repeat logic.
- Remaining action: Provide an approved eligibility file with `ayah_key`, category, context sensitivity, and review status.
- Exact reviewer input required: Approve the dataset and its default mode (`CURATED`).
- Release blocking: NO; sequential mode is safe and functional.
- Recommended decision/options: Approve a reviewed pool, or explicitly retain sequential mode for this release.
- Evidence/files: `apps/android/app/src/main/kotlin/org/amanahquran/app/core/daily/`, `V2_2_DAILY_AYAH_VALIDATION_REPORT.md`.

## V22-SIGNING-001

- ID: V22-SIGNING-001
- Phase: 27 — Release artifacts
- Feature: Signed release APK/AAB
- Reason: No `apps/android/keystore.properties`, JKS, or `AMANAH_RELEASE_*` environment values are available.
- Why automation cannot complete it: Signing credentials are secret external inputs.
- Work already completed: Debug APK build passed; unsigned release compilation was taken through content-pipeline execution and identified its missing ignored source workspace.
- Remaining action: Provide signing credentials and the repository's expected release track decision.
- Exact reviewer input required: Secure keystore properties/environment values.
- Release blocking: YES for signed production artifacts; NO for debug engineering validation.
- Recommended decision/options: Run the final release build in the credentialed release environment.
- Evidence/files: `apps/android/app/build.gradle.kts`, failed `assembleRelease` log.

## V22-DEVICE-001

- ID: V22-DEVICE-001
- Phase: 18 — Physical device review
- Feature: OEM and physical-device validation
- Reason: No ADB devices and no emulator AVDs are available in this environment.
- Why automation cannot complete it: Arabic shaping, OEM widget refresh, battery policy, and background media behavior require an actual device/AVD.
- Work already completed: Build/unit/lint gates passed; widget/provider/deep-link code compiles; a device test plan is included.
- Remaining action: Execute the physical-device matrix and attach screenshots/logs.
- Exact reviewer input required: Access to representative Samsung, Xiaomi/Redmi, Oppo/Vivo, and Infinix/Tecno devices or equivalent AVDs.
- Release blocking: YES for the physical-device gate; NO for autonomous engineering completion.
- Recommended decision/options: Perform the matrix before public promotion; keep the RC conditional meanwhile.
- Evidence/files: `V2_2_PHYSICAL_DEVICE_TEST_PLAN.md`, empty `adb devices` output.
