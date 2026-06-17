package org.amanahquran.app.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.amanahquran.app.content.validation.ContentValidationService
import org.amanahquran.app.core.repository.SearchRepositoryImpl
import org.amanahquran.app.core.trust.TrustCenterAssetLoader
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmanahContentDatabaseTest {
    private lateinit var context: Context
    private lateinit var database: AmanahContentDatabase

    @Before
    fun createDatabase() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
        )
            .createFromAsset(AmanahContentDatabase.ASSET_PATH)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun packagedAssetsExist() {
        context.assets.open(AmanahContentDatabase.ASSET_PATH).use {
            assertTrue(it.available() > 0)
        }
        context.assets.open(TrustCenterAssetLoader.ASSET_PATH).use {
            assertTrue(it.available() > 0)
        }
    }

    @Test
    fun roomOpensPrepackagedDatabaseAndCountsMatchCandidateAudit() = runBlocking {
        assertEquals(114, database.surahDao().getSurahCount())
        assertEquals(6236, database.ayahDao().getAyahCount())
        assertEquals(6236, database.quranTextDao().getTextCountByScript("UTHMANI"))
        assertEquals(6236, database.quranTextDao().getTextCountByScript("INDOPAK"))
        assertEquals(6236, database.searchIndexDao().getSearchIndexCount())
        assertEquals(120, database.contentSourceDao().getContentSourceCount())
        assertEquals(9, database.contentValidationDao().getContentValidationCount())
    }

    @Test
    fun requiredSampleAyahsLoadForBothScripts() = runBlocking {
        assertNotNull(database.quranTextDao().getTextByAyahAndScript("1:1", "UTHMANI")?.displayText)
        assertNotNull(database.quranTextDao().getTextByAyahAndScript("1:1", "INDOPAK")?.displayText)
        assertNotNull(database.quranTextDao().getTextByAyahAndScript("2:255", "UTHMANI")?.displayText)
        assertNotNull(database.quranTextDao().getTextByAyahAndScript("2:255", "INDOPAK")?.displayText)
    }

    @Test
    fun searchUsesNormalizedRowsButReturnsDisplayTextFromQuranTexts() = runBlocking {
        val searchRow = database.searchIndexDao().getSearchRow("1:1")
        assertNotNull(searchRow)

        val results = SearchRepositoryImpl(
            searchIndexDao = database.searchIndexDao(),
            quranTextDao = database.quranTextDao(),
        ).searchNormalizedArabic(searchRow!!.normalizedArabic.take(8), "UTHMANI")

        assertTrue(results.any { it.ayahKey == "1:1" })
        val display = results.first { it.ayahKey == "1:1" }.displayText
        assertEquals(database.quranTextDao().getTextByAyahAndScript("1:1", "UTHMANI")?.displayText, display)
        assertFalse(display == searchRow.normalizedArabic)
    }

    @Test
    fun trustCenterJsonLoads() {
        val trustContent = TrustCenterAssetLoader(context).load()
        assertTrue(trustContent.rawJson.contains("no_modification_statement"))
        assertTrue(trustContent.noModificationStatementPreview?.contains("Search normalization is stored separately") == true)
    }

    @Test
    fun noProhibitedTablesExistInCandidateDatabase() {
        val prohibited = setOf(
            "translations",
            "tafsir",
            "audio",
            "users",
            "accounts",
            "sync",
            "analytics",
            "tracking",
            "ads",
        )
        val cursor = database.openHelper.readableDatabase.query(
            "SELECT name FROM sqlite_master WHERE type = 'table'",
        )
        val tableNames = buildSet {
            cursor.use {
                while (it.moveToNext()) {
                    add(it.getString(0))
                }
            }
        }
        assertTrue(tableNames.none { it in prohibited })
    }

    @Test
    fun fontInventoryPresentButNoFontFilesBundled() = runBlocking {
        assertEquals(41, database.fontInventoryDao().getFontInventoryCount())
        val assetFonts = context.assets.list("fonts").orEmpty()
        assertTrue(assetFonts.none { it.endsWith(".ttf") || it.endsWith(".otf") || it.endsWith(".woff") || it.endsWith(".woff2") })
    }

    @Test
    fun validationServicePassesInternalGate() = runBlocking {
        val snapshot = ContentValidationService(
            database = database,
            trustCenterAssetLoader = TrustCenterAssetLoader(context),
        ).validatePackagedContent()

        assertTrue(snapshot.passed)
    }
}
