package org.amanahquran.app.feature.reader

import org.amanahquran.app.core.model.ScriptType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContinuousQuranBlocksTest {
    private fun ayah(key: String, pageNumber: Int, text: String = "text-$key") = ReaderAyahUiModel(
        ayahKey = key,
        surahNumber = key.substringBefore(':').toInt(),
        ayahNumber = key.substringAfter(':').toInt(),
        juzNumber = 1,
        pageNumber = pageNumber,
        displayText = text,
        scriptType = ScriptType.INDOPAK,
    )

    @Test
    fun ayahMarkerText_usesArabicIndicDigitsAfterTheEndOfAyahGlyph() {
        assertEquals("۝١", ayahMarkerText(1))
        assertEquals("۝٢٥٥", ayahMarkerText(255))
    }

    @Test
    fun buildContinuousQuranBlocks_singlePageProducesOneBlockWithNoOmissionOrDuplication() {
        val ayahs = listOf(ayah("2:1", pageNumber = 5), ayah("2:2", pageNumber = 5), ayah("2:3", pageNumber = 5))

        val blocks = buildContinuousQuranBlocks(ayahs)

        assertEquals(1, blocks.size)
        assertEquals(listOf("2:1", "2:2", "2:3"), blocks[0].ayahRanges.map { it.ayahKey })
        // Every ayah's own canonical text must appear verbatim, unmodified, inside the block.
        ayahs.forEach { assertTrue(blocks[0].plainText.contains(it.displayText)) }
    }

    @Test
    fun buildContinuousQuranBlocks_splitsOnPageNumberChange() {
        val ayahs = listOf(
            ayah("2:1", pageNumber = 5),
            ayah("2:2", pageNumber = 5),
            ayah("2:3", pageNumber = 6),
        )

        val blocks = buildContinuousQuranBlocks(ayahs)

        assertEquals(2, blocks.size)
        assertEquals(5, blocks[0].pageNumber)
        assertEquals(listOf("2:1", "2:2"), blocks[0].ayahRanges.map { it.ayahKey })
        assertEquals(6, blocks[1].pageNumber)
        assertEquals(listOf("2:3"), blocks[1].ayahRanges.map { it.ayahKey })
    }

    @Test
    fun buildContinuousQuranBlocks_emptyInputProducesNoBlocks() {
        assertEquals(emptyList<ContinuousQuranBlock>(), buildContinuousQuranBlocks(emptyList()))
    }

    @Test
    fun offsetToAyahKey_resolvesAnOffsetInsideTextOrItsMarkerToTheOwningAyah() {
        val ayahs = listOf(ayah("2:1", pageNumber = 5, text = "AAA"), ayah("2:2", pageNumber = 5, text = "BBB"))
        val block = buildContinuousQuranBlocks(ayahs).single()
        val secondRange = block.ayahRanges[1]

        assertEquals("2:1", offsetToAyahKey(block, 0))
        assertEquals("2:2", offsetToAyahKey(block, secondRange.textStart))
        assertEquals("2:2", offsetToAyahKey(block, secondRange.markerEnd - 1))
    }

    @Test
    fun offsetToAyahKey_beforeAnyRangeReturnsNull() {
        val block = ContinuousQuranBlock(pageNumber = 1, plainText = "x", ayahRanges = emptyList())
        assertNull(offsetToAyahKey(block, 0))
    }

    @Test
    fun collapseIntoContinuousBlocks_replacesAyahRunsButKeepsHeadersAndDividersInPlace() {
        val structural = listOf(
            ReaderStructuralItem.SurahHeader(2, "", "Al-Baqarah", 286, ScriptType.INDOPAK),
            ReaderStructuralItem.Bismillah(2, ScriptType.INDOPAK),
            ReaderStructuralItem.Ayah(ayah("2:1", pageNumber = 5)),
            ReaderStructuralItem.Ayah(ayah("2:2", pageNumber = 5)),
            ReaderStructuralItem.JuzHeader(2, "Al-Baqarah 2:142"),
            ReaderStructuralItem.Ayah(ayah("2:142", pageNumber = 20)),
        )

        val collapsed = collapseIntoContinuousBlocks(structural)

        assertEquals(5, collapsed.size)
        assertTrue(collapsed[0] is ReaderStructuralItem.SurahHeader)
        assertTrue(collapsed[1] is ReaderStructuralItem.Bismillah)
        val firstBlock = collapsed[2] as ReaderStructuralItem.ContinuousBlock
        assertEquals(listOf("2:1", "2:2"), firstBlock.block.ayahRanges.map { it.ayahKey })
        assertTrue(collapsed[3] is ReaderStructuralItem.JuzHeader)
        // No ContinuousBlock should ever merge ayahs across a header -- this asserts there are
        // exactly two blocks (one per run either side of the Juz header), never one combined block.
        val secondBlock = collapsed[4] as ReaderStructuralItem.ContinuousBlock
        assertEquals(listOf("2:142"), secondBlock.block.ayahRanges.map { it.ayahKey })
    }

    @Test
    fun collapseIntoContinuousBlocks_noAyahsProducesOnlyHeaders() {
        val structural = listOf(
            ReaderStructuralItem.SurahHeader(2, "", "Al-Baqarah", 286, ScriptType.INDOPAK),
        )
        assertEquals(structural, collapseIntoContinuousBlocks(structural))
    }

    @Test
    fun continuousBlockKeyIsStableAndDistinctPerBlock() {
        val ayahs = listOf(ayah("2:1", pageNumber = 5), ayah("2:2", pageNumber = 5))
        val block = ReaderStructuralItem.ContinuousBlock(buildContinuousQuranBlocks(ayahs).single())

        assertEquals("continuous-block-5-2:1-2:2", block.key())
    }

    @Test
    fun collapseIntoContinuousBlocks_pageDividerSplitsTheRunEvenWithoutAJuzOrSurahHeader() {
        val structural = listOf(
            ReaderStructuralItem.Ayah(ayah("3:153", pageNumber = 66)),
            ReaderStructuralItem.PageDivider(67),
            ReaderStructuralItem.Ayah(ayah("3:154", pageNumber = 67)),
        )

        val collapsed = collapseIntoContinuousBlocks(structural)

        assertEquals(3, collapsed.size)
        assertTrue(collapsed[0] is ReaderStructuralItem.ContinuousBlock)
        assertTrue(collapsed[1] is ReaderStructuralItem.PageDivider)
        assertTrue(collapsed[2] is ReaderStructuralItem.ContinuousBlock)
    }

    @Test
    fun buildContinuousQuranBlocks_splitsOnJuzBoundaryAndNeverBeginsInline() {
        val ayahs = listOf(
            ReaderAyahUiModel(
                ayahKey = "36:27",
                surahNumber = 36,
                ayahNumber = 27,
                juzNumber = 22,
                pageNumber = 442,
                displayText = "qila-dkhulil-jannah",
                scriptType = ScriptType.INDOPAK,
            ),
            ReaderAyahUiModel(
                ayahKey = "36:28",
                surahNumber = 36,
                ayahNumber = 28,
                juzNumber = 23,
                pageNumber = 443,
                displayText = "wa-ma-anzalna",
                scriptType = ScriptType.INDOPAK,
            ),
        )

        val blocks = buildContinuousQuranBlocks(ayahs)

        assertEquals(2, blocks.size)
        assertEquals(listOf("36:27"), blocks[0].ayahRanges.map { it.ayahKey })
        assertEquals(listOf("36:28"), blocks[1].ayahRanges.map { it.ayahKey })
        assertTrue(blocks[1].isJuzStart)
        assertTrue(blocks[1].ayahRanges[0].isJuzStart)
    }

    @Test
    fun buildContinuousQuranBlocks_splitsOnJuzBoundaryEvenOnSamePage() {
        val ayahs = listOf(
            ReaderAyahUiModel(
                ayahKey = "2:141",
                surahNumber = 2,
                ayahNumber = 141,
                juzNumber = 1,
                pageNumber = 21,
                displayText = "tilka-ummatun",
                scriptType = ScriptType.INDOPAK,
            ),
            ReaderAyahUiModel(
                ayahKey = "2:142",
                surahNumber = 2,
                ayahNumber = 142,
                juzNumber = 2,
                pageNumber = 21,
                displayText = "sayaqoolu-ssufahaau",
                scriptType = ScriptType.INDOPAK,
            ),
        )

        val blocks = buildContinuousQuranBlocks(ayahs)

        assertEquals(2, blocks.size)
        assertEquals(listOf("2:141"), blocks[0].ayahRanges.map { it.ayahKey })
        assertEquals(listOf("2:142"), blocks[1].ayahRanges.map { it.ayahKey })
        assertTrue(blocks[1].isJuzStart)
        assertTrue(blocks[1].ayahRanges[0].isJuzStart)
    }
}

