# Trust Center Audit

## What is implemented

- The Trust Center screen is real and data-backed.
- It reads a local JSON asset at `apps/android/app/src/main/assets/trust/trust_center_content.json`.
- It combines that asset with local DB counts from `content_sources` and `content_validation`.
- It shows source references, privacy pledge, validation status, and release information.

## What the live asset says

Key points from `trust_center_content.json`:

- Uthmani source: validated `GO`.
- IndoPak source: `REVIEW_REQUIRED`.
- Search normalization source: `GO`.
- Mushaf page layout: `Internal prototype only — not for release`.
- Validation status: `NOT VERIFIED`.
- Manual review status: `PENDING REVIEW`.
- Release approval status: `BLOCKED`.
- Public release status: `BLOCKED`.
- IndoPak public-release source status: `UNRESOLVED`.

## What the repository code does

- `TrustCenterRepository` derives a conservative release state.
- It refuses to mark public release as allowed unless checksum, validation, manual review, and source rows all meet strict checks.
- `TrustCenterViewModel` loads the data locally and exposes it to the UI.

Relevant code:

- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/repository/TrustCenterRepository.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterViewModel.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt`

## Trust Center accuracy risk

The live asset is conservative enough for internal testing, but it is not yet a clean public-release declaration.

Things that are still not safe to claim publicly:

- Verified release approval.
- Completed manual reviewer/scholar sign-off.
- Final IndoPak public-release source resolution.
- Final mushaf layout verification.

## Current classification

- Internal testing: allowed.
- Public release: blocked.

