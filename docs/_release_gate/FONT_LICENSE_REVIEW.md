# Font / License Review

## Audit Result

- The packaged app bundles Quran fonts in `apps/android/app/src/main/res/font`.
- Font information is stored in the packaged database and the Trust Center asset.
- The app renders using the bundled fonts for the supported V1 scripts.
- Reference URLs and license notices are documented in the Trust Center and the release notes.

## Inventory Summary

The packaged database contains 41 `font_inventory` rows. Review status has been completed for the release-relevant fonts.

| Font / source name | File present in repo | Bundled in app | License known | License text available | Commercial / free public app use allowed | Decision | Notes |
|---|---|---|---|---|---|---|---|
| QUL font files | Yes | Yes | Yes | Yes | Internal testing only until public gates clear | Approved | Reference URLs documented in Trust Center |
| QUL IndoPak / Nastaleeq candidates | Yes | Yes | Yes | Yes | Internal testing only until public gates clear | Approved | Current runtime uses `digital_khatt_indopak.otf` with `indopak_nastaleeq.ttf` as fallback |
| QUL Uthmani font candidates | Yes | Yes | Yes | Yes | Internal testing only until public gates clear | Approved | Official font portal recorded; `indopak_nastaleeq.ttf` remains a fallback for rare marks |
| Quran metadata / ligature assets | No | No | No | No | Not bundled | Not in V1 | Kept out of the app binary |
| Quran Foundation reference pages | No | No | No | No | Not bundled | Reference only | Not part of the app package |
| Quranic Arabic Corpus morphology data | No | No | No | No | Not bundled | Not in V1 | Excluded from V1 scope |

## Policy

- Keep bundled Quran fonts documented with source URLs and license notices.
- Do not rely on implied permissibility from source location or filename alone.
- Keep non-V1 font candidates as metadata until a specific license review approves them.
- Public release remains blocked until the non-font gates and manual review evidence are complete.

## Reference Links

- [Tanzil text license](https://tanzil.net/docs/text_license)
- [Tanzil download page](https://tanzil.net/download/)
- [DigitalKhatt IndoPak font repository](https://github.com/DigitalKhatt/indopakfont)
- [DigitalKhatt IndoPak OFL license](https://github.com/DigitalKhatt/indopakfont/blob/main/LICENSE)
- [King Fahd Quran font portal](https://fonts.qurancomplex.gov.sa/)
