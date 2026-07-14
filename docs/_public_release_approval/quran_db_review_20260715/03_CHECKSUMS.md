# Packaged Asset Checksums Evidence Proof

**Date**: 2026-07-15

This document records the cryptographically verified SHA-256 checksums of all content database, trust metadata, and font files packaged into the application.

## Packaged Content Hashes

The SHA-256 checksums were calculated on the local filesystem and cross-verified against build targets:

| Packaged File Path | Kind | SHA-256 Checksum | Release Status |
| :--- | :--- | :--- | :--- |
| `apps/android/app/src/main/assets/database/quran.db` | Content Database | `cf8693ca972f5049d640556804bf06bceb3530793afbfd34de518bae6bd8d8c5` | Pending Sign-Off |
| `apps/android/app/src/main/assets/trust/trust_center_content.json` | Trust Metadata | `eb3cbb710067972da5c7eacae6e5f09e411d61357465dd4440b75ed5b3210faf` | Pending Sign-Off |
| `apps/android/app/src/main/res/font/digital_khatt_indopak.otf` | Reader Font (IndoPak) | `59a5e78c530de236a365354d558b37706f37d782f7ee95c3c9b7fe9e0fad042a` | Pending Sign-Off |
| `apps/android/app/src/main/res/font/digital_khatt_v2.otf` | Reader Font (Uthmani) | `0935c48269a57c9808e52dfae47864c189396452901c689977156036a72dd217` | Pending Sign-Off |
| `apps/android/app/src/main/res/font/indopak_nastaleeq.ttf` | Reader Font (Fallback) | `a6463e24e36651404e9eff52dae26e18e9ef0718eb620636a66a20026a75c563` | Pending Sign-Off |

## Verification Command

To verify these hashes at any time, run:
```bash
sha256sum apps/android/app/src/main/assets/database/quran.db \
          apps/android/app/src/main/assets/trust/trust_center_content.json \
          apps/android/app/src/main/res/font/digital_khatt_indopak.otf \
          apps/android/app/src/main/res/font/digital_khatt_v2.otf \
          apps/android/app/src/main/res/font/indopak_nastaleeq.ttf
```

## Security & Verification Rule

- **No Modifications**: The above checksums must match the final binary assets exactly.
- **Verification Gate**: Any change to `quran.db` or `trust_center_content.json` will invalidate these checksums and trigger a build-time packaging failure during a public release task scan.

---
**Status**: verified by SHA-256 verification CLI commands.
