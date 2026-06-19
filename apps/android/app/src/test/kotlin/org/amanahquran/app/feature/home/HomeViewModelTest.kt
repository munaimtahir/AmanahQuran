package org.amanahquran.app.feature.home

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
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRepositoryImpl
import org.amanahquran.app.core.repository.LastReadRepositoryImpl
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.QuranContentRepositoryImpl
import org.amanahquran.app.core.repository.ReaderSettingsRepositoryImpl
import org.amanahquran.app.core.repository.LastReadState
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var database: AmanahContentDatabase
    private lateinit var repository: QuranContentRepository
    private lateinit var settingsRepository: ReaderSettingsRepositoryImpl
    private lateinit var lastReadRepository: LastReadRepositoryImpl
    private lateinit var bookmarkRepository: BookmarkRepositoryImpl
    private lateinit var tempFile: File

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "home-test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
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
            "amanah-home-state-${System.nanoTime()}.preferences_pb",
        )
        val dataSource = amanahPreferencesDataSourceForFile(tempFile)
        settingsRepository = ReaderSettingsRepositoryImpl(dataSource)
        lastReadRepository = LastReadRepositoryImpl(dataSource)
        bookmarkRepository = BookmarkRepositoryImpl(dataSource)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun continueReadingAppearsWhenLastReadExists() = runTest {
        settingsRepository.setSelectedScript(ScriptType.UTHMANI)
        lastReadRepository.saveLastRead(
            LastReadState(
                ayahKey = "2:255",
                surahNumber = 2,
                ayahNumber = 255,
                pageNumber = 42,
                juzNumber = 3,
                scriptType = ScriptType.UTHMANI,
                updatedAt = 1234L,
            ),
        )

        val viewModel = HomeViewModel(
            repository = repository,
            lastReadRepository = lastReadRepository,
            settingsRepository = settingsRepository,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val state = viewModel.uiState.first { it.continueReading != null }

        assertNotNull(state.continueReading)
        assertEquals("2:255", state.continueReading!!.ayahKey)
        assertEquals(2, state.continueReading!!.surahNumber)
    }
}
