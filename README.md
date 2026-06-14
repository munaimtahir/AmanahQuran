# Amanah Quran

**Project identity:** Amanah-e-Kisa  
**Public app name:** Amanah Quran  
**V1 focus:** Sacred Reader MVP  
**Primary platform:** Android  
**Future platform:** Web placeholder only

This repository is being organized for an Android-first release of Amanah Quran while keeping the layout ready for a future web app.

## Current layout

- `docs/` contains the main project documentation pack and must stay untouched during scaffold work.
- `apps/android/` will hold the Android V1 app.
- `apps/web/` is a placeholder for future web work only.
- `packages/shared/` is reserved for documentation and schema contracts only.
- `tools/` is reserved for local import, validation, and release helpers.

## V1 guardrails

- No ads.
- No analytics.
- No tracking.
- No login.
- No cloud sync.
- No donation popups.
- No in-app purchases.
- No unnecessary permissions.
- Quran display text must remain immutable and verified.
- Search-normalized text must stay separate from display text.
- Core reader features must work offline.

## Documentation

The implementation guidance lives in `docs/` and `docs/ai-dev/`.

