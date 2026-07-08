# Search Status

Internal testing status only.
Public release status: blocked.

Current evidence:

- Offline search screen and several queries worked.
- `2:255` returned a result but opened `2:1`.
- `Yaseen` returned no result while `36` returned Ya-Sin.

Required before release:

- Fix exact ayah result opening.
- Add or verify common Surah aliases such as `Yaseen`, `Ya-Sin`, and `Yasin`.
- Keep normalized search text separate from Quran display text.
- Re-test search offline on a physical device.
