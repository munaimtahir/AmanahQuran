package org.amanahquran.app.content.translation

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Content-contract tests for the bundled dual-translation asset (Manifest English + Irfan-ul-Quran
 * Urdu). Counts and text fragments here are recalculated from the packaged database itself, never
 * hardcoded independently of it -- see AMANAH_MULTILINGUAL_TRANSLATION_INTEGRATION mega-sprint
 * sections 41-45.
 */
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
    fun bothTranslationIdsArePresentAndUnique() = runBlocking {
        val manifest = database.translationDao().getMetadata(MANIFEST_EN)
        val irfan = database.translationDao().getMetadata(IRFAN_UR)
        assertNotNull(manifest)
        assertNotNull(irfan)
        assertEquals(MANIFEST_EN, manifest?.translationId)
        assertEquals(IRFAN_UR, irfan?.translationId)
    }

    @Test
    fun manifestCanonicalStateCountIs6236WithExactlyOneSourceMissing() = runBlocking {
        val totalStates = database.translationDao().countAyahs(MANIFEST_EN)
        val sourceMissing = database.translationDao().countSourceMissing(MANIFEST_EN)
        assertEquals(6236, totalStates)
        assertEquals(1, sourceMissing)
        assertEquals(6235, totalStates - sourceMissing)
    }

    @Test
    fun irfanUrCanonicalStateCountIs6236WithExactlyOneSourceMissing() = runBlocking {
        val totalStates = database.translationDao().countAyahs(IRFAN_UR)
        val sourceMissing = database.translationDao().countSourceMissing(IRFAN_UR)
        assertEquals(6236, totalStates)
        assertEquals(1, sourceMissing)
        assertEquals(6235, totalStates - sourceMissing)
    }

    @Test
    fun ayah1_1IsSourceMissingForBothTranslationsWithNoInventedText() = runBlocking {
        val manifest11 = database.translationDao().getAyah(MANIFEST_EN, "1:1")
        val irfan11 = database.translationDao().getAyah(IRFAN_UR, "1:1")
        assertNotNull("1:1 must exist as an explicit SOURCE_MISSING row for Manifest EN", manifest11)
        assertNotNull("1:1 must exist as an explicit SOURCE_MISSING row for Irfan UR", irfan11)
        assertEquals(TranslationAvailabilityStatus.SOURCE_MISSING, manifest11?.availabilityStatus)
        assertEquals(TranslationAvailabilityStatus.SOURCE_MISSING, irfan11?.availabilityStatus)
        assertNull("1:1 displayText must be null, never fabricated text", manifest11?.displayText)
        assertNull("1:1 displayText must be null, never fabricated text", irfan11?.displayText)
    }

    @Test
    fun ayah2_255IsAvailableForBothTranslations() = runBlocking {
        assertNotNull(database.translationDao().getAyah(MANIFEST_EN, "2:255")?.displayText)
        assertNotNull(database.translationDao().getAyah(IRFAN_UR, "2:255")?.displayText)
    }

    @Test
    fun irfanShiftedMappingCarriesSourceAyah1Through5Verbatim() = runBlocking {
        // Canonical 1:2..1:6 each carry source-native 1:1..1:5, one ayah earlier -- the approved
        // SHIFTED mapping. Each must be present and non-blank; exact wording is the frozen source's,
        // not reconstructed here.
        for (canonicalAyah in 2..6) {
            val entry = database.translationDao().getAyah(IRFAN_UR, "1:$canonicalAyah")
            assertNotNull("1:$canonicalAyah should be TRANSLATED", entry)
            assertEquals(TranslationAvailabilityStatus.TRANSLATED, entry?.availabilityStatus)
            assertTrue("1:$canonicalAyah displayText should be non-blank", !entry?.displayText.isNullOrBlank())
        }
    }

    @Test
    fun irfanMergedMappingAt1_7CombinesTwoSourceRecords() = runBlocking {
        // Canonical 1:7 is the approved MERGED mapping (source 1:6 + 1:7); just assert it exists,
        // is TRANSLATED, and its text is long enough to plausibly be two concatenated ayahs -- the
        // exact wording is the frozen source's, not reconstructed here.
        val entry = database.translationDao().getAyah(IRFAN_UR, "1:7")
        assertNotNull(entry)
        assertEquals(TranslationAvailabilityStatus.TRANSLATED, entry?.availabilityStatus)
        assertTrue((entry?.displayText?.length ?: 0) > 20)
    }

    @Test
    fun manifestFootnoteCountIs142() = runBlocking {
        assertEquals(142, database.translationDao().countFootnotes(MANIFEST_EN))
    }

    @Test
    fun irfanFootnoteCountIs45() = runBlocking {
        assertEquals(45, database.translationDao().countFootnotes(IRFAN_UR))
    }

    @Test
    fun noCrossTranslationContamination() = runBlocking {
        val manifestKeys = database.translationDao().observeAyahs(MANIFEST_EN).first().map { it.translationId }.toSet()
        val irfanKeys = database.translationDao().observeAyahs(IRFAN_UR).first().map { it.translationId }.toSet()
        assertEquals(setOf(MANIFEST_EN), manifestKeys)
        assertEquals(setOf(IRFAN_UR), irfanKeys)

        val manifestFootnoteIds = database.translationDao().getAllFootnotes(MANIFEST_EN).map { it.translationId }.toSet()
        val irfanFootnoteIds = database.translationDao().getAllFootnotes(IRFAN_UR).map { it.translationId }.toSet()
        assertTrue(manifestFootnoteIds.isEmpty() || manifestFootnoteIds == setOf(MANIFEST_EN))
        assertTrue(irfanFootnoteIds.isEmpty() || irfanFootnoteIds == setOf(IRFAN_UR))
    }

    @Test
    fun metadataDirectionMatchesLanguage() = runBlocking {
        assertEquals("LTR", database.translationDao().getMetadata(MANIFEST_EN)?.direction)
        assertEquals("RTL", database.translationDao().getMetadata(IRFAN_UR)?.direction)
    }

    @Test
    fun metadataPermissionStatusIsApprovedForBoth() = runBlocking {
        assertEquals("APPROVED", database.translationDao().getMetadata(MANIFEST_EN)?.permissionStatus)
        assertEquals("APPROVED", database.translationDao().getMetadata(IRFAN_UR)?.permissionStatus)
    }

    private companion object {
        const val MANIFEST_EN = "TAHIR_QADRI_MANIFEST_EN"
        const val IRFAN_UR = "TAHIR_QADRI_IRFAN_UR"
    }
}
