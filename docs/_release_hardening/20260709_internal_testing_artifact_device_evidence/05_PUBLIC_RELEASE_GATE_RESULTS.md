# Public Release Gate Results

Command:

```bash
./gradlew assembleRelease -PamanahReleaseTrack=public
```

Result:
- FAIL

Failure point:
- `:app:scanPackagedContentAssets`

Why it failed:
- `quran.db` is not public-release approved
- `trust_center_content.json` is not public-release approved
- packaged fonts are still not public-release approved under the current policy

Conclusion:
- This is the correct behavior for the current state.
- Public release remains NO-GO.
