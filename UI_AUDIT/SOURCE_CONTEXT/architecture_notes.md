# Architecture & UI Framework Notes

### 1. Pure Jetpack Compose Implementation
- The application UI is built 100% in declarative Jetpack Compose using Compose BOM `2026.06.01` and Kotlin `2.1.0`.
- There are zero legacy XML layout files in `res/layout`; all layouts are pure Kotlin Composables.
- Theme switching (Light, Dark, Sepia, System) and accessibility adjustments (Elder Mode) leverage CompositionLocal (`LocalElderMode`, `AmanahQuranTheme`).

### 2. Room SQLite & Asset-Packaged Content
- Quranic verses, Surah metadata, Juz partitions, page mappings, and font inventories reside in an offline pre-packaged SQLite database (`assets/database/quran.db`).
- Read queries are managed via Room DAOs (`AyahDao`, `SurahDao`, `MushafDao`, `SearchIndexDao`).
- Zero runtime network queries or cloud dependencies exist.
- Search queries execute against a pre-indexed normalized table (`SearchIndexDao`), ensuring that searching never modifies or interpolates the authentic display text.

### 3. Preferences DataStore State Persistence
- User settings (script selection, reading mode, theme mode, elder mode, font scale) are managed through `PreferenceDataStoreFactory` via `ReaderSettingsRepository`.
- Changes are exposed as Kotlin `Flow`s, delivering sub-millisecond, glitch-free recomposition.

### 4. Zero Third-Party Tracking / Ad SDKs
- Build analysis and manifest inspection confirm zero inclusions of Google Firebase Analytics, Adjust, AppsFlyer, Facebook SDK, AdMob, or any tracking dependencies.
- The app requests zero internet permissions (`android.permission.INTERNET` is absent from AndroidManifest.xml).
- Only `POST_NOTIFICATIONS` is declared for local-only reading reminder scheduling via WorkManager.
