package org.amanahquran.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoScrollModelsTest {
    @Test
    fun sixPaceLevelsInDescendingMinutesPerJuzOrder() {
        val minutes = AutoScrollPace.entries.map { it.approximateMinutesPerJuz }
        assertEquals(6, AutoScrollPace.entries.size)
        assertEquals(minutes.sortedDescending(), minutes)
    }

    @Test
    fun fasterAndSlowerStepOneLevelAndClampAtTheEdges() {
        assertEquals(AutoScrollPace.MODERATELY_FAST, AutoScrollPace.COMFORTABLE.faster())
        assertEquals(AutoScrollPace.SLOW, AutoScrollPace.COMFORTABLE.slower())
        assertEquals(AutoScrollPace.VERY_FAST, AutoScrollPace.VERY_FAST.faster())
        assertEquals(AutoScrollPace.VERY_SLOW, AutoScrollPace.VERY_SLOW.slower())
    }

    @Test
    fun isSlowestAndIsFastestFlagsOnlyTrueAtTheEdges() {
        assertTrue(AutoScrollPace.VERY_SLOW.isSlowest)
        assertTrue(AutoScrollPace.VERY_FAST.isFastest)
        assertTrue(!AutoScrollPace.COMFORTABLE.isSlowest && !AutoScrollPace.COMFORTABLE.isFastest)
    }

    @Test
    fun defaultPaceIsComfortable() {
        assertEquals(AutoScrollPace.COMFORTABLE, AutoScrollPace.default)
    }

    @Test
    fun fromStoredNameRoundTripsAndRejectsInvalidValues() {
        assertEquals(AutoScrollPace.FAST, AutoScrollPace.fromStoredName("FAST"))
        assertNull(AutoScrollPace.fromStoredName("NOT_A_PACE"))
        assertNull(AutoScrollPace.fromStoredName(null))
    }

    @Test
    fun autoScrollStateHasExactlyTheFiveDocumentedTransitionTargets() {
        assertEquals(
            setOf("INACTIVE", "STARTING", "RUNNING", "PAUSED", "COMPLETED"),
            AutoScrollState.entries.map { it.name }.toSet(),
        )
    }
}
