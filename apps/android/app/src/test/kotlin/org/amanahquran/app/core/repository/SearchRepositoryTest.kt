package org.amanahquran.app.core.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.core.database.AmanahContentDatabase
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SearchRepositoryTest {
    private lateinit var context: Context
    private lateinit var database: AmanahContentDatabase
    private lateinit var repository: SearchRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "search-test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
        )
            .createFromAsset(AmanahContentDatabase.ASSET_PATH)
            .allowMainThreadQueries()
            .build()
        repository = SearchRepositoryImpl(
            searchIndexDao = database.searchIndexDao(),
            quranTextDao = database.quranTextDao(),
            surahDao = database.surahDao(),
            ayahDao = database.ayahDao(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun surahNumberSearchReturnsSurahResult() = runTest {
        val results = repository.search("Surah 2", ScriptType.UTHMANI)

        assertTrue(results.any { it.resultType == SearchResultType.SURAH && it.surahNumber == 2 })
    }

    @Test
    fun ayahReferenceSearchReturnsCorrectAyahIdentity() = runTest {
        val results = repository.search("2:255", ScriptType.UTHMANI)

        val result = results.first { it.ayahKey == "2:255" }
        assertEquals(SearchResultType.AYAH, result.resultType)
        assertEquals(2, result.surahNumber)
        assertEquals(255, result.ayahNumber)
        assertEquals(PageReferenceType.UTHMANI, result.pageReferenceType)
    }

    @Test
    fun arabicSearchUsesSearchIndexButDisplaysVerifiedDisplayText() = runTest {
        val searchRow = database.searchIndexDao().getSearchRow("1:1")
        assertNotNull(searchRow)

        val results = repository.searchNormalizedArabic(searchRow!!.normalizedArabic.take(8), "UTHMANI")

        assertTrue(results.any { it.ayahKey == "1:1" })
        val display = results.first { it.ayahKey == "1:1" }.displayText
        val quranText = database.quranTextDao().getTextByAyahAndScript("1:1", "UTHMANI")

        assertNotNull(quranText)
        assertEquals(quranText!!.displayText, display)
        assertFalse(display == searchRow.normalizedArabic)
    }

    @Test
    fun pageAndJuzSearchReturnResultsWhenMetadataExists() = runTest {
        val pageResults = repository.search("Page 1", ScriptType.INDOPAK)
        val juzResults = repository.search("Juz 30", ScriptType.INDOPAK)

        assertTrue(pageResults.isNotEmpty())
        assertTrue(juzResults.isNotEmpty())
    }
}
