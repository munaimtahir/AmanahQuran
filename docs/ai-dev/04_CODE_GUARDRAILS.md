# 04 — Code Guardrails

## Dependency Guardrails

Do not add:

- Ad SDKs.
- Analytics SDKs.
- Authentication SDKs.
- Social SDKs.
- Push notification SDKs.
- Cloud sync SDKs.
- Crash reporting SDKs unless explicitly approved.
- Any dependency that collects user/device data.

Allowed with caution:

- Kotlin standard libraries.
- AndroidX.
- Jetpack Compose.
- Room.
- DataStore.
- Hilt, if selected.
- SQLite tooling.
- Testing libraries.

Every new dependency must be justified in the sprint report.

## Android Permission Guardrails

V1 should require no dangerous runtime permissions.

Do not add:

- Location.
- Contacts.
- Microphone.
- Camera.
- Storage permission.
- Notification permission.
- Advertising ID permission.

## Network Guardrails

- Core reader must not need internet.
- Search must be local.
- Bookmarks must be local.
- Last-read must be local.
- Trust Center must work offline.

If source URLs are displayed in Trust Center, open them externally only if user taps them. Do not initialize network clients for core V1.

## Quran Text Guardrails

- `displayText` is immutable verified Quran text.
- Do not normalize display text.
- Do not strip marks from display text.
- Do not generate IndoPak from Uthmani or Uthmani from IndoPak.
- Store each script separately.
- Store normalized text only in `SearchIndex`.

## Bookmark Guardrails

Bookmarks must use:

- `ayahKey`, e.g. `2:255`, for ayah bookmarks.
- `pageNumber` for page bookmarks.

Do not use visual text as bookmark identity.

## Last-Read Guardrails

Last-read must store canonical references:

- Surah number.
- Ayah number.
- Ayah key.
- Page number.
- Juz number.
- Script type.
- Optional scroll offset.

## Trust Center Guardrails

Trust Center must read from:

- Content manifest.
- Content source table.
- Validation table/report.

It must show:

- Source.
- License.
- Version.
- Checksum.
- Import date.
- Validation status.
- No-modification statement.
- Privacy pledge.

## UI Guardrails

Avoid:

- Ad-like blank spaces.
- Popups during reading.
- Excessive animations.
- Gamification.
- Streak pressure.
- Donation prompts.
- Social features.

## Future Web App Guardrails

- Do not implement web app in V1.
- Do not create shared runtime logic that complicates Android V1.
- Shared folder is for schemas and contracts only until future phase.
- Web app must later follow the same no-tracking and verified-content rules.
