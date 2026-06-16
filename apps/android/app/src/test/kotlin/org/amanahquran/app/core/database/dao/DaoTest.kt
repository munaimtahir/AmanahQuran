package org.amanahquran.app.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amanahquran.app.core.database.AmanahQuranDatabase
import org.amanahquran.app.core.database.entity.AyahEntity
import org.amanahquran.app.core.database.entity.QuranTextEntity
import org.amanahquran.app.core.database.entity.SurahEntity
import org.amanahquran.app.core.model.ScriptType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DaoTest {
    private lateinit var db: AmanahQuranDatabase
    private lateinit var surahDao: SurahDao
    private lateinit var ayahDao: AyahDao
    private lateinit var quranTextDao: QuranTextDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AmanahQuranDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        surahDao = db.surahDao()
        ayahDao = db.ayahDao()
        quranTextDao = db.quranTextDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `insert and get surahs`() = runBlocking {
        val surahs = listOf(
            SurahEntity(1, "Al-Fatihah", "The Opening", "الفاتحة", "Meccan", 7),
            SurahEntity(2, "Al-Baqarah", "The Cow", "البقرة", "Medinan", 286)
        )
        surahDao.insertAllSurahs(surahs)
        
        val allSurahs = surahDao.getAllSurahs().first()
        assertEquals(2, allSurahs.size)
        assertEquals("Al-Fatihah", allSurahs[0].nameArabic)
    }

    @Test
    fun `insert and get ayahs and texts`() = runBlocking {
        val ayah = AyahEntity("1:1", 1, 1, 1, 1, 1, 1, null)
        ayahDao.insertAllAyahs(listOf(ayah))
        
        val textIndopak = QuranTextEntity(
            ayahKey = "1:1",
            scriptType = ScriptType.INDOPAK,
            displayText = "DUMMY_INDOPAK_TEXT",
            sourceId = 1,
            checksum = "HASH1"
        )
        val textUthmani = QuranTextEntity(
            ayahKey = "1:1",
            scriptType = ScriptType.UTHMANI,
            displayText = "DUMMY_UTHMANI_TEXT",
            sourceId = 2,
            checksum = "HASH2"
        )
        quranTextDao.insertAllQuranTexts(listOf(textIndopak, textUthmani))
        
        val fetchedIndopak = quranTextDao.getTextByAyahAndScript("1:1", ScriptType.INDOPAK)
        assertNotNull(fetchedIndopak)
        assertEquals("DUMMY_INDOPAK_TEXT", fetchedIndopak?.displayText)

        val fetchedUthmani = quranTextDao.getTextByAyahAndScript("1:1", ScriptType.UTHMANI)
        assertNotNull(fetchedUthmani)
        assertEquals("DUMMY_UTHMANI_TEXT", fetchedUthmani?.displayText)
    }
}
