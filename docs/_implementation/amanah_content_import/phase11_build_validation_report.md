# Phase 11 Build Validation Report

Commands run from `apps/android`:

- `./gradlew :app:testDebugUnitTest`: passed after schema/test fixes.
- `./gradlew test`: passed.
- `./gradlew :app:assembleDebug`: passed.
- `./gradlew :app:lintDebug`: passed.

Failures encountered and fixed:

- Robolectric could not see Android assets until unit tests were configured with Android resources.
- Robolectric needed SDK 34 for the asset-backed Room test.
- Room validation required imported `id` columns to match nullable SQLite primary-key metadata.
- Room validation required `search_index` foreign key/default-value metadata.

Remaining issues:

- Public release remains blocked by manual Quran text review, font/license decisions, real-device page navigation verification, and Trust Center wording review.
