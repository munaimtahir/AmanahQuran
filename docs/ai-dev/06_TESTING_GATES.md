# 06 — Testing Gates

## Purpose

No sprint should be considered complete without relevant tests or a justified no-test explanation.

## Required Test Categories

## 1. Unit Tests

Use for:

- Search parser.
- Arabic normalization.
- Theme settings.
- Script settings.
- Bookmark logic.
- Last-read logic.
- Trust Center manifest parsing.

## 2. Room DAO Tests

Use for:

- Surah DAO.
- Ayah DAO.
- QuranText DAO.
- SearchIndex DAO.
- Bookmark DAO.
- LastRead DAO.
- ContentSource DAO.
- ContentValidation DAO.

## 3. Data Validation Tests

Must verify:

- 114 Surahs.
- 6236 ayahs.
- No missing ayah keys.
- No duplicate ayah keys.
- IndoPak text exists.
- Uthmani text exists.
- Search index count matches ayah count.
- Content sources are complete.
- Checksums are present.

## 4. Search Tests

Must verify:

- Surah name search.
- Surah number search.
- Ayah reference search, e.g. `2:255`.
- Juz search.
- Page search.
- Arabic normalized search.
- Result preview uses display Quran text, not normalized text.

## 5. Bookmark Tests

Must verify:

- Add ayah bookmark.
- Remove ayah bookmark.
- Add page bookmark.
- Remove page bookmark.
- Bookmarks persist.
- Script switch does not break bookmarks.

## 6. Last-Read Tests

Must verify:

- Save last-read.
- Restore last-read.
- Last-read survives script switch.
- Last-read works offline.

## 7. Settings/DataStore Tests

Must verify:

- Theme persists.
- Script selection persists.
- Elder Mode persists.
- Font size persists if implemented.

## 8. UI Smoke Tests

Must verify screens open:

- Home.
- Reader.
- Search.
- Bookmarks.
- Settings.
- Trust Center.

## 9. Offline Mode Tests

Manual or automated checklist:

- Enable airplane mode.
- Open app.
- Open reader.
- Open search.
- Add/remove bookmark.
- Resume last-read.
- Open Trust Center.
- Change settings.

All must work offline.

## 10. Permission Audit

Confirm AndroidManifest does not request:

- Location.
- Contacts.
- Microphone.
- Camera.
- Storage.
- Notification.
- Advertising ID.

## 11. Dependency Audit

Confirm build files do not include:

- Ad SDK.
- Analytics SDK.
- Auth SDK.
- Social SDK.
- Cloud sync SDK.
- Push notification SDK.

## Sprint Completion Report Must Include

```text
Tests run:
- Command:
- Result:

Manual checks:
- Offline:
- Permissions:
- Dependencies:

Known issues:
- ...

Guardrail confirmation:
- No ads:
- No analytics:
- No login:
- No tracking:
- Quran display text not modified:
```
