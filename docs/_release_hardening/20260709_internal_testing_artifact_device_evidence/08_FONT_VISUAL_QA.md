# Font Visual QA

## Runtime stack

- IndoPak: `digital_khatt_indopak.otf`
- Uthmani: `digital_khatt_v2.otf`
- Uthmani fallback: `indopak_nastaleeq.ttf`

## Validation

Command:

```bash
./gradlew validateQuranFonts
```

Result:
- PASS

## Device visual checks

Captured reader screens show:
- No tofu boxes on the visible search result preview for `2:255`
- No tofu boxes on the visible Ayah 2:255 reader in IndoPak
- No tofu boxes on the visible Page 540 IndoPak reader
- No tofu boxes on the visible Page 540 Uthmani reader
- No visible font-crash or glyph-collapse artifacts on the page-rendering screens
- Elder Mode preserved readable Arabic text and did not introduce clipping in the captured Settings view

## Conservative note

- The device pass did not isolate every rare fallback glyph path or every punctuation/annotation combination.
- The font coverage gate remains a hard gate, which is the correct posture for release.
