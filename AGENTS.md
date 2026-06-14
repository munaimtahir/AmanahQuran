# AGENTS.md — Amanah Quran AI Development Rules

This repository is for **Amanah Quran**, under the wider project identity **Amanah-e-Kisa**.

## Before Any Coding

Every AI coding agent must first read:

1. `/docs`
2. `/docs/ai-dev`
3. `/AGENTS.md`

Then summarize:

- Current project status.
- V1 scope.
- Existing files.
- Proposed files to modify.
- Tests to run.

Do not make code changes before this discovery summary.

## Product Identity

- Public app name: **Amanah Quran**
- Project identity: **Amanah-e-Kisa**
- Nature: Charity / Sadaqah Jariyah
- Platform for V1: Android
- Future platform: Web app may be added later, but V1 implementation remains Android-first.

## V1 Sacred Reader MVP Scope

Build only:

- Offline Quran reading.
- IndoPak script support.
- Uthmani script support.
- Script switching.
- Surah navigation.
- Juz navigation.
- Page navigation.
- Last-read position.
- Bookmarks.
- Offline search.
- Elder Mode.
- Light / Dark / Sepia / System themes.
- Trust Center.
- Content source attribution and verification.

## Do Not Build in V1

Do not add:

- Ads.
- Analytics SDK.
- Tracking.
- Login.
- Accounts.
- Cloud sync.
- Donation popups.
- In-app purchases.
- Audio.
- Translation.
- Tafsir.
- Word-by-word meaning.
- Hifz tools.
- AI.
- Prayer times.
- Qibla.
- Islamic calendar.
- Hadith database.
- Social features.
- Push notifications.
- Any network-dependent core feature.

## Quran Text Integrity

- Never modify Quran display text.
- Display text must come only from verified source data.
- Search-normalized text must be stored separately.
- Never render normalized text as Quran display text.
- Bookmarks and last-read must use canonical references such as `surah:ayah` and page number, not visual text.

## Privacy Rules

V1 must remain:

- No ads.
- No tracking.
- No analytics SDK.
- No advertising ID.
- No forced login.
- No unnecessary permissions.
- No data collection.
- No data sharing.
- Fully functional offline after install.

## Repository Organization

Preferred future-ready layout:

```text
/apps/android
/apps/web
/packages/shared
/docs
/docs/ai-dev
/tools
```

If an Android project already exists at root with `/app`, do not move it without explicit approval. In that case, preserve current structure and document a future migration plan.

## Coding Requirements

Every code change must include one of:

- Unit tests.
- UI tests.
- Data validation tests.
- A clear written reason why tests are not applicable.

Every sprint must end with a short implementation report including:

- Files changed.
- Features completed.
- Tests run.
- Results.
- Known issues.
- Scope guardrail confirmation.

## Definition of Done

A change is done only if:

- It stays inside V1 scope.
- It does not add prohibited features.
- It does not introduce tracking or monetization.
- It preserves Quran text integrity.
- It works offline where applicable.
- It has tests or a valid no-test explanation.
- It updates relevant docs when architecture, data contracts, or release rules change.
