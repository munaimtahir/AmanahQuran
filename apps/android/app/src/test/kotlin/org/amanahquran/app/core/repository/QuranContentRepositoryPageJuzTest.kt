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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class QuranContentRepositoryPageJuzTest {
    private lateinit var database: AmanahContentDatabase
    private lateinit var repository: QuranContentRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "page-juz-test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
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
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun juzListReturnsThirtyItems() = runTest {
        val juzList = repository.getJuzList()

        assertEquals(30, juzList.size)
        assertEquals(1, juzList.first().juzNumber)
        assertEquals(30, juzList.last().juzNumber)
    }

    @Test
    fun pageListReturnsLayoutPagesForBothReferences() = runTest {
        val indopakPages = repository.getPageList(PageReferenceType.INDOPAK)
        val uthmaniPages = repository.getPageList(PageReferenceType.UTHMANI)

        assertTrue(indopakPages.isNotEmpty())
        assertTrue(uthmaniPages.isNotEmpty())
        assertEquals(PageReferenceType.INDOPAK, indopakPages.first().pageReferenceType)
        assertEquals(PageReferenceType.UTHMANI, uthmaniPages.first().pageReferenceType)
        assertEquals(indopakPages.size, uthmaniPages.size)
    }

    @Test
    fun pageReaderUsesVerifiedDisplayText() = runTest {
        val ayahs = repository.getAyahsForPage(1, PageReferenceType.INDOPAK, ScriptType.UTHMANI.name)
        val quranText = database.quranTextDao().getTextByAyahAndScript("1:1", "UTHMANI")
        val searchRow = database.searchIndexDao().getSearchRow("1:1")

        assertTrue(ayahs.isNotEmpty())
        assertNotNull(quranText)
        assertNotNull(searchRow)
        assertEquals(quranText!!.displayText, ayahs.first().displayText)
        assertFalse(ayahs.first().displayText == searchRow!!.normalizedArabic)
    }

    @Test
    fun juzReaderUsesVerifiedDisplayText() = runTest {
        val ayahs = repository.getAyahsForJuz(30, ScriptType.INDOPAK.name)
        val firstAyah = ayahs.firstOrNull()
        val quranText = firstAyah?.let { database.quranTextDao().getTextByAyahAndScript(it.ayahKey, "INDOPAK") }

        assertTrue(ayahs.isNotEmpty())
        assertNotNull(firstAyah)
        assertNotNull(quranText)
        assertEquals(quranText!!.displayText, firstAyah.displayText)
    }
}
