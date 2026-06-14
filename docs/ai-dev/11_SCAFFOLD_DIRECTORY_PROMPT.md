# 11 — Scaffold Directory Prompt

Use this prompt after placing the AI dev pack into the project root.

```text
You are working inside the Amanah Quran repository.

Important existing condition:
- All main project documentation already exists in /docs.
- The AI development files exist in /docs/ai-dev.
- Do not delete, rename, overwrite, or duplicate existing /docs documentation.
- Read /AGENTS.md, /docs, and /docs/ai-dev before making changes.

Goal:
Set up a clean repository scaffold for Android V1 while keeping the repo ready for a future web app.

V1 product boundaries:
- Build only Amanah Quran Sacred Reader MVP.
- Android-first.
- Future web app placeholder only.
- No web app implementation in this sprint.

Core principles:
- No ads.
- No analytics.
- No tracking.
- No login.
- No cloud sync.
- No donation popups.
- No in-app purchases.
- No unnecessary permissions.
- Quran display text must never be modified.
- Search-normalized text must be separate from display text.
- Core reader must work offline.

Preferred scaffold if no Android project exists yet:

/
  AGENTS.md
  README.md
  apps/
    android/
      settings.gradle.kts
      build.gradle.kts
      gradle.properties
      app/
        build.gradle.kts
        src/
          main/
            AndroidManifest.xml
            kotlin/
              org/
                amanahquran/
                  app/
                    MainActivity.kt
                    AmanahQuranApp.kt
                    core/
                      database/
                      datastore/
                      model/
                      navigation/
                      theme/
                      ui/
                      util/
                    feature/
                      home/
                      reader/
                      search/
                      bookmarks/
                      settings/
                      trust/
                    content/
                      manifest/
                      validation/
            assets/
              db/
              content/
              fonts/
          test/
          androidTest/
    web/
      README.md
      .gitkeep
  packages/
    shared/
      README.md
      schemas/
      contracts/
  docs/
    ai-dev/
  tools/
    content-import/
      README.md
    validation/
      README.md
    release/
      README.md
  .github/
    workflows/
      README.md

If an Android project already exists at repository root:
- Do not move it.
- Do not restructure it automatically.
- Add only safe placeholders:
  /apps/web/README.md
  /apps/web/.gitkeep
  /packages/shared/README.md
  /packages/shared/schemas/
  /packages/shared/contracts/
  /tools/content-import/README.md
  /tools/validation/README.md
  /tools/release/README.md
- Create /docs/ai-dev/repo_migration_note.md explaining how the repo could later migrate to /apps/android.

Android scaffold requirements:
- Kotlin.
- Jetpack Compose.
- Package namespace: org.amanahquran.app.
- Create placeholder screens only:
  HomeScreen
  ReaderScreen
  SearchScreen
  BookmarksScreen
  SettingsScreen
  TrustCenterScreen
- Create navigation routes for these screens.
- Create basic theme structure including planned System, Light, Dark, and Sepia options.
- No Quran database implementation in this sprint.
- No content import in this sprint.
- No search implementation in this sprint.
- No bookmarks implementation in this sprint.

Future web app rules:
- Create placeholder only.
- Do not install web framework yet.
- Do not add React/Next/Vite/Svelte/etc. yet.
- Do not add npm/pnpm/yarn lockfiles yet unless explicitly requested.
- The web README must say web app is future scope and must follow no-ads, no-tracking, verified-content principles.

Shared package rules:
- Shared package is documentation/contracts only for now.
- Do not create runtime shared library.
- Do not add backend API client.
- Do not add auth or cloud sync contracts except as future notes.

Tests/build:
- Run Gradle build if Android scaffold is created.
- Run available tests.
- If tests cannot run, explain exactly why.

Final response required:
1. Tree of created/changed files.
2. Confirmation that existing /docs files were not modified.
3. Confirmation that no out-of-scope V1 features were added.
4. Build/test commands run and results.
5. Any assumptions made.
6. Recommended next sprint prompt.
```
