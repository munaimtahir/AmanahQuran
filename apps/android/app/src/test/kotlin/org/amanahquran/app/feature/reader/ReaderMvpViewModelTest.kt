package org.amanahquran.app.feature.reader

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.MainDispatcherRule
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.amanahquran.app.core.database.AmanahContentDatabase
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ReaderAnchor
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRepository
import org.amanahquran.app.core.repository.BookmarkRepositoryImpl
import org.amanahquran.app.core.repository.LastReadRepository
import org.amanahquran.app.core.repository.LastReadRepositoryImpl
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.QuranContentRepositoryImpl
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepositoryImpl
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReaderMvpViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var database: AmanahContentDatabase
    private lateinit var repository: QuranContentRepository
    private lateinit var settingsRepository: ReaderSettingsRepository
    private lateinit var lastReadRepository: LastReadRepository
    private lateinit var bookmarkRepository: BookmarkRepository
    private lateinit var preferencesTempFile: File

    @Before
    fun createRepository() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferencesTempFile = File(
            RuntimeEnvironment.getApplication().filesDir,
            "amanah-reader-state-${System.nanoTime()}.preferences_pb",
        )
        val dataSource = amanahPreferencesDataSourceForFile(preferencesTempFile)
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "reader-test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
        )
            .createFromAsset(AmanahContentDatabase.ASSET_PATH)
            .allowMainThreadQueries()
            .build()

        repository = QuranContentRepositoryImpl(
            surahDao = database.surahDao(),
            ayahDao = database.ayahDao(),
            quranTextDao = database.quranTextDao(),
            mushafLayoutReferenceDao = database.mushafLayoutReferenceDao(),
        )
        settingsRepository = ReaderSettingsRepositoryImpl(dataSource)
        lastReadRepository = LastReadRepositoryImpl(dataSource)
        bookmarkRepository = BookmarkRepositoryImpl(dataSource)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun surahList_loads114Surahs() = runTest {
        val viewModel = SurahListViewModel(
            repository = repository,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertEquals(114, state.surahs.size)
        assertEquals(1, state.surahs.first().surahNumber)
        assertEquals(114, state.surahs.last().surahNumber)
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun reader_openingSurah1ContinuesThroughTheEndOfTheQuranNotJustItsOwn7Ayahs() = runTest {
        // READER-UX-02: a bare Surah open (Surah Index / "Continue Reading" -- no specific target
        // ayah) reads continuously through to the end of the Quran instead of dead-ending at that
        // Surah's own last ayah, so scrolling past Al-Fatihah's 7th ayah flows into Al-Baqarah
        // rather than stopping (this also fixed a real reported bug: with the old exact-surah-only
        // query, enabling Page Mode made "open Surah 1" silently jump to "Page 1", which legitimately
        // also contains Al-Baqarah 2:1-2:2 on a real Mushaf page and looked like Surah 2 bleeding
        // into Surah 1).
        val totalAyahCount = repository.getAyahCount()
        val viewModel = ReaderViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            lastReadRepository = lastReadRepository,
            bookmarkRepository = bookmarkRepository,
            initialOpenMode = ReaderOpenMode.Surah(1),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertEquals(1, state.surahNumber)
        assertEquals(ScriptType.INDOPAK, state.selectedScript)
        assertEquals(totalAyahCount, state.ayahs.size)
        assertEquals("1:1", state.ayahs.first().ayahKey)
        assertEquals(114, state.ayahs.last().surahNumber)
        assertTrue(state.ayahs.any { it.ayahKey == "2:1" })
        assertTrue(state.ayahs.all { it.displayText.isNotBlank() })
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun reader_bareSurahOpenWithPageModeEnabledStaysListScopedNotThePageThatBleedsIntoTheNextSurah() = runTest {
        // The exact scenario reported as a bug: Page Mode globally on, user opens "Surah 1" from
        // the Surah Index (no specific ayah -- anchor is SurahStart). This must NOT redirect into
        // ReaderOpenMode.Page (which would show Al-Baqarah 2:1/2:2 on the same physical page).
        settingsRepository.setBookModeEnabled(true)
        val viewModel = ReaderViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            lastReadRepository = lastReadRepository,
            bookmarkRepository = bookmarkRepository,
            initialOpenMode = ReaderOpenMode.Surah(1),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading }

        assertEquals(ReaderOpenMode.Surah(1), state.openMode)
        assertEquals("1:1", state.ayahs.first().ayahKey)
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun reader_loadsSurah2WithUthmaniTextAndContinuesPastItsOwnLastAyah() = runTest {
        settingsRepository.setSelectedScript(ScriptType.UTHMANI)
        val expectedCount = repository.getAyahCount() - (repository.getSurahByNumber(1)?.ayahCount ?: 7)
        val viewModel = ReaderViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            lastReadRepository = lastReadRepository,
            bookmarkRepository = bookmarkRepository,
            initialOpenMode = ReaderOpenMode.Surah(2),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertEquals(ScriptType.UTHMANI, state.selectedScript)
        assertEquals(expectedCount, state.ayahs.size)
        assertEquals(
            database.quranTextDao().getTextByAyahAndScript("2:1", "UTHMANI")?.displayText,
            state.ayahs.first().displayText,
        )
        assertEquals(114, state.ayahs.last().surahNumber)
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun reader_loadsSurah2WithIndoPakTextAndContinuesPastItsOwnLastAyah() = runTest {
        val expectedCount = repository.getAyahCount() - (repository.getSurahByNumber(1)?.ayahCount ?: 7)
        val viewModel = ReaderViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            lastReadRepository = lastReadRepository,
            bookmarkRepository = bookmarkRepository,
            initialOpenMode = ReaderOpenMode.Surah(2),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertEquals(ScriptType.INDOPAK, state.selectedScript)
        assertEquals(expectedCount, state.ayahs.size)
        assertEquals(
            database.quranTextDao().getTextByAyahAndScript("2:1", "INDOPAK")?.displayText,
            state.ayahs.first().displayText,
        )
        assertEquals(114, state.ayahs.last().surahNumber)
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun reader_loadsPage1WithUthmaniText() = runTest {
        settingsRepository.setSelectedScript(ScriptType.UTHMANI)
        val viewModel = ReaderViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            lastReadRepository = lastReadRepository,
            bookmarkRepository = bookmarkRepository,
            initialOpenMode = ReaderOpenMode.Page(1, PageReferenceType.UTHMANI),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading }

        assertEquals("Page 1", state.surahName)
        assertEquals(9, state.ayahs.size)
        assertEquals(PageReferenceType.UTHMANI, state.openMode.let { (it as ReaderOpenMode.Page).pageReferenceType })
        assertEquals(
            database.quranTextDao().getTextByAyahAndScript("1:1", "UTHMANI")?.displayText,
            state.ayahs.first().displayText,
        )
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun scriptSwitch_preservesAyahKeys() = runTest {
        val indopakKeys = repository.getAyahsForSurah(2, ScriptType.INDOPAK.name).map { it.ayahKey }
        val uthmaniKeys = repository.getAyahsForSurah(2, ScriptType.UTHMANI.name).map { it.ayahKey }

        assertEquals(indopakKeys, uthmaniKeys)
        assertEquals(286, indopakKeys.size)
    }

    @Test
    fun sampleAyah2255_loadsInBothScripts() = runTest {
        val uthmani = repository.getAyahDisplay("2:255", ScriptType.UTHMANI.name)
        val indopak = repository.getAyahDisplay("2:255", ScriptType.INDOPAK.name)

        assertNotNull(uthmani)
        assertNotNull(indopak)
        assertEquals("2:255", uthmani!!.ayahKey)
        assertEquals("2:255", indopak!!.ayahKey)
        assertTrue(uthmani.displayText.isNotBlank())
        assertTrue(indopak.displayText.isNotBlank())
        assertEquals(
            database.quranTextDao().getTextByAyahAndScript("2:255", "UTHMANI")?.displayText,
            uthmani.displayText,
        )
        assertEquals(
            database.quranTextDao().getTextByAyahAndScript("2:255", "INDOPAK")?.displayText,
            indopak.displayText,
        )
    }

    @Test
    fun exactAyahAnchorLoadsContainingSurahAndScrollsToTargetAyah() = runTest {
        val expectedCount = repository.getAyahCount() - (repository.getSurahByNumber(1)?.ayahCount ?: 7)
        val viewModel = ReaderViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            lastReadRepository = lastReadRepository,
            bookmarkRepository = bookmarkRepository,
            initialOpenMode = ReaderOpenMode.Surah(2),
            initialAnchor = ReaderAnchor.ExactAyah("2:255"),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading && it.selectedAyahKey == "2:255" }

        assertEquals(ReaderOpenMode.Surah(2), state.openMode)
        assertEquals(expectedCount, state.ayahs.size)
        assertEquals("2:1", state.ayahs.first().ayahKey)
        assertTrue(state.ayahs.any { it.ayahKey == "2:255" })
        assertEquals("2:255", state.selectedAyahKey)
        assertTrue(state.anchorScrollIndex != null && state.anchorScrollIndex!! >= 0)
        val targetBlock = state.readerBlocks[state.anchorScrollIndex!!] as ReaderStructuralItem.Ayah
        assertEquals("2:255", targetBlock.ayah.ayahKey)
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun exactAyahAnchorInBookModeLoadsContainingPageAndKeepsSelectedAyah() = runTest {
        settingsRepository.setBookModeEnabled(true)
        val viewModel = ReaderViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            lastReadRepository = lastReadRepository,
            bookmarkRepository = bookmarkRepository,
            initialOpenMode = ReaderOpenMode.Surah(2),
            initialAnchor = ReaderAnchor.ExactAyah("2:255"),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading && it.selectedAyahKey == "2:255" }

        val expectedPage = repository.getPageForAyah("2:255", PageReferenceType.INDOPAK)
        assertEquals(ReaderOpenMode.Page(expectedPage ?: 1, PageReferenceType.INDOPAK), state.openMode)
        assertEquals("2:255", state.selectedAyahKey)
        assertTrue(state.ayahs.any { it.ayahKey == "2:255" })
        assertTrue(state.anchorScrollIndex != null)
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun exactAyahAnchorSurvivesScriptSwitch() = runTest {
        val viewModel = ReaderViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            lastReadRepository = lastReadRepository,
            bookmarkRepository = bookmarkRepository,
            initialOpenMode = ReaderOpenMode.Surah(2),
            initialAnchor = ReaderAnchor.ExactAyah("2:255"),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )
        viewModel.uiState.first { !it.isLoading && it.selectedAyahKey == "2:255" }

        settingsRepository.setSelectedScript(ScriptType.UTHMANI)
        val switched = viewModel.uiState.first {
            !it.isLoading && it.selectedScript == ScriptType.UTHMANI && it.selectedAyahKey == "2:255"
        }

        assertEquals(ReaderAnchor.ExactAyah("2:255"), switched.anchor)
        assertEquals("2:255", switched.selectedAyahKey)
        assertTrue(switched.anchorScrollIndex != null)
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun searchIndex_isNotUsedForReaderDisplay() = runTest {
        val readerAyah = repository.getAyahDisplay("1:1", ScriptType.INDOPAK.name)
        val searchRow = database.searchIndexDao().getSearchRow("1:1")
        val quranText = database.quranTextDao().getTextByAyahAndScript("1:1", "INDOPAK")

        assertNotNull(readerAyah)
        assertNotNull(searchRow)
        assertNotNull(quranText)
        assertEquals(quranText!!.displayText, readerAyah!!.displayText)
        assertFalse(readerAyah.displayText == searchRow!!.normalizedArabic)
    }
}
