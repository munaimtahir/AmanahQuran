package org.amanahquran.app.feature.reader

import org.amanahquran.app.core.model.ScriptType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReaderAnchorControllerTest {
    private fun ayah(key: String) = ReaderStructuralItem.Ayah(
        ReaderAyahUiModel(
            ayahKey = key,
            surahNumber = key.substringBefore(':').toInt(),
            ayahNumber = key.substringAfter(':').toInt(),
            juzNumber = 1,
            pageNumber = 1,
            displayText = "text-$key",
            scriptType = ScriptType.INDOPAK,
        ),
    )

    @Test
    fun nearestAyahRow_picksTheRowWhoseCentreIsClosestToTheTargetY() {
        val rows = listOf(
            Triple("2:1", 0, 200),   // centre 100
            Triple("2:2", 200, 300), // centre 350
            Triple("2:3", 500, 250), // centre 625
        )

        assertEquals("2:1" to 0, nearestAyahRow(rows, targetYPx = 90f))
        assertEquals("2:2" to 200, nearestAyahRow(rows, targetYPx = 360f))
        assertEquals("2:3" to 500, nearestAyahRow(rows, targetYPx = 9999f))
    }

    @Test
    fun nearestAyahRow_atViewportCentreForAButtonPress() {
        val rows = listOf(
            Triple("18:1", 0, 400),
            Triple("18:2", 400, 400),
        )
        // A simulated 800px-tall viewport centre (400px) sits exactly on the boundary; either
        // adjacent row is an acceptable nearest match, but the result must be one of them.
        val result = nearestAyahRow(rows, targetYPx = 400f)
        assert(result?.first == "18:1" || result?.first == "18:2")
    }

    @Test
    fun nearestAyahRow_emptyRowsReturnsNull() {
        assertNull(nearestAyahRow(emptyList(), targetYPx = 100f))
    }

    @Test
    fun blockIndexForAyah_findsTheAyahAmongMixedHeaderBlocks() {
        val blocks = listOf(
            ReaderStructuralItem.SurahHeader(2, "", "Al-Baqarah", 286, ScriptType.INDOPAK),
            ReaderStructuralItem.Bismillah(2, ScriptType.INDOPAK),
            ayah("2:1"),
            ayah("2:2"),
            ReaderStructuralItem.JuzHeader(2, "Al-Baqarah 2:142"),
            ayah("2:142"),
        )

        assertEquals(2, blockIndexForAyah(blocks, "2:1"))
        assertEquals(5, blockIndexForAyah(blocks, "2:142"))
    }

    @Test
    fun blockIndexForAyah_missingAyahReturnsNull() {
        val blocks = listOf(ayah("2:1"), ayah("2:2"))
        assertNull(blockIndexForAyah(blocks, "2:255"))
    }

    private fun continuousBlock(vararg keys: String) = ReaderStructuralItem.ContinuousBlock(
        buildContinuousQuranBlocks(keys.map { key -> ayah(key).ayah }).single(),
    )

    @Test
    fun blockIndexForAyah_findsAnAyahInsideAContinuousBlockCoveringManyAyahs() {
        val blocks = listOf(
            ReaderStructuralItem.SurahHeader(2, "", "Al-Baqarah", 286, ScriptType.INDOPAK),
            continuousBlock("2:1", "2:2", "2:3"),
        )

        assertEquals(1, blockIndexForAyah(blocks, "2:2"))
    }

    @Test
    fun blockIndexForAyah_ayahNotInAnyContinuousBlockReturnsNull() {
        val blocks = listOf(continuousBlock("2:1", "2:2"))
        assertNull(blockIndexForAyah(blocks, "2:255"))
    }
}
