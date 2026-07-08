# Next Sprint Recommendation

## Sprint goal

Fix the release wiring bug and rerun the full release evidence flow without changing Quran content.

## Priority order

1. Fix the bad content-pipeline path in `apps/android/app/build.gradle.kts`.
2. Re-run `assembleRelease` and the content pipeline checks.
3. Re-run the fresh physical-device regression folder.
4. Re-test exact ayah anchors:
   - search `2:255`
   - bookmark `2:255`
   - Continue Reading
5. Re-test `Page 540` in both scripts.
6. Re-capture Juz 30 boundary evidence.
7. Reconcile Trust Center wording with the live asset and legal evidence.
8. Confirm manual reviewer/scholar sign-off fields are complete.
9. Re-check font coverage warnings and note any real tofu risk.
10. Prepare Play Store metadata and screenshots only after the above pass.

## What not to do next

- Do not add translations, tafsir, audio, AI, login, ads, tracking, or cloud sync.
- Do not modify Quran display text.
- Do not use normalized text as display text.

