# 08 — Context Refresh Prompt

Use this prompt at the start of every new AI coding session.

```text
You are working on the Amanah Quran project.

Before coding:
1. Read /AGENTS.md.
2. Read all files in /docs.
3. Read all files in /docs/ai-dev.
4. Inspect the repository structure.
5. Summarize current project status.

Project rules:
- Public app name: Amanah Quran.
- Project identity: Amanah-e-Kisa.
- V1 scope: Sacred Reader MVP only.
- Android-first, future web app later.
- No ads, no analytics, no tracking, no login, no cloud sync, no donation prompts, no in-app purchases.
- Do not build audio, translation, tafsir, word-by-word, hifz tools, AI, prayer times, qibla, calendar, hadith, social features, or push notifications in V1.
- Quran display text must never be modified.
- Search-normalized text must be separate and must never be rendered as Quran display text.
- All core features must work offline.

After reading, report:
1. Current scope.
2. Existing structure.
3. Files you propose to modify.
4. Tests you will run.
5. Guardrails that apply.

Do not modify files until after this summary.
```
