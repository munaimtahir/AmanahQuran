# V2.2 Automated Gate Report

| Gate | Result | Evidence |
|---|---|---|
| Quran/translation preservation | PASS | Existing integrity evidence plus preservation report |
| Daily Ayah canonical selection | PASS | 3 selector tests; full suite 270 tests |
| Room/schema compilation | PASS | Debug build and KSP pass |
| Reader/search/activity regression | PASS | Existing suite included in 270 tests |
| Debug compilation | PASS | `assembleDebug` |
| Unit tests | PASS | 270 tests completed |
| Lint | PASS | `lintDebug` |
| Public signed release | DEFERRED | Missing signing credentials and ignored pipeline source workspace |
| Physical device | DEFERRED | No device/AVD available |

No assertion-only gate is marked PASS; each PASS has a command or tracked evidence source.
