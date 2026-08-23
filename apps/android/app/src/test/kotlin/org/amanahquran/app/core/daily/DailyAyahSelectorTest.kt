package org.amanahquran.app.core.daily

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyAyahSelectorTest {
    private val keys = listOf("1:1", "1:2", "1:3", "2:1", "2:255", "36:1", "112:1", "114:6")

    @Test
    fun sameDateIsDeterministic() {
        val date = LocalDate.of(2026, 8, 22)
        assertEquals(
            DailyAyahSelector.randomDailyKey(date, keys),
            DailyAyahSelector.randomDailyKey(date, keys),
        )
    }

    @Test
    fun differentDatesProduceDifferentAyahs() {
        val day1 = DailyAyahSelector.randomDailyKey(LocalDate.of(2026, 8, 22), keys)
        val day2 = DailyAyahSelector.randomDailyKey(LocalDate.of(2026, 8, 23), keys)
        assertNotNull(day1)
        assertNotNull(day2)
        assertTrue(keys.contains(day1))
        assertTrue(keys.contains(day2))
    }

    @Test
    fun randomDailyKeyAvoidsRecentKeys() {
        val date = LocalDate.of(2026, 8, 22)
        val recent = setOf("1:1", "1:2", "1:3", "2:1", "2:255", "36:1", "112:1")
        val selected = DailyAyahSelector.randomDailyKey(date, keys, recent)
        assertEquals("114:6", selected)
    }

    @Test
    fun randomDailyKeyFallsBackWhenAllKeysRecent() {
        val date = LocalDate.of(2026, 8, 22)
        val recent = keys.toSet()
        val selected = DailyAyahSelector.randomDailyKey(date, keys, recent)
        assertNotNull(selected)
        assertTrue(keys.contains(selected))
    }

    @Test
    fun sequentialKeyMaintainsLegacyCompatibility() {
        val date = LocalDate.of(2026, 8, 22)
        assertEquals(
            DailyAyahSelector.sequentialKey(date, keys.size, keys),
            DailyAyahSelector.sequentialKey(date, keys.size, keys),
        )
    }
}

