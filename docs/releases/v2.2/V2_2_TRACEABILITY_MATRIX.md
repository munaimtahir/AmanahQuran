# Amanah Quran V2.2 Traceability Matrix

| Feature | Status | Evidence / gap |
|---|---|---|
| Verified English translation | IMPLEMENTED | Dual translation asset, Room schema, integrity report |
| Verified Urdu translation | IMPLEMENTED | Dual translation asset, Room schema, integrity report |
| Reader 2.0 translation integration | IMPLEMENTED | Continuous/Ayah reader and translation UI/tests |
| Daily Ayah engine | NEEDS_REFACTOR | No single persisted source of truth found |
| Daily Ayah widget | ABSENT | No AppWidget receiver/resources found |
| Supported lock-screen surface | BLOCKED_HUMAN_REVIEW | Platform/OEM capability varies; native supported surfaces only |
| Daily Ayah authentic audio-lite | BLOCKED_HUMAN_REVIEW | No approved reciter/source/license in repository evidence |
| Reading activity engine | IMPLEMENTED | `ReadingActivityRepository`, tracker, aggregation tests |
| Reading streak | IMPLEMENTED | Streak calculator/ViewModel/tests |
| Reading calendar | IMPLEMENTED | Calendar screen/ViewModel/tests |
| Smart reminders | IMPLEMENTED | Opt-in WorkManager scheduler/worker/tests |
| Reading statistics | IMPLEMENTED | Dashboard/aggregator/tests |
| Reading goals | PARTIAL | Dashboard exists; persistent goal model/UI needs completion |
| Reading presets | ABSENT | No preset model or setting found |
| Daily Ayah history | ABSENT | No Daily Ayah history model/screen found |
| Personal reading history | PARTIAL | Activity records exist; dedicated history surface needs completion |
| Home 2.0 | IMPLEMENTED | Existing dominant Continue Reading home hierarchy |
| Search 2.0 | IMPLEMENTED | Unified offline Quran/translation/reference search and tests |
| Elder Mode 2.0 | PARTIAL | Existing reader/settings coverage; new surfaces require propagation |
| Trust Center 2.0 | IMPLEMENTED | Local source metadata and checksum verification |
| Expanded backup/restore | IMPLEMENTED | Versioned local codec and restore validation |
| Translation-aware bookmarks/search/interactions | IMPLEMENTED | Canonical ayah joins and translation-aware search |
| Privacy/no tracking/no monetization | IMPLEMENTED | Dependency/manifest audits and offline architecture |
| Release build evidence | PARTIAL | Debug baseline passed; release gate rerun required |
| Physical-device validation | BLOCKED_HUMAN_REVIEW | Availability must be checked; no assumption of physical testing |
