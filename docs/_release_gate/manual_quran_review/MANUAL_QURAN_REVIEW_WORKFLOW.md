# Manual Quran Review Workflow

## 1. Who Should Review

- A qualified Quran text reviewer with Arabic reading ability.
- A second reviewer for confirmation of any suspected issue.
- A project maintainer to apply approved source-only corrections.

## 2. What They Should Review

- `quran_texts.display_text` for both Uthmani and IndoPak scripts.
- Critical ayahs, surah openings, and a random sample.
- Any verse flagged by the reviewer as suspicious.
- Trust Center source attribution against the packaged metadata.

## 3. How to Mark Pass / Fail

- `Pass`: display text matches the verified source pack and the row is acceptable.
- `Fail`: display text differs from the verified source pack or a source issue is suspected.
- `Needs follow-up`: reviewer cannot confirm and wants a second pass.

## 4. How to Report Suspected Text Issues

- Record the ayah key.
- Record the exact observed difference.
- Record the source pack being compared.
- Attach a screenshot or excerpt from the source document if available.
- Do not edit the packaged DB directly.

## 5. How to Handle Source Comparison

- Compare against the verified upstream source file for the same script.
- Confirm whether the issue exists in the source or only in an import/export step.
- Confirm whether the issue is a display, mapping, or source problem.

## 6. Correction Workflow

1. Issue reported.
2. Source checked.
3. Second reviewer confirms.
4. Correction approved.
5. DB regenerated from the source pipeline only.
6. Validation rerun.
7. Android import rerun.
8. Device validation rerun if needed.

## 7. Non-Negotiable Rule

- No direct manual DB editing is allowed for Quran text corrections.

## 8. Release Rule

- Public release remains blocked until manual review is signed off.
