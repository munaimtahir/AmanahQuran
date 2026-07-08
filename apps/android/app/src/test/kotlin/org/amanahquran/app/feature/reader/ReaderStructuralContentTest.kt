package org.amanahquran.app.feature.reader

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.amanahquran.app.core.database.AmanahContentDatabase
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.QuranContentRepositoryImpl
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.runBlocking

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReaderStructuralContentTest {
    private lateinit var database: AmanahContentDatabase
    private lateinit var repository: QuranContentRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "reader-structural-test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
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
    fun surah1OpeningDoesNotDuplicateBismillah() {
        runBlocking {
            val ayahs = listOf(
                ayahModel("1:1", ScriptType.INDOPAK),
                ayahModel("1:2", ScriptType.INDOPAK),
            )

            val blocks = buildReaderStructuralItems(ayahs, ReaderOpenMode.Surah(1))

            assertEquals(
                listOf(
                    "surah-header-1",
                    "ayah-1:1-INDOPAK",
                    "ayah-1:2-INDOPAK",
                ),
                blocks.map { it.key() },
            )
            assertFalse(blocks.any { it is ReaderStructuralItem.Bismillah })
        }
    }

    @Test
    fun surah2OpeningAddsHeaderAndBismillah() {
        runBlocking {
            val ayahs = listOf(
                ayahModel("2:1", ScriptType.INDOPAK),
                ayahModel("2:2", ScriptType.INDOPAK),
            )

            val blocks = buildReaderStructuralItems(ayahs, ReaderOpenMode.Surah(2))

            assertEquals(
                listOf(
                    "surah-header-2",
                    "bismillah-2",
                    "ayah-2:1-INDOPAK",
                    "ayah-2:2-INDOPAK",
                ),
                blocks.map { it.key() },
            )
        }
    }

    @Test
    fun surah9OpeningSkipsBismillah() {
        runBlocking {
            val ayahs = listOf(
                ayahModel("9:1", ScriptType.INDOPAK),
                ayahModel("9:2", ScriptType.INDOPAK),
            )

            val blocks = buildReaderStructuralItems(ayahs, ReaderOpenMode.Surah(9))

            assertEquals(
                listOf(
                    "surah-header-9",
                    "ayah-9:1-INDOPAK",
                    "ayah-9:2-INDOPAK",
                ),
                blocks.map { it.key() },
            )
            assertFalse(blocks.any { it is ReaderStructuralItem.Bismillah })
        }
    }

    @Test
    fun juzBoundaryInsideSameSurahInsertsJuzHeaderOnly() {
        runBlocking {
            val boundary = findInternalJuzBoundary() ?: error("No internal juz boundary found")
            val previous = ayahModel(boundary.previousAyahKey, ScriptType.INDOPAK)
            val current = ayahModel(boundary.currentAyahKey, ScriptType.INDOPAK)

            val blocks = buildReaderStructuralItems(
                ayahs = listOf(previous, current),
                openMode = ReaderOpenMode.Page(previous.pageNumber, PageReferenceType.INDOPAK),
            )

            assertEquals(
                listOf(
                    "ayah-${previous.ayahKey}-INDOPAK",
                    "juz-header-${current.juzNumber}-${startingReferenceLabel(current.surahNumber, current.ayahNumber)}",
                    "ayah-${current.ayahKey}-INDOPAK",
                ),
                blocks.map { it.key() },
            )
            assertFalse(blocks.any { it is ReaderStructuralItem.Bismillah })
            assertFalse(blocks.any { it is ReaderStructuralItem.SurahHeader })
        }
    }

    @Test
    fun middleOfSurahDoesNotShowFullHeaderAtStart() {
        runBlocking {
            val ayahs = listOf(
                ayahModel("3:153", ScriptType.INDOPAK),
                ayahModel("3:154", ScriptType.INDOPAK),
            )

            val blocks = buildReaderStructuralItems(
                ayahs = ayahs,
                openMode = ReaderOpenMode.Page(66, PageReferenceType.INDOPAK),
            )

            assertEquals(
                listOf(
                    "ayah-3:153-INDOPAK",
                    "ayah-3:154-INDOPAK",
                ),
                blocks.map { it.key() },
            )
            assertFalse(blocks.any { it is ReaderStructuralItem.SurahHeader })
        }
    }

    @Test
    fun coincidentJuzAndSurahStartRendersJuzThenSurahThenBismillah() {
        runBlocking {
            val first = findSurahAndJuzStart() ?: error("No surah/juz overlap found")
            val nextKey = nextAyahKey(first.ayahKey)
            val ayahs = listOf(
                ayahModel(first.ayahKey, ScriptType.INDOPAK),
                ayahModel(nextKey, ScriptType.INDOPAK),
            )

            val blocks = buildReaderStructuralItems(
                ayahs = ayahs,
                openMode = ReaderOpenMode.Surah(first.surahNumber),
                showLeadingJuzHeader = true,
            )

            assertEquals(
                listOf(
                    "juz-header-${first.juzNumber}-${first.startingReference}",
                    "surah-header-${first.surahNumber}",
                    "bismillah-${first.surahNumber}",
                    "ayah-${first.ayahKey}-INDOPAK",
                    "ayah-$nextKey-INDOPAK",
                ),
                blocks.map { it.key() },
            )
            assertTrue(blocks.any { it is ReaderStructuralItem.Bismillah })
        }
    }

    @Test
    fun scriptSwitchKeepsCanonicalAyahIdentityAcrossBlocks() {
        runBlocking {
            val ayahsIndopak = listOf(
                ayahModel("2:255", ScriptType.INDOPAK),
                ayahModel("2:256", ScriptType.INDOPAK),
            )
            val ayahsUthmani = listOf(
                ayahModel("2:255", ScriptType.UTHMANI),
                ayahModel("2:256", ScriptType.UTHMANI),
            )

            val indopak = buildReaderStructuralItems(ayahsIndopak, ReaderOpenMode.Surah(2))
            val uthmani = buildReaderStructuralItems(ayahsUthmani, ReaderOpenMode.Surah(2))

            assertEquals(
                indopak.filterIsInstance<ReaderStructuralItem.Ayah>().map { it.ayah.ayahKey },
                uthmani.filterIsInstance<ReaderStructuralItem.Ayah>().map { it.ayah.ayahKey },
            )
        }
    }

    private data class InternalBoundary(
        val previousAyahKey: String,
        val currentAyahKey: String,
    )

    private data class OverlapStart(
        val ayahKey: String,
        val surahNumber: Int,
        val juzNumber: Int,
        val startingReference: String,
    )

    private suspend fun ayahModel(ayahKey: String, scriptType: ScriptType): ReaderAyahUiModel {
        val ayah = database.ayahDao().getAyahByKey(ayahKey) ?: error("Missing ayah $ayahKey")
        val display = database.quranTextDao().getTextByAyahAndScript(ayahKey, scriptType.name)
            ?: error("Missing display text for $ayahKey $scriptType")
        val surah = database.surahDao().getSurahByNumber(ayah.surahNumber)
            ?: error("Missing surah ${ayah.surahNumber}")
        return ReaderAyahUiModel(
            ayahKey = ayah.ayahKey,
            surahNumber = ayah.surahNumber,
            ayahNumber = ayah.ayahNumber,
            juzNumber = ayah.juzNumber,
            pageNumber = ayah.pageNumber,
            displayText = display.displayText,
            scriptType = scriptType,
            surahNameArabic = surah.nameArabic,
            surahNameSimple = surah.nameSimple,
            surahAyahCount = surah.ayahCount,
        )
    }

    private suspend fun findInternalJuzBoundary(): InternalBoundary? {
        for (juzNumber in 1..30) {
            val currentKey = repository.getFirstAyahForJuz(juzNumber) ?: continue
            val current = database.ayahDao().getAyahByKey(currentKey) ?: continue
            if (current.ayahNumber <= 1) continue
            val previousKey = "${current.surahNumber}:${current.ayahNumber - 1}"
            val previous = database.ayahDao().getAyahByKey(previousKey) ?: continue
            if (previous.surahNumber == current.surahNumber) {
                return InternalBoundary(previousAyahKey = previous.ayahKey, currentAyahKey = current.ayahKey)
            }
        }
        return null
    }

    private suspend fun findSurahAndJuzStart(): OverlapStart? {
        for (juzNumber in 1..30) {
            val currentKey = repository.getFirstAyahForJuz(juzNumber) ?: continue
            val current = database.ayahDao().getAyahByKey(currentKey) ?: continue
            if (current.ayahNumber != 1) continue
            if (current.surahNumber == 1 || current.surahNumber == 9) continue
            return OverlapStart(
                ayahKey = current.ayahKey,
                surahNumber = current.surahNumber,
                juzNumber = current.juzNumber,
                startingReference = startingReferenceLabel(current.surahNumber, current.ayahNumber),
            )
        }
        return null
    }

    private suspend fun nextAyahKey(ayahKey: String): String {
        val ayah = database.ayahDao().getAyahByKey(ayahKey) ?: error("Missing ayah $ayahKey")
        return "${ayah.surahNumber}:${ayah.ayahNumber + 1}"
    }

    private suspend fun startingReferenceLabel(surahNumber: Int, ayahNumber: Int): String {
        val surahSimple = repository.getSurahByNumber(surahNumber)?.nameSimple.orEmpty()
        return "${surahSimple.ifBlank { "Surah $surahNumber" }} $surahNumber:$ayahNumber"
    }
}
