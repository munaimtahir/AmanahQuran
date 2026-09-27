# Repo Hygiene, Scope Docs and v2.2 Review — Implementation Report

Date: 2026-09-27. Branch: `claude/using-credits-nbqbb1`. Baseline: v2.2.0 (versionCode 11).

## Features completed

1. **Claude Code on the web session hook.** `.claude/hooks/session-start.sh` installs the Android SDK
   (cmdline-tools, platform 36, build-tools 36.0.0), writes `apps/android/local.properties` and warms the
   Gradle cache. It only runs when `CLAUDE_CODE_REMOTE=true`. `.gitignore` now keeps `.claude/settings.json`
   and `.claude/hooks/` tracked; the rest of `.claude/` stays ignored.
2. **Scope docs aligned with shipped v2.2.** `AGENTS.md`, `docs/ai-dev/00`, `08` and `09` now list what has
   shipped (V1 plus approved V2.x additions), what is gated (audio, curated Daily Ayah pool) and what is still
   not allowed.
3. **Review of v2.2 code** (Daily Ayah, widget, backup/restore, reminders), with three fixes (below).
4. **Repo tidy.**
   - `V2_2_*.md` moved to `docs/releases/v2.2/`.
   - The documentation pack (.md/.docx/.pdf) moved to `docs/`, where `docs/ai-dev/12_PROJECT_BOOTSTRAP_SUMMARY.md` expects it.
   - `FInal Sign off Form.md` became `docs/_public_release_approval/SCHOLAR_REVIEWER_SIGN_OFF.md`.
   - `README_AI_DEV_MISSING_FILES.md` moved to `docs/ai-dev/`.
   - The root `script.sh` and `clear_active_font_license.sh` moved to `tools/legal/`.
   - The debug `apps/android/window_dump.xml` and the committed `scripts/__pycache__/` were removed, and `__pycache__` is now ignored.
   - The content-pipeline reports stay at the root because `scripts/generate_content_pipeline.py` writes them there.
   - `docs/legal.zip` was kept: its evidence snapshots differ from the current `docs/legal/` files.

## Review fixes

| Area | Problem | Fix |
|---|---|---|
| Daily Ayah history | Full-corpus picks were stored as `REVIEWED_RANDOM` and shown as "reviewed_random", which implies scholar review that never happened. | Added `FULL_CORPUS_RANDOM` and `DailyAyahSelector.select`. Legacy `REVIEWED_RANDOM` records are labelled "random". |
| Backup restore | Reminder `hour`/`minute` were not range-checked. With reminders on, `withHour` threw during restore. With them off, the bad value was stored and `LocalTime.of` could crash the reminder screen. | Values are clamped to 0–23 / 0–59 at import. |
| Widget | An exception from `getToday()` inside the widget's `Dispatchers.IO` coroutines was uncaught, so it could crash the app process from a background broadcast. | The widget now falls back to the placeholder view. |

## Review findings not changed (for owner decision)

- **Lock-screen widget:** `widgetCategory="keyguard"` has no effect on phones running Android 5.0 or later, so it only works on some newer tablets. The v2.2 "lock screen widget" works as a home-screen widget on most devices.
- **Widget text font:** the widget renders Arabic with the system font, not the verified Quran fonts, so IndoPak marks may render differently from the reader. This needs a device check against the glyph-fallback release blocker.
- **Widget refresh:** `DailyAyahWidgetProvider.refresh()` has no callers, so changing the script or translation updates the widget only on the next 30-minute period. Its `notifyAppWidgetViewDataChanged` call does nothing for a non-collection widget.
- **Permission-audit gate:** `docs/ai-dev/06_TESTING_GATES.md` still lists Notification as a forbidden permission, while v2.x ships `POST_NOTIFICATIONS` for opt-in local reminders. This gate was left unchanged on purpose because loosening it needs owner sign-off.

## Tests run

- `./gradlew :app:testDebugUnitTest :app:lintDebug --no-daemon`: BUILD SUCCESSFUL, 276 tests, 0 failures (4 new), lint clean.
- Session hook: executed with `CLAUDE_CODE_REMOTE=true`, completed in about 90 seconds.

## Known issues

- Maven Central sometimes returns HTTP 429 in cloud sessions. Re-running Gradle resolves it.
- No device or emulator was available, so the widget changes are covered by unit tests and compilation only.

## Scope guardrail confirmation

No ads, analytics, tracking, login, network features or monetization were added. No new permissions or
dependencies were added. Quran and translation display text were not modified.
