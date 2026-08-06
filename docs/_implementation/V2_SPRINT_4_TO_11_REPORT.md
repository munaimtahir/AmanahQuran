# Amanah Quran V2 — Sprints 4–11 Implementation Report

Updated: 2026-08-02

## Scope decision

V2 remains Android-first, offline-capable, source-attributed, and privacy-first.
Audio playback and audio downloads remain explicitly parked for V3 because no
approved reciter catalogue and licence were selected. No ads, tracking,
accounts, cloud sync, network-dependent reader behavior, or Quran display-text
conversion were added.

## Sprint results

| Sprint | Result | Completed work and evidence |
| --- | --- | --- |
| 4 — translation reader/search | PASS | Bundled Urdu Junagarhi Room pack; persistent reader translation toggle and font size; RTL Urdu rendering; offline Urdu search returning canonical ayah keys and verified Arabic display previews; translation and search/display-separation tests. |
| 5 — audio engine | PARKED V3 | No audio feature added because an approved source catalogue is not available. |
| 6 — audio downloads | PARKED V3 | No download/network feature added. |
| 7 — collections/backup | PASS | Local bookmark collections, filtering, versioned backup codec, SAF export/import, validation preview, and restore application; round-trip and repository tests. |
| 8 — sharing/reporting | PASS | Local text share, generated ayah PNG share using the selected verified script font, and editable mailto issue reporting with rendering/translation/app-bug/other categories. |
| 9 — Trust Center/privacy/accessibility | PASS (engineering) | Offline source and licence attribution, optional translation metadata, on-device Quran/translation SHA-256 verification, privacy pledge, conservative release status, Elder Mode and large-target settings. Verification success/failure tests added. |
| 10 — device/performance | PASS (available device) | Debug APK installed on TECNO CH6i Android 13; launch, Search, reader script switching, Settings Urdu-toggle persistence, Trust Center and on-device checksum verification exercised without app crash. |
| 11 — signed release | PASS (build gate) | V2 version metadata set to `2.0.0` / versionCode `6`; internal and public release-track assemblies pass with content/license validation, R8, resource shrinking, signing configuration, and native-symbol ZIP generation. |

### Continue Reading regression fix

Continue Reading now opens the full containing surah (or containing page in Book
Mode) and scrolls to the saved `surah:ayah` anchor. Previously, the exact-ayah
route loaded a one-ayah list, which made the reader appear truncated. A
ViewModel regression test covers the full-surah load and target-scroll behavior.

The broader reader-experience preparation pass is documented in
[`V2_READER_EXPERIENCE_PREPARATION.md`](V2_READER_EXPERIENCE_PREPARATION.md).

## Verification commands

- `./gradlew :app:testDebugUnitTest --no-daemon` — PASS.
- `./gradlew :app:assembleDebug :app:lintDebug --no-daemon` — PASS.
- `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:lintDebug --no-daemon` — PASS.
- `./gradlew :app:assembleRelease -PamanahReleaseTrack=internal --no-daemon` — PASS.
- `./gradlew :app:assembleRelease -PamanahReleaseTrack=public --no-daemon` — PASS.

## Device evidence

Attached device: TECNO CH6i, Android 13, ADB serial `08357252AE006901`.

The post-fix release rebuild passed all local gates, but the ADB device
disconnected before the refreshed APK could be reinstalled. A fresh post-fix
device smoke should be rerun when the device reconnects.

- APK installed successfully with `adb install -r`.
- When replacing the debug APK with the release APK, Android correctly rejected
  the update because the debug and release signing certificates differ. A clean
  `adb uninstall org.amanahquran.app` followed by `adb install` was required;
  this removes only local app data on the test device and is not a runtime crash.
- The clean-installed release APK reports version `2.0.0` / versionCode `6` and
  launches successfully.
- Main activity launched successfully.
- Release Search resolved `Yaseen` to `Ya-Sin`, Surah 36.
- Reader exposed IndoPak and Uthmani controls; switching completed without a crash.
- Settings showed and persisted `Show Urdu translation`.
- Release Trust Center recomputed and verified both `quran.db` and
  `translation_urdu_junagarhi.db` against their recorded checksums.
- Recent app log scan showed no `FATAL EXCEPTION` for the exercised flows.

## Known release follow-up

- Play Console upload and post-upload review remain external actions.
- A human reviewer must re-approve the exact V2 artifact/content bundle if the
  public release process requires a new approval record rather than relying on
  the historical V1.0.5 approval.
- Full TalkBack and long-duration low-end performance evidence still requires a
  dedicated manual QA session; no unsupported claim is made here.

## Scope guardrail confirmation

V2 work stayed within the approved next-version extension around translation,
local user-state backup, sharing/reporting, trust, accessibility, and release
hardening. Quran display text remains sourced from verified stored fields;
normalized search text remains separate; core reader behavior remains offline;
no prohibited monetization, tracking, login, cloud, or network reader feature
was introduced.
