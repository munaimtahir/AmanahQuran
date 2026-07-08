# Release Blockers

## Blocker 1: Release task path bug

- `apps/android/app/build.gradle.kts:124-130`
- `assembleRelease` fails because the content pipeline task resolves `apps/scripts/generate_content_pipeline.py`, which does not exist.
- The real script is at `scripts/generate_content_pipeline.py`.

## Blocker 2: Live Trust Center is still conservative

- `apps/android/app/src/main/assets/trust/trust_center_content.json:18-27`
- `apps/android/app/src/main/assets/trust/trust_center_content.json:69-77`
- `apps/android/app/src/main/assets/trust/trust_center_content.json:91-102`

Current asset status:

- IndoPak public-release source unresolved.
- Mushaf page layout not verified.
- Manual review pending.
- Public release blocked.

## Blocker 3: Manual reviewer / scholar sign-off is incomplete

- The live Trust Center asset says the required human evidence is still pending.
- The release-gate docs also keep manual sign-off as required.

## Blocker 4: Fresh physical-device regression package still required

Needed before public release:

- Search `2:255`
- Bookmark `2:255`
- Continue Reading
- Page 540 in both scripts
- Juz 30 boundary
- Trust Center
- Offline behavior

## Blocker 5: Release packaging readiness

- Play Store metadata is still not fully ready.
- Screenshots need a fresh evidence pack.
- Public-release wording still needs a final conservative pass.

## Non-blockers for internal testing

- Debug build passes.
- Unit tests pass.
- Lint passes.
- Content database is populated.
- No ads or tracking SDKs were found.
- No dangerous permissions were found.

