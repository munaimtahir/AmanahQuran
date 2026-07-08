package org.amanahquran.app.core.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.sqlite.db.SimpleSQLiteQuery
import org.amanahquran.app.core.database.AmanahContentDatabase
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ScriptType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReaderQueryCleanupTest {
    private lateinit var database: AmanahContentDatabase
    private lateinit var repository: QuranContentRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "reader-query-cleanup-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
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
    fun surahQueryReturnsSelectedSurahAndSelectedScriptOnly() {
        val ayahs = kotlinx.coroutines.runBlocking {
            repository.getReaderAyahs(ReaderOpenMode.Surah(2), ScriptType.UTHMANI.name)
        }

        assertTrue(ayahs.isNotEmpty())
        assertTrue(ayahs.all { it.surahNumber == 2 })
        assertTrue(ayahs.all { it.scriptType == ScriptType.UTHMANI.name })
        assertTrue(ayahs.all { it.displayText.isNotBlank() })
    }

    @Test
    fun juzQueryReturnsSelectedJuzAndSelectedScriptOnly() {
        val ayahs = kotlinx.coroutines.runBlocking {
            repository.getReaderAyahs(ReaderOpenMode.Juz(30), ScriptType.INDOPAK.name)
        }

        assertTrue(ayahs.isNotEmpty())
        assertTrue(ayahs.all { it.juzNumber == 30 })
        assertTrue(ayahs.all { it.scriptType == ScriptType.INDOPAK.name })
    }

    @Test
    fun pageQueryReturnsSelectedPageAndSelectedScriptOnly() {
        val ayahs = kotlinx.coroutines.runBlocking {
            repository.getReaderAyahs(ReaderOpenMode.Page(1, PageReferenceType.INDOPAK), ScriptType.INDOPAK.name)
        }

        assertTrue(ayahs.isNotEmpty())
        assertTrue(ayahs.all { it.pageNumber == 1 })
        assertTrue(ayahs.all { it.scriptType == ScriptType.INDOPAK.name })
    }

    @Test
    fun packagedDatabaseContainsRequiredIndexes() {
        val ayahIndexes = readIndexNames("ayahs")
        val quranTextIndexes = readIndexNames("quran_texts")

        assertTrue(ayahIndexes.contains("idx_ayahs_surah_ayah"))
        assertTrue(ayahIndexes.contains("idx_ayahs_juz_order"))
        assertTrue(ayahIndexes.contains("idx_ayahs_page_order"))
        assertTrue(ayahIndexes.contains("idx_ayahs_key"))
        assertTrue(quranTextIndexes.contains("idx_quran_texts_ayah_script"))
        assertTrue(quranTextIndexes.contains("idx_quran_texts_unique_ayah_script"))
    }

    @Test
    fun readerPathDoesNotCallGetAllSurahs() {
        val source = File(
            "src/main/kotlin/org/amanahquran/app/feature/reader/ReaderViewModel.kt",
        ).readText()

        assertFalse(source.contains("getAllSurahs()"))
    }

    private fun readIndexNames(tableName: String): Set<String> {
        val names = mutableSetOf<String>()
        database.openHelper.readableDatabase.query(SimpleSQLiteQuery("PRAGMA index_list('$tableName')")).use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            while (cursor.moveToNext()) {
                names += cursor.getString(nameIndex)
            }
        }
        return names
    }
}
