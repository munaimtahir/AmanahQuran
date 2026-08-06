package org.amanahquran.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReaderZoomLevelTest {
    @Test
    fun sevenLevelsInAscendingMultiplierOrder() {
        val multipliers = ReaderZoomLevel.entries.map { it.multiplier }
        assertEquals(7, ReaderZoomLevel.entries.size)
        assertEquals(multipliers.sorted(), multipliers)
    }

    @Test
    fun increaseStepsOneLevelAndClampsAtMaximum() {
        assertEquals(ReaderZoomLevel.LARGE, ReaderZoomLevel.STANDARD.increased())
        assertEquals(ReaderZoomLevel.MAXIMUM, ReaderZoomLevel.MAXIMUM.increased())
    }

    @Test
    fun decreaseStepsOneLevelAndClampsAtCompact() {
        assertEquals(ReaderZoomLevel.SMALL, ReaderZoomLevel.STANDARD.decreased())
        assertEquals(ReaderZoomLevel.COMPACT, ReaderZoomLevel.COMPACT.decreased())
    }

    @Test
    fun isMinimumAndIsMaximumFlagsOnlyTrueAtTheEdges() {
        assert(ReaderZoomLevel.COMPACT.isMinimum)
        assert(!ReaderZoomLevel.SMALL.isMinimum)
        assert(ReaderZoomLevel.MAXIMUM.isMaximum)
        assert(!ReaderZoomLevel.EXTRA_LARGE.isMaximum)
    }

    @Test
    fun defaultIsStandardAndElderDefaultIsExtraLarge() {
        assertEquals(ReaderZoomLevel.STANDARD, ReaderZoomLevel.default)
        assertEquals(ReaderZoomLevel.EXTRA_LARGE, ReaderZoomLevel.elderDefault)
    }

    @Test
    fun fromStoredNameRoundTripsAndRejectsInvalidValues() {
        assertEquals(ReaderZoomLevel.LARGE, ReaderZoomLevel.fromStoredName("LARGE"))
        assertNull(ReaderZoomLevel.fromStoredName("NOT_A_LEVEL"))
        assertNull(ReaderZoomLevel.fromStoredName(null))
    }

    @Test
    fun nearestToMigratesAnArbitraryMultiplierToTheClosestLevel() {
        assertEquals(ReaderZoomLevel.STANDARD, ReaderZoomLevel.nearestTo(1.0f))
        assertEquals(ReaderZoomLevel.STANDARD, ReaderZoomLevel.nearestTo(1.02f))
        assertEquals(ReaderZoomLevel.LARGE, ReaderZoomLevel.nearestTo(1.16f))
        assertEquals(ReaderZoomLevel.COMPACT, ReaderZoomLevel.nearestTo(0.5f))
        assertEquals(ReaderZoomLevel.MAXIMUM, ReaderZoomLevel.nearestTo(3f))
    }
}
