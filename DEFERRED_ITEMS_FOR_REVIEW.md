# Deferred Items for Review

This is the single authoritative V2.2 deferment ledger.

## Status Summary

- **V22-AUDIO-001**: **RESOLVED / CLOSED** (User directed Daily Ayah to remain strictly text-only; no audio dependencies).
- **V22-CURATED-001**: **RESOLVED / CLOSED** (User approved deterministic pseudo-random selection across the full 6,236 canonical Quran corpus with SplitMix64 bit-mixing and rolling 30-day anti-repeat window).
- **V22-DEVICE-001**: **RESOLVED / CLOSED** (Automated device-level UI instrumentation suite executed and PASSED 100% on connected `Android_15_Test` [API 35] and `Android_26_Test` [API 26] AVDs).
- **V22-SIGNING-001**: **PENDING PRODUCTION RELEASE PIPELINE** (Standard secret release keystore not checked into public source tree).

---

## V22-SIGNING-001

- ID: V22-SIGNING-001
- Phase: 27 — Release artifacts
- Feature: Signed release APK/AAB
- Reason: Production signing credentials (`keystore.properties` / JKS) are kept in secure CI/CD environment secrets.
- Work completed: Clean debug assembly (`assembleDebug`), 272 unit tests, clean lint (`lintDebug`), and release content validation (`validateReleaseContent`) 100% PASS.
- Remaining action: Run production build pipeline with release keystore.
- Release blocking: Only for signed Play Store bundle publishing; all code, content, and engineering gates are 100% GO.

