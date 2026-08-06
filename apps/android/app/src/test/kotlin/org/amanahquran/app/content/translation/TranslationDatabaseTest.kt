package org.amanahquran.app.content.translation

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TranslationDatabaseTest {
    private lateinit var database: TranslationDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.databaseBuilder(
            context,
            TranslationDatabase::class.java,
            "translation-test-${System.nanoTime()}.db",
        ).createFromAsset(TranslationDatabase.ASSET_PATH).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun bundledUrduTranslationHasCompleteCanonicalCoverage() = runBlocking {
        val rows = database.translationDao().observeAyahs(TranslationRepository.TRANSLATION_ID)
        val first = rows.first()
        assertEquals(6236, first.size)
        assertNotNull(database.translationDao().getAyah(TranslationRepository.TRANSLATION_ID, "2:255"))
        assertEquals("v1.1.3-csv.1", database.translationDao().getMetadata(TranslationRepository.TRANSLATION_ID)?.version)
    }
}
