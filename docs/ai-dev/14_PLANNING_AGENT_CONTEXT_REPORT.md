# 14 — Planning Agent Context Report

**As of:** 2026-08-06
**Status of this document:** Canonical current-state snapshot for the external planning agent. This is a point-in-time description of where the project stands, not a changelog. It supersedes any prior briefing given to the planning agent (previously current up to roughly project version 1.1.2). Use it as the source of truth for further roadmap/planning decisions; do not merge it with older briefings.

---

## 1. Product Identity (unchanged, non-negotiable)

- Public app name: **Amanah Quran**
- Project identity: **Amanah-e-Kisa**
- Nature: Charity / Sadaqah Jariyah — not a commercial product
- Platform: Android only (Kotlin + Jetpack Compose), package `org.amanahquran.app`
- Web app: future scope, not started; only a placeholder directory reservation exists
- Governing rules live in `/AGENTS.md` and `/docs/ai-dev/00_AI_AGENT_MASTER_RULES.md`; both still apply and have not been revised

Hard constraints still in force everywhere in the app: no ads, no analytics/tracking SDK, no login/accounts, no cloud sync, no donation popups, no in-app purchases, no push notifications, no network-dependent core feature. These have been verified (see §7) as actually absent from the shipped code, not just stated as policy.

## 2. Release State

Three release lines exist. Only the first is a store-facing shipped release; the other two are candidates.

| Line | Version | Status | Notes |
|---|---|---|---|
| v1.0.5 | versionCode 5 | **SIGNED & APPROVED**, 2026-07-23 | Public-release track. Reviewer sign-off recorded (Dr. Hafiz Muhammad Munaim Tahir). This is the last release with a complete, closed release-gate record. |
| v1.0.4 | versionCode 4 | Superseded internal build | Predecessor to v1.0.5; kept only as ledger history. |
| **V2.0.0** | versionCode 6 | **RELEASE CANDIDATE — engineering gates pass, signed AAB built and device-verified, not yet uploaded to Play Console** | AAB SHA-256 `244912ea6316551a18b680d414b483769f8023c274d7eabe9a49925a0930002b` (built 2026-08-05). Publishing is explicitly an unmade human decision, not a technical blocker. |

**Important repository-state caveat:** the last actual git commit on `main` is `813df6d` ("Approve Amanah Quran V1 public release candidate", 2026-07-15). Everything describing v1.0.5 (2026-07-23) and all V2.0 work (through 2026-08-05) — roughly 50+ files, ~1,800 lines of change including all of §3's new features — currently exists **only in the uncommitted working tree**, not in git history. Any planning that assumes this work is version-controlled or recoverable from git log is currently wrong; it should be committed before being treated as durable.

## 3. What Is Actually Built and Working (feature inventory)

Confirmed present in code (not just planned) as of the current working tree:

**Core reader (v1.0.x, stable):**
- Offline Quran text, 114 surahs / 6,236 ayahs, packaged as a prebuilt Room `quran.db` asset
- IndoPak and Uthmani script rendering with a script switch
- Surah, Juz, and page navigation (including non-ADB-verified page/juz navigation)
- Last-read position tracking and resume
- Ayah and page bookmarks
- Offline search (display text and normalized search text are stored and kept separate — verified, not just policy)
- Elder Mode (larger typography/controls, simplified reader chrome)
- Light / Dark / Sepia / System themes
- Trust Center: source attribution, checksum display, no-modification statement, privacy pledge

