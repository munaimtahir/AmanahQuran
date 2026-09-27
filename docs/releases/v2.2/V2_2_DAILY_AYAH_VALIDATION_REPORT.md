# V2.2 Daily Ayah Validation Report

- Scope: Text-only (no audio dependencies).
- Algorithm: Deterministic pseudo-random selection (`DailyAyahSelector.randomDailyKey`) using SplitMix64 hash over `date.toEpochDay()`.
- Distribution: Uniform, pseudo-random distribution across all 6,236 canonical Quran ayahs.
- Same local date: persisted record is reused (idempotent for the day).
- Process death/reboot: state is persisted in local DataStore.
- Timezone: selection uses caller's local `ZoneId` and local calendar date.
- Anti-repeat: rolling 30-day history exclusion window to avoid recent repeats.
- Canonical retrieval: content is loaded strictly by canonical `ayah_key` (`surah:ayah`) from the verified Quran DB and translation DB without modifying Quran text.
- History: newest 30 records retained; no cloud tracking or redundant favorites.
- Widget: uses the same repository and exact ayah deep link.

Automated selector tests and instrumented emulator tests passed (100%).

