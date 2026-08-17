package org.amanahquran.app.core.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.amanahquran.app.core.database.AmanahContentDatabase
import org.amanahquran.app.core.database.AmanahQuranDatabase
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
class MushafRepositoryLayoutTest {
    private lateinit var contentDatabase: AmanahContentDatabase
    private lateinit var quranDatabase: AmanahQuranDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        contentDatabase = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "mushaf-layout-test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
        )
            .createFromAsset(AmanahContentDatabase.ASSET_PATH)
            .allowMainThreadQueries()
            .build()
        quranDatabase = Room.inMemoryDatabaseBuilder(
            context,
            AmanahQuranDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        contentDatabase.close()
        quranDatabase.close()
    }

    @Test
    fun pageOneStartsAtSurahBoundaryButMiddlePagesDoNot() = runBlocking {
        val repository = createRepository()

        val pageOne = repository.getMushafPage(1, ScriptType.INDOPAK).first
        val page66 = repository.getMushafPage(66, ScriptType.INDOPAK).first

        assertTrue(pageOne.startsAtSurahBoundary)
        assertEquals(1, pageOne.surahNumber)
        assertNotNull(pageOne.surahAyahCount)

        assertFalse(page66.startsAtSurahBoundary)
        assertEquals(3, page66.surahNumber)
        assertEquals("Al Imran", page66.surahLabel)
        assertNotNull(page66.surahAyahCount)
    }

    private fun createRepository(): MushafRepository {
        val bookmarkRepository = object : BookmarkRepository {
            override fun getAllBookmarks() = flowOf(emptyList<BookmarkRecord>())
            override suspend fun addAyahBookmark(ayahKey: String): Long = -1
            override suspend fun toggleAyahBookmark(ayahKey: String): Boolean = false
            override suspend fun addPageBookmark(pageNumber: Int, pageReferenceType: PageReferenceType, firstAyahKey: String?): Long = -1
            override suspend fun togglePageBookmark(pageNumber: Int, pageReferenceType: PageReferenceType): Boolean = false
            override suspend fun removeAyahBookmark(ayahKey: String) {}
            override suspend fun removePageBookmark(pageNumber: Int, pageReferenceType: PageReferenceType) {}
            override suspend fun removeBookmarkById(id: Long) {}
            override suspend fun isAyahBookmarked(ayahKey: String): Boolean = false
            override suspend fun isPageBookmarked(pageNumber: Int, pageReferenceType: PageReferenceType): Boolean = false
            override suspend fun getBookmarkCount(): Int = 0
        }

        return MushafRepositoryImpl(
            contentDatabase = contentDatabase,
            quranDatabase = quranDatabase,
            bookmarkRepository = bookmarkRepository,
        )
    }
}
