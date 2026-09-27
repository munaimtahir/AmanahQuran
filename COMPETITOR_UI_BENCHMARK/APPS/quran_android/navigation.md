# Quran for Android — Information Architecture & Navigation Map

- **Home Surface**: 3-tab layout (`SURAHS`, `JUZ'`, `BOOKMARKS`) with top action bar (Search, Overflow Menu).
- **Reader Surface (`PagerActivity`)**:
  - Immersive full-screen Mushaf page viewer (`androidx.viewpager.widget.ViewPager`).
  - Single tap toggles Top Toolbar (Navigate Up, Surah Name, Subtitle "Page X, Juz' Y", Bookmarks toggle, Show Translation toggle, More Options) and Bottom Audio Bar (Play button, Reciter selector).
  - RTL Swipe flips pages forward/backward.
  - Long press on ayah triggers ayah selection overlay and action modal (Play, Bookmark, Share, Translation).
- **Search Surface (`SearchActivity`)**: Full-screen search with search bar and results list.
- **Translations Manager (`TranslationManagerActivity`)**: Network catalog of downloadable translation packages.
