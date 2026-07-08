# Current Reader Routes

Date: 2026-06-25

## Entry Paths Before This Fix

| Entry | Route | Parameters | Observed behavior |
| --- | --- | --- | --- |
| Surah list | `reader/surah/{surahNumber}` | `surahNumber` | Opens Surah start |
| Juz list | `reader/juz/{juzNumber}` | `juzNumber` | Opens Juz start |
| Page list | `reader/page/{pageNumber}/{pageReferenceType}` | page and page layout | Opens Page start |
| Search Surah | Surah route | `surahNumber` | Opens Surah start |
| Search exact ayah | `reader/surah/{surahNumber}?ayahKey={ayahKey}` | Surah and canonical key | Route log retains key, reader displays Surah start |
| Ayah bookmark | same query route | stored canonical `ayahKey` | Route log retains key, reader displays Surah start |
| Page bookmark | Page route | page and layout | Opens correct page |
| Continue Reading | `reader/mushaf/{pageNumber}` | page only | Ignores stored canonical ayah and stored script |
| Last-read restore | Home card to Mushaf route | page only | Uses separate prototype Mushaf path |

## Root Cause: Exact Ayah Opens At Surah Start

Two defects combine:

1. The plain Surah route is registered before the query-parameter Surah route. Navigation can match `reader/surah/2?ayahKey=2:255` to the plain route, so `ayahKey` is not delivered to the destination.
2. Even when `initialAyahKey` reaches `ReaderViewModel`, the standard `LazyColumn` has no `LazyListState` or `scrollToItem` logic. The selected ayah is marked in state, but the list remains at item zero.

The repository already stores canonical bookmark and last-read identity correctly. The failure is at route selection and presentation anchoring.

## Root Cause: Continue Reading Delay And Stale Script

Home sends only `pageNumber` to the separate Mushaf route. `MushafReaderViewModel` then:

1. Starts prototype line-data initialization.
2. Reads settings asynchronously.
3. Starts a ViewModel page load.
4. Independently starts another page load in each pager item.
5. Observes settings and starts another load when script state changes.

This permits stale IndoPak work to begin before Uthmani state is applied and duplicates repository work. Device logs showed the stale composition surviving for approximately 13.4 seconds.

## Canonical Route Model

All reader entry paths will use `ReaderAnchor`:

- `SurahStart(surahNumber)`
- `ExactAyah(ayahKey)`
- `PageStart(pageNumber, pageReferenceType)`
- `JuzStart(juzNumber)`

`ExactAyah` resolves metadata from `ayahs`, loads the containing Surah in the selected display script, selects the exact canonical key, and scrolls the structural reader list to that ayah. It never changes Quran display text.
