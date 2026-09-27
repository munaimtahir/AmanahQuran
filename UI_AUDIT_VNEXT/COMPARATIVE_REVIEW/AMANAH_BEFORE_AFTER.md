# Amanah Quran — Before / After (UI Refinement R1–R5)

"Before" = v2.2.0 plus the PR #1 waves (merge `e69bf23`). "After" = this refinement.

| Surface | Before | After |
|---|---|---|
| Reader text size | Sheet sliders wrote on every drag tick and lost the reading position; linked-size toggle unreachable | Sizes apply on release with the reading position kept; "Link Arabic and translation size" switch in the sheet; reset button |
| Reader chrome | Toolbar inside the layout, so the text jumped when it hid; tap-to-hide only worked in gaps between ayahs | Toolbar floats over the text (no jump); hides when scrolling forward, returns on scroll up, any tap, or at the top; never auto-hides in Elder Mode or with TalkBack |
| Reader settings sheet | "Manifest En / Irfan Ur" labels, fixed rows that overflowed | Same labels as Settings, wrapping chips, Elder-sized targets, scrolls at 200% font scale |
| Jump to ayah | Ayah number within the loaded surah only | Tap the reader title (or the ayah counter): `2:255` or an ayah number; opens other surahs; clear error for invalid refs |
| Ayah actions | Icon-only card that could sit behind the navigation bar | Labelled Bookmark / Share, overflow for image share and reports, clear of the navigation bar, Elder-sized |
| Discoverability | No hint that ayahs are tappable | One-time hint after the pinch hint |
| Home | Search and Bookmarks below Streak and Daily Ayah | Browse grid directly under Continue Reading; Elder Mode lists Search and Bookmarks first |
| Bookmarks | Last-read position only mentioned in copy | "Reading position" section (auto-saved) above "Saved bookmarks" |
| Urdu translation | 1.65× line height; italic "missing" text had fixed 20sp leading (clipped at large sizes) | Nastaliq 1.85×, English 1.65×, never under 1.4× |
| Themes | System / Light / Dark / Sepia | Adds optional **Black (OLED)** |
| Settings | No preview | Live preview of script, sizes and spacing using verified ayah 1:2 |
| Advanced settings (Elder Mode) | Half-width chips, small switch target | Full-width chips in Elder Mode; whole row toggles |
| Search keyboard action | Opened the first of many results | Opens directly only when there is exactly one result |
| Daily Ayah history | Showed a translation even with translation Off | Follows the current translation setting |