**New in V2.0 (candidate, not yet published):**
- **Urdu translation** (QuranEnc, Muhammad Junagarhi, CSV `v1.1.3-csv.1`) as a translation-aware reader mode, with its own DB module (`content/translation`) and offline bundled asset — see the scope note in §9, this is a **Phase 2 ("Understanding Layer") feature per the original roadmap**, now shipped ahead of that phase boundary.
- **Bookmark collections**: folder/collection assignment on bookmarks, checkbox picker, filter by collection (new DAO/entity/repository: `BookmarkCollectionDao`, `BookmarkCollectionEntity`, `BookmarkCollectionRepository`)
- **Local backup/restore**: a versioned export/import codec using Android SAF, with validation preview before applying a restore (`core/backup`)
- **Page Mode redesign**: true fit-to-screen paged reader (`HorizontalPager`, RTL-correct swipe direction), replacing scroll-only reading, with pinch-to-zoom that doesn't persist over the font-size setting
- **Content proof / trust feature area** (`feature/contentproof`) — an additional trust-verification surface beyond the original Trust Center screen
- Trust Center simplified to: what each source is, "nothing has been changed" statement, one "Verify now" on-device checksum action — dropped raw validation-row dumps and internal-test-build wording for the public-facing version
- Reader script toggle consolidated into Settings only (removed the duplicate control that used to live in the reader itself)

**Explicitly still absent / parked (by design, not oversight):**
- Audio recitation — parked for V3.0; no reciter source/license selected yet
- English translation, tafsir, word-by-word, hifz tools, prayer times, qibla, calendar, hadith database, social features — none exist in code, per `09_DO_NOT_BUILD_YET.md`

## 4. Architecture Snapshot

Single Gradle module at `apps/android/app`, package `org.amanahquran.app`, organized as:

```
core/       database, datastore, model, navigation, theme, ui, util, repository, backup, trust
content/    manifest, validation, translation   (content-layer, DB-adjacent)
feature/    home, reader, search, bookmarks, settings, trust, contentproof
```

114 Kotlin source files under `main/`, 23 test files under `test/`. Content is packaged as prebuilt SQLite (Room) assets generated by an offline pipeline (`scripts/generate_content_pipeline.py`, `content-pipeline/`), not fetched at runtime — the app has no network stack for core functionality.

Build config: compileSdk/targetSdk 36 (Android 16), minSdk 24 (Android 7.0), AGP 9.0.0 (ledger) moving toward 9.x/Kotlin Compose plugin in the uncommitted tree, R8 minification + resource shrinking enabled, full native debug symbols configured for Play Console.

## 5. Content & Data Status

- **Quran text**: Tanzil Uthmani XML is the primary Uthmani source; Tanzil Simple Clean XML is cross-check/search-normalization input only. IndoPak assets are from QUL and are noted in `CONTENT_SOURCE_DECISIONS.md` as "retained but under license review" (see discrepancy in §10).
- **Urdu translation**: QuranEnc Urdu Junagarhi, CSV `v1.1.3-csv.1`, source SHA-256 `027cd258...4481fec4`, 6,236/6,236 rows mapped, packaged Room DB SHA-256 `9f08b2e4...79970a33677`. Content/scholar accuracy review recorded as APPROVED (Hafiz Dr. Muhammad Munaim Tahir).
- Display text and search-normalized text are architecturally and empirically separate (verified by an AI structural/suspicious-character/separation audit trail under `docs/_ai_quran_audit*`, all GO).

## 6. Licensing / Legal Status

- **Fonts**: `digital_khatt_indopak.otf`, `indopak_nastaleeq.ttf`, `digital_khatt_v2.otf` are the three actually-bundled/referenced fonts, cleared per `docs/legal/FINAL_LICENSE_CLEARANCE_DECISION.md` (public-release status updated 2026-07-23). Several previously-considered fonts (`uthmanic_hafs_v22.ttf`, `KFGQPCNastaleeq-Regular.ttf`, `QPC_V2_Hafs.ttf`) are **not** present in the tree — treat those as not in use, not as pending.
- **Urdu translation**: cleared for public distribution 2026-08-02 by maintainer authorization (`docs/legal/TRANSLATION_LICENSE_CLEARANCE_DECISION.md`). Known non-blocking gap: no archived copy of QuranEnc's terms-of-use page is saved under `docs/legal/evidence/` yet (font clearances have this; translation clearance currently rests on maintainer attestation only).

## 7. QA / Release-Gate Status

For the closed v1.0.5 line, `docs/_release_gate/RELEASE_READINESS_MATRIX.md` records **GO on every gate**: content traceability, DB validation, import validation, reader/search/navigation/bookmarks functionality, real-device validation (TECNO CH6i, Android 13), AI structural/character/separation audits, manual Quran text review, font/license review, Trust Center wording, privacy/permission audit (no dangerous permissions, no ad/tracking SDKs), final build/test/lint, Play Store declarations. Final verdict recorded: **RELEASE APPROVED / GO**.

