package org.amanahquran.app.core.daily

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DailyAyahSelectorTest {
    private val keys = listOf("1:1", "1:2", "1:3", "2:1")

    @Test fun sameDateIsDeterministic() {
        val date = LocalDate.of(2026, 8, 22)
        assertEquals(DailyAyahSelector.sequentialKey(date, keys.size, keys), DailyAyahSelector.sequentialKey(date, keys.size, keys))
    }

    @Test fun differentDatesAdvanceStableSequence() {
        assertNotEquals(
            DailyAyahSelector.sequentialKey(LocalDate.of(2026, 8, 22), keys.size, keys),
            DailyAyahSelector.sequentialKey(LocalDate.of(2026, 8, 23), keys.size, keys),
        )
    }

    @Test fun reviewedRandomAvoidsRecentWhenCandidatesExist() {
        assertEquals("2:1", DailyAyahSelector.reviewedRandomKey(LocalDate.of(2026, 8, 22), keys, setOf("1:1", "1:2", "1:3")))
    }
}
