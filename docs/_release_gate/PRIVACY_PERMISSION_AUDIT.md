# Privacy / Permission Audit

## Manifest

- `AndroidManifest.xml` declares no runtime permissions.
- `INTERNET` is not requested.
- No location, contacts, microphone, camera, storage, or notification permission is requested.

## Dependencies

The app dependencies are limited to:

- AndroidX Compose
- AndroidX Navigation
- AndroidX Lifecycle
- AndroidX Datastore
- AndroidX Room
- Kotlin coroutines
- Test libraries only

No ad SDK, analytics SDK, auth SDK, social SDK, cloud sync SDK, or push SDK was found in the build files.

## Verdict

GO

The app remains aligned with the V1 privacy model and offline-first constraints.