For V2.0 (candidate), `docs/_implementation/V2_IMPLEMENTATION_STATE.md` records 86/86 unit tests passing, clean lint, and direct on-device verification (same physical device) for both debug and R8-minified release builds, across 11 tracked sprint areas (10 PASS, 1 PARKED — audio). Known non-blocking gaps recorded there: TalkBack accessibility evidence only spot-checked (not full screen-by-screen), no deep performance profiling beyond a cold-start/memory snapshot, and Android 16 emulator UI verification is blocked by a host/AVD infrastructure defect (root-caused as unrelated to the app; physical-device verification substitutes for it).

## 8. Repository State

- `main` is 8 commits deep total, last commit 2026-07-15 (v1.0.5-era `AGENTS.md`/scaffold work).
- One tag exists: `v1.0.0-rc-public-approved`.
- Working tree currently has ~53 modified files and several new untracked files/directories (translation module, backup module, bookmark-collection files, V2 implementation-state docs, `RELEASE_LEDGER.md`) representing all of v1.0.5's documentation and all of V2.0 — see the caveat in §2.
- No remote-tracking divergence beyond the above (`main...origin/main`, working tree ahead only by uncommitted local changes).

## 9. Position Relative to the Original Roadmap

`markdown/08_Roadmap_Backlog_and_Milestones.md` (not yet revised to reflect V2.0) defines V1 as "Phase 1 — Sacred Reader MVP" with translation, Urdu UI, tafsir, and word-by-word explicitly deferred to "Phase 2 — Understanding Layer," and hifz/masjid/AI features deferred further to Phases 3–5. **V2.0 has already shipped a Phase 2 item (Urdu translation) while still under the "V1"/"Sacred Reader" identity**, ahead of that document's stated sequencing. Audio (also nominally Phase 2/3-adjacent) is explicitly parked for V3.0 rather than V2.0. Planning should treat the roadmap doc's phase boundaries as directional intent rather than a currently-accurate plan — it has not been updated to reconcile with what V2.0 actually became.

## 10. Open Discrepancies Worth Resolving Before Further Planning

These are standing inconsistencies in the canonical docs themselves, not bugs in the app:

1. `CONTENT_SOURCE_DECISIONS.md` still states "Approved reader fonts remain internal-testing only until explicit public distribution approval exists," while `docs/legal/FINAL_LICENSE_CLEARANCE_DECISION.md` records public-release approval as of 2026-07-23. One of these two documents is stale.
2. `CONTENT_SOURCE_DECISIONS.md` also still flags QUL IndoPak assets as "under review until license status is cleared" — this has not been closed out with a decision record the way fonts and translation have.
3. The root-level `markdown/`, `docs/markdown/`, `source/`, and `docs/source/` directories appear to duplicate the same charter/PRD/input documents in two locations; no canonical-copy decision is recorded.
4. AGENTS.md and `09_DO_NOT_BUILD_YET.md` still describe a "V1 Sacred Reader MVP" that forbids translation entirely, while V2.0 is a translation-bearing release under active release-candidate status. These top-level agent-facing rule docs have not been revised to define what V2.0 is actually allowed to contain.

## 11. Where To Look For More

- Full V2.0 engineering evidence: `docs/_implementation/V2_IMPLEMENTATION_STATE.md`
- Release history and build hashes: `docs/_release_gate/RELEASE_LEDGER.md`
- Per-gate v1.0.5 evidence: `docs/_release_gate/RELEASE_READINESS_MATRIX.md` and sibling files in that directory
- Legal/licensing: `docs/legal/`
- Original product scope and constraints: `/AGENTS.md`, `docs/ai-dev/00_AI_AGENT_MASTER_RULES.md`, `docs/ai-dev/09_DO_NOT_BUILD_YET.md`
- Roadmap (directional, not current — see §9): `markdown/08_Roadmap_Backlog_and_Milestones.md`
