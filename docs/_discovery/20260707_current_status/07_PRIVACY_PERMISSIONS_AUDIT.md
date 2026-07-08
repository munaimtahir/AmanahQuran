# Privacy and Permissions Audit

## Manifest

The Android manifest contains no `uses-permission` entries.

Relevant file:

- `apps/android/app/src/main/AndroidManifest.xml`

## Code search

I searched for the usual privacy/monetization SDK indicators and found no evidence of:

- Firebase analytics
- AdMob or Google Ads
- tracking SDKs
- auth/login SDKs
- billing/in-app purchase SDKs
- remote config
- crash reporting SDKs
- push notification setup
- network permissions in the manifest

## Current privacy posture

- Offline-first by design.
- No forced login.
- No ads.
- No analytics SDK.
- No tracking SDK.
- No unnecessary runtime permissions.
- No visible network dependency for core reader flows.

## Notes

- The app does use `enableEdgeToEdge`, but that is a UI/window behavior, not tracking.
- Trust Center links are informational and do not create a core network dependency.

## Status

- Privacy audit: good for internal testing.
- Public release: still blocked for non-privacy reasons.

