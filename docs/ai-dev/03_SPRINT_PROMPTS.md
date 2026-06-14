# 03 — Sprint Prompts

Copy these prompts into Codex, Gemini CLI, Cursor, or another AI coding agent.

## Prompt 1 — Project Discovery

```text
Read /AGENTS.md, all existing /docs files, and /docs/ai-dev. Do not modify files yet.

Summarize:
1. Current Amanah Quran V1 scope.
2. Existing repository structure.
3. Existing Android project status, if any.
4. Missing files or folders.
5. Recommended next coding sprint.
6. Risks to V1 guardrails.

Remember:
- V1 is Sacred Reader only.
- No ads, analytics, login, tracking, cloud sync, audio, translation, tafsir, AI, prayer times, qibla, hadith, social features, donations, or push notifications.
- Quran display text must never be modified.
```

## Prompt 2 — Future-Ready Scaffold Setup

```text
Read /AGENTS.md, /docs, and /docs/ai-dev.

Task:
Set up the repository scaffold for Amanah Quran with Android V1 as the active app and a future web app placeholder.

Important:
- If no Android project exists, create a future-ready monorepo structure:
  /apps/android
  /apps/web
  /packages/shared
  /tools
  /.github/workflows
  /docs/ai-dev
- If an Android project already exists at root, do not move it. Instead, create only safe placeholder folders and document a future migration plan.
- Keep all existing /docs files unchanged.

Android V1 target:
- Kotlin.
- Jetpack Compose.
- Room SQLite later.
- DataStore later.
- Package: org.amanahquran.app.

Create only scaffold and placeholder files in this sprint:
- Android build scaffold if missing.
- Placeholder Compose screens:
  Home, Reader, Search, Bookmarks, Settings, Trust Center.
- apps/web/README.md stating web app is future scope and must not be implemented yet.
- packages/shared/README.md stating shared contracts are future-only except schema templates.
- tools/README.md with content-import, validation, and release tool folder purposes.

Do not implement Quran data yet.
Do not add ads, analytics, login, tracking, network features, cloud sync, audio, translation, tafsir, AI, prayer times, qibla, hadith, social features, donations, or push notifications.

Run available build/tests and report:
1. Files created/changed.
2. Tests/build commands run.
3. Results.
4. Confirmation that existing /docs files were not modified.
5. Confirmation that V1 guardrails were preserved.
```

## Prompt 3 — Android Navigation Shell

```text
Read /AGENTS.md and /docs/ai-dev.

Implement the Android navigation shell only:
- Home.
- Reader.
- Search.
- Bookmarks.
- Settings.
- Trust Center.

Use Jetpack Compose.
Keep screens as respectful placeholders.
Add no Quran content yet.
Add no network or SDK dependencies.
Run build/tests.
Report changed files and results.
```

## Prompt 4 — Room Schema

```text
Read /AGENTS.md and /docs/ai-dev/05_CONTENT_DATA_CONTRACTS.md.

Implement Room entities and DAOs for:
- Surah.
- Ayah.
- QuranText.
- SearchIndex.
- Bookmark.
- LastRead.
- ContentSource.
- ContentValidation.

Rules:
- QuranText.displayText is verified display text.
- SearchIndex.normalizedArabic is search-only.
- Bookmarks and LastRead use canonical references.
- Do not import real Quran content in this sprint.

Add DAO/unit tests.
Run tests.
Report changed files and results.
```

## Prompt 5 — Content Validation Tooling

```text
Read /AGENTS.md and /docs/ai-dev/05_CONTENT_DATA_CONTRACTS.md.

Implement validation tooling for content manifests and database integrity.

Validation must check:
- 114 Surahs.
- 6236 ayahs.
- No missing ayah keys.
- No duplicate ayah keys.
- IndoPak and Uthmani text stored separately.
- Search index count matches ayah count.
- Content source metadata includes source, license, version, checksum, import date, validation status.
- Normalized text is never used as display Quran text.

Do not fetch content from network in the app.
Add tests and report results.
```

## Prompt 6 — Reader Screen

```text
Implement the offline reader screen using local repository interfaces.

Reader must support:
- Surah mode.
- Juz mode.
- Page mode.
- Ayah-level rendering.
- Placeholder data only if verified Quran DB is not yet available.

Rules:
- Do not modify Quran display text.
- Do not add translation, tafsir, audio, or AI.
- Do not add popups or ad spaces.

Add ViewModel tests where applicable.
Run build/tests.
Report results.
```

## Prompt 7 — Script Switching

```text
Implement IndoPak/Uthmani script selection.

Requirements:
- Selection persists locally.
- Reader updates without losing canonical position.
- Bookmark identity does not depend on script.
- Search previews use selected script.

Do not transform one script into another.
Use only separate stored display text fields.
Run tests and report.
```

## Prompt 8 — Last-Read

```text
Implement local last-read save and restore.

Store:
- Ayah key.
- Surah number.
- Ayah number.
- Juz number.
- Page number.
- Script type.
- Optional scroll offset.
- Updated timestamp.

No cloud sync.
No account.
No network.
Run tests and report.
```

## Prompt 9 — Bookmarks

```text
Implement local bookmarks.

Support:
- Ayah bookmark.
- Page bookmark.
- Remove bookmark.
- Bookmark list.
- Open bookmark in reader.

Use canonical ayah key/page number.
Ensure script switching does not break bookmarks.
Run tests and report.
```

## Prompt 10 — Offline Search

```text
Implement offline search.

Support:
- Surah name.
- Surah number.
- Ayah reference like 2:255.
- Juz number.
- Page number.
- Arabic text via separate normalized search index.

Never display normalized Quran text.
Render result preview using verified display text in selected script.
Run tests and report.
```

## Prompt 11 — Elder Mode and Themes

```text
Implement Elder Mode and themes.

Elder Mode:
- Larger Arabic text.
- Larger UI text.
- Larger tap targets.
- Simplified reader controls.

Themes:
- System.
- Light.
- Dark.
- Sepia.

Persist settings locally.
No decorative backgrounds that reduce Quran readability.
Run tests and report.
```

## Prompt 12 — Trust Center

```text
Implement Trust Center.

Show:
- Source name.
- Source URL.
- License.
- Version.
- Checksum.
- Import date.
- Ayah count.
- Surah count.
- Validation status.
- Manual review status.
- No-modification statement.
- No ads/no tracking/no login privacy pledge.
- App version/content version.

Must work offline.
Run tests and report.
```

## Prompt 13 — QA Hardening

```text
Run a V1 QA hardening pass.

Check:
- Offline mode.
- No dangerous permissions.
- No ad SDK.
- No analytics SDK.
- No login/auth dependency.
- No network dependency for reader.
- Search works offline.
- Bookmarks work offline.
- Last-read works offline.
- Elder Mode works.
- Themes work.
- Trust Center works.
- Content validation gates are present.

Fix only V1-scope issues.
Run tests/build and report.
```

## Prompt 14 — Final Release Audit

```text
Perform final release audit for Amanah Quran V1.

Do not add new features.

Audit:
- V1 scope compliance.
- Content validation.
- Trust Center completeness.
- Privacy/no-tracking compliance.
- Dependency audit.
- Permission audit.
- Offline behavior.
- Device readiness.
- Play Store Data Safety readiness.

Generate a release readiness report.
```
