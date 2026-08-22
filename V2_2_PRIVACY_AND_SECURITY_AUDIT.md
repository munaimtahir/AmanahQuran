# V2.2 Privacy and Security Audit

- Ads/analytics/tracking/auth/billing SDK scan: no matches.
- Account/cloud-sync infrastructure: none found.
- Core Quran, translations, Daily Ayah, search, bookmarks, and activity remain local/offline.
- Manifest permissions: opt-in `POST_NOTIFICATIONS` only, required for user-enabled reminders; no location, camera, microphone, contacts, storage, or advertising ID.
- Widget receiver is explicitly exported only for the Android AppWidget broadcast contract; FileProvider remains non-exported.
- Daily Ayah widget deep links carry only a canonical `ayah_key`; no user data is transmitted.
- Audio networking is absent because audio activation is deferred.
- Backup excludes bundled Quran/translation content and remains user-controlled/local.

Status: PASS for implemented code; signed artifact/Play Console inspection remains external.
