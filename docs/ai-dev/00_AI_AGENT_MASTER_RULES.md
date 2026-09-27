# 00 — AI Agent Master Rules

## Purpose

These rules control all AI-assisted development for Amanah Quran.

## Locked Identity

- Project identity: **Amanah-e-Kisa**
- Public product name: **Amanah Quran**
- V1 release theme: **Sacred Reader MVP**
- Primary platform: **Android**
- Commercial model: **Completely free, no ads, no in-app purchases, no monetization pressure**

## Current Scope

> **Status (v2.2.0):** V1 has shipped and V2.x additions (English/Urdu translations, continuous reader, local backup/restore, streaks/stats/reading calendar, local reminders, Daily Ayah widget) are approved and shipped. The full current scope is in `/AGENTS.md`. The V1 list below is kept as the baseline.

## V1 Scope (baseline)

V1 included:

1. Offline Quran reading.
2. IndoPak script.
3. Uthmani script.
4. Script switching.
5. Surah navigation.
6. Juz navigation.
7. Page navigation.
8. Last-read position.
9. Bookmarks.
10. Offline search.
11. Elder Mode.
12. Themes.
13. Trust Center.
14. Source attribution.
15. Content validation.

## Non-Negotiable Guardrails

1. No ads.
2. No ad SDK.
3. No analytics SDK.
4. No tracking.
5. No forced login.
6. No accounts.
7. No cloud dependency.
8. No donations inside the reader.
9. No in-app purchases.
10. No unnecessary Android permissions. (`POST_NOTIFICATIONS` is allowed only for opt-in local reminders, requested at runtime.)
11. No Quran text modification.
12. No unverified Quran display text.
13. No network dependency for core reader functions.
14. No sectarian or political content.

## Content Safety Rules

- Quran display text is immutable after import.
- Display Quran text must never be transformed at runtime except for normal font rendering.
- Search normalization must be separate from display text.
- A content manifest is required for each content pack.
- Each content pack must include source, license, version, checksum, import date, and validation status.
- Public release requires manual content review.

## Privacy Rules

The app must not collect, transmit, infer, or share user data in V1.

Local-only allowed data:

- Last-read position.
- Bookmarks.
- Theme setting.
- Script setting.
- Elder Mode setting.
- Translation selection (V2.x).
- Reading activity, streaks and reminder settings (V2.x, on-device only).
- Daily Ayah history (V2.x, last 30 canonical ayah keys).

## Android Technical Rules

Recommended stack:

- Kotlin.
- Jetpack Compose.
- Room SQLite.
- DataStore.
- ViewModels.
- Repository layer.
- No third-party SDK unless justified and approved.

## Future Web App Rule

The repository may be organized as a future-ready monorepo. However:

- Do not build the web app in V1.
- Do not create backend services for V1.
- Do not make Android depend on web code.
- Shared contracts may be prepared as documentation or plain schema files only.

## Definition of Done

Every implementation step must prove:

- Current approved scope respected (see `/AGENTS.md`).
- Guardrails respected.
- Tests run or test limitation explained.
- Quran text integrity preserved.
- Offline behavior preserved.
- Docs updated when relevant.
