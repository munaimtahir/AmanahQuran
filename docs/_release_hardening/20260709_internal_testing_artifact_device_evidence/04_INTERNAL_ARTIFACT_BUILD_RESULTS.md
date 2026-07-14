# Internal Artifact Build Results

Command:

```bash
./gradlew assembleRelease -PamanahReleaseTrack=internal
```

Result:
- PASS

What ran successfully:
- `validateQuranFonts`
- `validateContentLicenses` with `profile=internal`
- `validateQuranDatabase`
- `scanPackagedContentAssets` with `profile=internal`
- `validateReleaseContent`
- `assembleRelease`

Artifact produced:
- `apps/android/app/build/outputs/apk/release/app-release.apk`

Internal marker:
- `apps/android/app/build/reports/amanah-release/release_track.txt`
- Marker content records:
  - `amanahReleaseTrack=internal`
  - `artifactLabel=INTERNAL TESTING ONLY - NOT PUBLIC RELEASE APPROVED`

Conclusion:
- The internal-testing artifact path is working end-to-end.
