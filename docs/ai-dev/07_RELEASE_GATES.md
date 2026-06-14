# 07 — Release Gates

Amanah Quran V1 cannot be released unless every gate below passes.

## Product Scope Gate

- Sacred Reader MVP only.
- No out-of-scope features.
- No feature creep into audio, translation, tafsir, AI, prayer times, qibla, hadith, social, donations, accounts, or cloud sync.

## Content Gate

- 114 Surahs present.
- 6236 ayahs present.
- IndoPak script verified.
- Uthmani script verified.
- No missing ayah keys.
- No duplicate ayah keys.
- Display text source documented.
- Search-normalized text stored separately.
- Checksums generated.
- Content manifest complete.
- Manual content review completed.

## Trust Center Gate

Trust Center must show:

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
- Privacy pledge.
- App/content version.

## Privacy Gate

- No ads.
- No ad SDK.
- No analytics SDK.
- No tracking.
- No login.
- No account system.
- No cloud sync.
- No advertising ID.
- No data collection.
- No data sharing.
- No unnecessary permissions.

## Offline Gate

With airplane mode enabled, user can:

- Open app.
- Read Quran.
- Switch script.
- Search.
- Add/remove bookmark.
- Resume last-read.
- Open Settings.
- Open Trust Center.
- Change theme.
- Use Elder Mode.

## Android Gate

- Release build succeeds.
- Min SDK and target SDK selected intentionally.
- App runs on target OEM devices:
  - Samsung.
  - Vivo.
  - Oppo.
  - Xiaomi/Redmi.
  - Infinix/Tecno if available.
- Small-screen layout tested.
- Elder Mode tested.
- Dark/Sepia themes tested.

## Dependency Gate

Build files contain no:

- Ad SDK.
- Analytics SDK.
- Auth SDK.
- Social SDK.
- Cloud sync SDK.
- Push SDK.

## Play Store Data Safety Gate

The app design should allow truthful declaration:

- No data collected.
- No data shared.
- No location collected.
- No personal information collected.
- No analytics collected.
- No advertising data collected.

## Final Release Checklist

Before release, complete:

- `/docs/ai-dev/templates/release_checklist.template.md`
- Content validation report.
- Sprint handoff report.
- Manual device QA report.
