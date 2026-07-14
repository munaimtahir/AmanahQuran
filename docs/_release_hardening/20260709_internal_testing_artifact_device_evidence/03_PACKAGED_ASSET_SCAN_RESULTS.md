# Packaged Asset Scan Results

## Public track

Command:

```bash
./gradlew scanPackagedContentAssets
```

Result:
- FAIL
- `profile = public`
- `artifact_label = PUBLIC RELEASE TRACK`
- `packaged_count = 5`
- `blocker_count = 5`
- `warning_count = 0`

Blockers:
- `apps/android/app/src/main/assets/database/quran.db`
- `apps/android/app/src/main/assets/trust/trust_center_content.json`
- `apps/android/app/src/main/res/font/digital_khatt_indopak.otf`
- `apps/android/app/src/main/res/font/digital_khatt_v2.otf`
- `apps/android/app/src/main/res/font/indopak_nastaleeq.ttf`

## Internal track

Command:

```bash
./gradlew assembleRelease -PamanahReleaseTrack=internal
```

Observed scan result inside the build:
- PASS
- `profile = internal`
- `artifact_label = INTERNAL TESTING ONLY - NOT PUBLIC RELEASE APPROVED`
- `packaged_count = 5`
- `blocker_count = 0`
- `warning_count = 5`

Interpretation:
- Internal packaging is now explicitly labeled.
- Public approval remains blocked.
- The scan is still doing its job for public release.
