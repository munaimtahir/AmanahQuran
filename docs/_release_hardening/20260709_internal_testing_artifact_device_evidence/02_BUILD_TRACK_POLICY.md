# Build Track Policy

## Policy selected

Implemented policy: property-driven release tracks.

### Public track

- Default when `amanahReleaseTrack` is omitted.
- Used for public-release validation and packaging.
- Must fail while any public-release blocker remains.
- Must continue to reject:
  - `quran.db`
  - `trust_center_content.json`
  - packaged fonts that are not public-release approved

### Internal track

- Enabled with `-PamanahReleaseTrack=internal`.
- Intended for closed/internal QA only.
- May package review-required assets.
- Must remain clearly labeled as internal testing only.

## Commands

Public strict:

```bash
./gradlew assembleRelease -PamanahReleaseTrack=public
```

Internal testing:

```bash
./gradlew assembleRelease -PamanahReleaseTrack=internal
```

## Guardrails preserved

- Public release gate remains strict.
- Scan still blocks public approval when content assets are not approved.
- No Quran display text was modified.
- No Trust Center wording was softened.
