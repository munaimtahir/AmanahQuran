# Phase 7 Script Setting Report

## Existing Settings Check

- No DataStore-backed settings repository exists in the current app.
- Existing settings screen is a placeholder.

## Implemented For This Sprint

- Reader script selection is local to `ReaderViewModel`.
- Default script is IndoPak.
- Switching scripts preserves the current Surah and ayah identity.

## Deferred

- Persist selected script with DataStore when the settings architecture is introduced.
- Add DataStore tests at that time.

## Guardrails

- No complex settings architecture was added for this sprint.
- No runtime permissions or network dependencies were added.
