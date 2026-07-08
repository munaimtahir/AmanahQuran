# Bookmarks and Last-Read Status

Internal testing only.
Public release status: blocked.

Current evidence:

- Page bookmark add/open/remove passed for Page 540.
- Ayah bookmark for `2:255` opened `2:1`, not the selected ayah.
- Continue Reading Page 540 took approximately 13.7 seconds.

Required before release:

- Fix ayah bookmark destination anchoring.
- Fix Continue Reading performance and script-state restore.
- Re-test bookmarks and last-read on a physical device.
