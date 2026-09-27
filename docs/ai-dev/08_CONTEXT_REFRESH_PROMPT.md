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
- Current release: v2.2.0. V1 Sacred Reader MVP plus approved V2.x additions (see /AGENTS.md "Shipped Scope").
- Android-first, future web app later.
- No ads, no analytics, no tracking, no login, no cloud sync, no donation prompts, no in-app purchases.
- Do not build tafsir, word-by-word, hifz tools, AI, prayer times, qibla, Hijri calendar, hadith, social features, or remote push notifications. Audio stays gated until an approved source exists.
- Quran and translation display text must never be modified.
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
