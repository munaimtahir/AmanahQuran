# 01 — Repository Structure

## Goal

Keep the repository organized for the Android V1 app while allowing a future web app to be added cleanly later.

## Preferred Future-Ready Monorepo Structure

Use this if the repository is still empty or has no Android project yet:

```text
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
            assets/
              db/
              content/
              fonts/
            kotlin/
              org/
                amanahquran/
                  app/
                    AmanahQuranApp.kt
                    MainActivity.kt
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
          test/
          androidTest/
    web/
      README.md
      .gitkeep
  packages/
    shared/
      README.md
      schemas/
        content_manifest.schema.json
        content_validation.schema.json
      contracts/
        README.md
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
```

## If Android Already Exists at Repository Root

If the existing Android project already has:

```text
/app
/build.gradle.kts
/settings.gradle.kts
```

then do **not** move the project automatically.

Instead:

1. Preserve the current Android structure.
2. Add `/apps/web/.gitkeep` only as a future placeholder if approved.
3. Add `/packages/shared` only for contracts/templates if approved.
4. Document any future migration in `/docs/ai-dev/10_FUTURE_WEBAPP_READY_SCAFFOLD.md`.

## Android Source Package Target

Inside Android app code, use:

```text
org.amanahquran.app
```

Suggested Android package layout:

```text
org.amanahquran.app
  core
    database
    datastore
    model
    navigation
    theme
    ui
    util
  feature
    home
    reader
    search
    bookmarks
    settings
    trust
  content
    manifest
    validation
```

## V1 Module Rule

For V1, prefer a single Android application module:

```text
apps/android/app
```

Do not split into many Gradle modules unless complexity clearly requires it.

## Future Web App Placeholder

The future web app should be prepared only as a placeholder:

```text
apps/web/README.md
apps/web/.gitkeep
```

The README should say:

- Web app is future scope.
- Do not implement web features during V1.
- Future web app must follow the same content verification and no-tracking principles.

## Shared Package Rule

`/packages/shared` is for future cross-platform contracts only.

Allowed now:

- JSON schemas.
- Content manifest templates.
- Validation report templates.
- Shared naming conventions.

Not allowed now:

- Runtime shared library.
- Backend API SDK.
- Web UI code.
- Cloud sync logic.

## Tools Folder

Use `/tools` for scripts that generate or validate local content:

```text
tools/
  content-import/
  validation/
  release/
```

Rules:

- Tools may run during development.
- Tools must not modify Quran display text except by importing exact verified source text.
- Tools must generate logs/reports for validation.
- Tools must not introduce network dependency into the Android app.
