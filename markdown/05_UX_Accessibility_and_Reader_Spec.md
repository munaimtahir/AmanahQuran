# UX, Accessibility, and Reader Specification
# Amanah Quran - V1 Sacred Reader MVP

## 1. UX Vision

Amanah Quran should feel calm, sacred, trustworthy, and easy to use. The reading experience must be free from visual noise, popups, ads, donation prompts, social mechanics, and unnecessary settings.

## 2. Information Architecture

Primary navigation:

1. Read.
2. Search.
3. Bookmarks.
4. Settings.
5. Trust Center.

Elder Mode navigation:

1. Continue Reading.
2. Open Quran.
3. Bookmarks.
4. Search.
5. Settings.

## 3. Home Screen Requirements

Required cards/actions:

1. Continue Reading.
2. Open Quran.
3. Search.
4. Bookmarks.
5. Trust Center.
6. Current script indicator.
7. Current theme-aware layout.

Acceptance criteria:

1. Continue Reading opens previous location in one tap.
2. Quran navigation opens in one tap.
3. Trust Center is accessible within two taps.
4. No ad-like placeholder, promotional banner, or donation card is shown.
5. Elder Mode increases readability and button size.

## 4. Reader Screen Requirements

Reader top bar:

1. Back button.
2. Current Surah/Juz/page label.
3. Script switch.
4. Bookmark action.
5. Reader settings action.

Reader content:

1. Arabic display text.
2. Ayah number markers.
3. Comfortable line height.
4. Optional mini progress indicator.
5. No unrelated cards.
6. No popups during reading.
7. No bottom area reserved for ads.

## 5. Arabic Typography Requirements

1. Font must support Arabic shaping correctly.
2. Diacritics must render clearly.
3. IndoPak script must be readable and familiar for Pakistan users.
4. Uthmani script must remain visually authentic.
5. Line height must prevent collision of diacritics.
6. Font size must be adjustable.
7. Elder Mode must use larger defaults.
8. Avoid letter spacing that damages Arabic shaping.
9. Avoid decorative backgrounds behind Quran text.
10. Test on common OEMs.

## 6. Theme Requirements

Required themes:

1. System.
2. Light.
3. Dark.
4. Sepia / Paper.

Theme principles:

1. Reading comfort is more important than decorative design.
2. Dark mode should not use harsh pure-white text if it causes fatigue.
3. Sepia should support long recitation sessions.
4. Ayah numbers and controls must maintain contrast.
5. Elder Mode must work with every theme.

## 7. Elder Mode Specification

When enabled:

1. Home cards become larger.
2. Reader font becomes larger.
3. Line spacing increases.
4. Tap targets increase.
5. Navigation labels become more explicit.
6. Icon-only actions should gain labels where space allows.
7. Settings screen should simplify choices.
8. Search input becomes larger.
9. Bookmark action remains obvious.
10. Trust Center text remains readable.

Recommended Elder Mode defaults:

| Element | Elder Mode Behavior |
|---|---|
| Arabic font size | Larger default |
| UI text | Increased size |
| Buttons | Large, clear labels |
| Touch targets | Minimum 48dp, preferably larger |
| Reader controls | Fewer visible controls |
| Search field | Tall field, clear placeholder |
| Theme | High readability with system default respected |

## 8. Search UX

Search screen should support:

1. Simple text box.
2. Reference lookup examples.
3. Results grouped by type if useful: Surah, Ayah, Juz, Page.
4. Arabic ayah preview in selected script.
5. Tap result to open reader.

Avoid:

1. Complex filters in V1.
2. Cloud search.
3. Search suggestions requiring internet.
4. Highlighting that distorts Quran text.

## 9. Bookmark UX

Bookmarks screen should show:

1. Bookmark type: Ayah or Page.
2. Surah/page reference.
3. Short preview in selected script.
4. Date added.
5. Open action.
6. Remove action.

Empty state:

> No bookmarks yet. Open any ayah or page and tap Bookmark to save your place.

## 10. Trust Center UX

Trust Center should feel transparent and reassuring, not technical overload. Show summary first, then details.

Recommended structure:

1. Trust Summary.
2. Quran Text Sources.
3. Verification Status.
4. Privacy Pledge.
5. App and Database Integrity.
6. Detailed metadata.

## 11. First Launch

Do not use long onboarding. First launch may show a simple non-blocking statement:

> Amanah Quran is free, offline, ad-free, and built without tracking. Quran text sources and verification details are available in the Trust Center.

Do not ask for:

1. Login.
2. Permissions.
3. Donations.
4. Notifications.
5. Personal information.

## 12. Accessibility Requirements

1. Support Android font scaling where safe.
2. Provide content descriptions for major actions.
3. Use text labels where icons may be unclear.
4. Maintain contrast in all themes.
5. Keep touch targets accessible.
6. Support predictable back navigation.
7. Do not rely only on color for state.
8. Avoid motion-heavy transitions.
9. Test TalkBack for basic screen navigation.
10. Avoid tiny Arabic controls or low-contrast ayah markers.

## 13. Small-Screen Requirements

On small phones:

1. Reader controls must not overlap.
2. Font size controls must remain usable.
3. Elder Mode must not hide critical actions.
4. Long Surah names should truncate gracefully.
5. Bookmark and script switch actions should remain reachable.

## 14. UX Anti-Patterns to Avoid

1. Home screen overloaded with future features.
2. Donation prompt on launch.
3. Popup during recitation.
4. Social sharing feed.
5. Streaks or badges that trivialize Quran reading.
6. Ads-like empty spaces.
7. Too many first-run choices.
8. Mixing Study/Hifz/Masjid features into V1 navigation.
