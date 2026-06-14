# QA and Release Checklist
# Amanah Quran - V1 Sacred Reader MVP

## 1. Release Philosophy

Amanah Quran should not be released because the code compiles. It should be released only when the Quran content, offline behavior, reader stability, privacy model, and Trust Center are verified.

## 2. Functional QA

| ID | Test | Expected Result | Status |
|---|---|---|---|
| F-001 | First app launch | App opens without login, permissions, ads, or donation prompt | Pending |
| F-002 | Open Surah Al-Fatihah | Surah opens offline in selected script | Pending |
| F-003 | Open Surah Al-Baqarah | Long Surah scrolls smoothly | Pending |
| F-004 | Open Surah Yaseen | Surah opens correctly | Pending |
| F-005 | Open Juz 30 | Juz opens correctly | Pending |
| F-006 | Open page 1 | Page opens correctly | Pending |
| F-007 | Switch IndoPak to Uthmani | Text changes, position remains stable | Pending |
| F-008 | Switch Uthmani to IndoPak | Text changes, position remains stable | Pending |
| F-009 | Bookmark ayah | Bookmark appears in list | Pending |
| F-010 | Remove bookmark | Bookmark disappears | Pending |
| F-011 | Bookmark page | Page bookmark appears | Pending |
| F-012 | Resume last-read | Opens previous location | Pending |
| F-013 | Search Surah name | Correct Surah appears | Pending |
| F-014 | Search ayah reference | Correct ayah opens | Pending |
| F-015 | Search Arabic text | Matching ayahs appear | Pending |
| F-016 | Enable Elder Mode | Text/buttons become larger | Pending |
| F-017 | Disable Elder Mode | UI returns to standard mode | Pending |
| F-018 | Change theme | Theme changes and persists | Pending |
| F-019 | Open Trust Center | Source/verification/privacy data visible | Pending |
| F-020 | Airplane mode full use | Core V1 features work offline | Pending |

## 3. Content QA

| ID | Test | Expected Result | Severity | Status |
|---|---|---|---|---|
| C-001 | Surah count | 114 | Critical | Pending |
| C-002 | Ayah count | 6236 | Critical | Pending |
| C-003 | Duplicate ayah keys | 0 | Critical | Pending |
| C-004 | Missing IndoPak text | 0 | Critical | Pending |
| C-005 | Missing Uthmani text | 0 | Critical | Pending |
| C-006 | Missing page mapping | 0 | Critical | Pending |
| C-007 | Missing Juz mapping | 0 | Critical | Pending |
| C-008 | Search index row count | Matches ayah count | Critical | Pending |
| C-009 | Content manifest | Present and complete | Critical | Pending |
| C-010 | Checksums | Match source manifest | Critical | Pending |
| C-011 | Trust Center metadata | Complete for both scripts | Critical | Pending |
| C-012 | Manual review | Completed before public release | Critical | Pending |

## 4. Privacy QA

| ID | Test | Expected Result | Status |
|---|---|---|---|
| P-001 | Runtime permissions | No dangerous permissions requested | Pending |
| P-002 | Ad SDK scan | No ad SDK present | Pending |
| P-003 | Analytics SDK scan | No analytics SDK present | Pending |
| P-004 | Advertising ID usage | Not used | Pending |
| P-005 | Login/account flow | Not present | Pending |
| P-006 | Network dependency | Core V1 features work without internet | Pending |
| P-007 | Data transmission | No user data transmitted | Pending |
| P-008 | Play Store Data Safety | Can declare no data collected/shared | Pending |

## 5. Device QA

Test minimum:

1. Samsung mid-range phone.
2. Vivo phone.
3. Oppo phone.
4. Xiaomi/Redmi phone.
5. Infinix phone.
6. Tecno phone.
7. Small-screen Android phone.
8. Large-screen Android phone.

For each device:

1. Install fresh build.
2. Launch app.
3. Open reader.
4. Switch scripts.
5. Search Arabic text.
6. Bookmark and remove bookmark.
7. Enable Elder Mode.
8. Test dark and sepia themes.
9. Kill and reopen app to test last-read.
10. Use app in airplane mode.

## 6. Accessibility QA

1. Elder Mode usable on small screens.
2. Tap targets large enough.
3. Text readable in all themes.
4. No icon-only critical actions without labels where clarity is needed.
5. TalkBack can navigate core screens.
6. Android font scaling does not break critical screens.
7. Back navigation predictable.
8. No essential state depends only on color.

## 7. Release Blockers

Do not release if any of the following are present:

1. Quran content validation failure.
2. Missing or unverified script pack.
3. Missing Trust Center metadata.
4. Unclear source license.
5. Any ad SDK.
6. Any analytics SDK.
7. Any forced login.
8. Any unnecessary permission.
9. Any crash in reader flow.
10. Search displaying normalized Quran text as display text.
11. Bookmarks or last-read failing offline.
12. Elder Mode hiding critical controls.

## 8. Final Release Sign-Off

| Area | Owner | Status | Notes |
|---|---|---|---|
| Product scope | Product lead | Pending |  |
| Quran text verification | Content reviewer | Pending |  |
| Android functionality | Developer | Pending |  |
| Privacy review | Product/technical reviewer | Pending |  |
| Device QA | QA tester | Pending |  |
| Play Store readiness | Release owner | Pending |  |
