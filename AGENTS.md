# AGENTS.md — Amanah Quran AI Development Rules

This repository is for **Amanah Quran**, under the wider project identity **Amanah-e-Kisa**.

## Before Any Coding

Every AI coding agent must first read:

1. `/docs`
2. `/docs/ai-dev`
3. `/AGENTS.md`

Then summarize:

- Current project status.
- Current approved scope.
- Existing files.
- Proposed files to modify.
- Tests to run.

Do not make code changes before this discovery summary.

## Product Identity

- Public app name: **Amanah Quran**
- Project identity: **Amanah-e-Kisa**
- Nature: Charity / Sadaqah Jariyah
- Platform: Android
- Future platform: Web app may be added later, but implementation remains Android-first.

## Current Release Status

- **V1 Sacred Reader MVP:** shipped (public release candidate approved).
- **V2.x:** shipped through **v2.2.0** (versionCode 11). See `docs/releases/v2.2/V2_2_IMPLEMENTATION_REPORT.md` and `DEFERRED_ITEMS_FOR_REVIEW.md`.

New work builds on the shipped V2.2 scope below. Anything not listed as shipped or approved needs explicit owner approval before it is built.

## Shipped Scope

V1 Sacred Reader MVP:

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

V2.x additions (owner-approved):

- English and Urdu translations (Off / English / Urdu), from verified, licensed sources only, shown in the Trust Center.
- Continuous-scroll reader and auto-scroll.
- Local-only backup and restore of user data (bookmarks, last-read, settings, reading activity, reminders).
- Reading streak, reading activity statistics and reading calendar (on-device only).
- Local reading reminders via WorkManager. Opt-in, with the notification permission requested only when the user turns reminders on. No remote push.
- Daily Ayah (text-only, deterministic, on-device) and a home / lock-screen widget that opens the exact ayah.

Gated (contracts exist; must stay disabled until the owner approves a source and licence):

- Audio recitation (`core/audio` ships `NoApprovedAudioRepository` only).
- Curated Daily Ayah pool (needs a reviewed eligibility dataset).

## Still Not Allowed

Do not add:

- Ads.
- Analytics SDK.
- Tracking.
- Login.
- Accounts.
- Cloud sync or server backend.
- Donation popups.
- In-app purchases.
- Tafsir.
- Word-by-word meaning.
- Hifz tools.
- AI or AI-generated religious content.
- Prayer times.
- Qibla.
- Islamic (Hijri) calendar.
- Hadith database.
- Social features.
- Remote push notifications.
- Any network-dependent core feature.
- New translations or audio sources without verified source and licence evidence.

## Quran Text Integrity

- Never modify Quran display text or translation display text.
- Display text must come only from verified source data.
- Search-normalized text must be stored separately.
- Never render normalized text as Quran display text.
- Bookmarks and last-read must use canonical references such as `surah:ayah` and page number, not visual text.

## Privacy Rules

The app must remain:

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

- It stays inside the shipped / approved scope above.
- It does not add prohibited features.
- It does not introduce tracking or monetization.
- It preserves Quran text integrity.
- It works offline where applicable.
- It has tests or a valid no-test explanation.
- It updates relevant docs when architecture, data contracts, or release rules change.
