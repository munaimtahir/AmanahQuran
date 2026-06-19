package org.amanahquran.app.feature.bookmarks

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.MainDispatcherRule
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.amanahquran.app.core.database.AmanahContentDatabase
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRepositoryImpl
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.QuranContentRepositoryImpl
import org.amanahquran.app.core.repository.ReaderSettingsRepositoryImpl
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertFalse
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
class BookmarksViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var database: AmanahContentDatabase
    private lateinit var repository: QuranContentRepository
    private lateinit var settingsRepository: ReaderSettingsRepositoryImpl
    private lateinit var bookmarkRepository: BookmarkRepositoryImpl
    private lateinit var tempFile: File

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "bookmarks-test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
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
        tempFile = File(
            RuntimeEnvironment.getApplication().filesDir,
            "amanah-bookmark-state-${System.nanoTime()}.preferences_pb",
        )
        val dataSource = amanahPreferencesDataSourceForFile(tempFile)
        settingsRepository = ReaderSettingsRepositoryImpl(dataSource)
        bookmarkRepository = BookmarkRepositoryImpl(dataSource)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun bookmarkPreviewUsesVerifiedDisplayText() = runTest {
        settingsRepository.setSelectedScript(ScriptType.UTHMANI)
        bookmarkRepository.addAyahBookmark("2:255")

        val viewModel = BookmarksViewModel(
            bookmarkRepository = bookmarkRepository,
            settingsRepository = settingsRepository,
            quranContentRepository = repository,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading }
        val item = state.items.first { it.record.ayahKey == "2:255" }
        val quranText = database.quranTextDao().getTextByAyahAndScript("2:255", "UTHMANI")
        val searchRow = database.searchIndexDao().getSearchRow("2:255")

        assertNotNull(quranText)
        assertNotNull(searchRow)
        assertEquals(quranText!!.displayText, item.previewText)
        assertFalse(item.previewText == searchRow!!.normalizedArabic)
    }

    @Test
    fun pageBookmarkRendersWithPageReferenceTypeAndVerifiedPreview() = runTest {
        settingsRepository.setSelectedScript(ScriptType.UTHMANI)
        bookmarkRepository.addPageBookmark(1, PageReferenceType.UTHMANI)

        val viewModel = BookmarksViewModel(
            bookmarkRepository = bookmarkRepository,
            settingsRepository = settingsRepository,
            quranContentRepository = repository,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { !it.isLoading }
        val item = state.items.first { it.record.bookmarkType.name == "PAGE" }
        val quranText = database.quranTextDao().getTextByAyahAndScript(item.record.ayahKey ?: "1:1", "UTHMANI")

        assertEquals(1, item.record.pageNumber)
        assertEquals(PageReferenceType.UTHMANI, item.record.pageReferenceType)
        assertNotNull(quranText)
        assertTrue(item.subtitle.contains("Uthmani"))
        assertTrue(item.previewText.orEmpty().isNotBlank())
    }
}
