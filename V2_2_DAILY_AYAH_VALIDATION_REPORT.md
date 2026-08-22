# V2.2 Daily Ayah Validation Report

- Same local date: persisted record is reused.
- Process death/reboot: state is in local DataStore.
- Timezone: selection uses the caller's local `ZoneId` and local calendar date.
- Anti-repeat: reviewed/random selection excludes recent keys when candidates exist.
- Canonical retrieval: content is loaded by `ayah_key` from the verified Quran DB and translation DB.
- History: newest 30 records are retained; no redundant favorites system is created.
- Default mode: deterministic sequential fallback because no scholarly curated pool is present.
- Widget: uses the same repository and exact ayah deep link.

Automated selector tests passed as part of the 270-test unit suite.
