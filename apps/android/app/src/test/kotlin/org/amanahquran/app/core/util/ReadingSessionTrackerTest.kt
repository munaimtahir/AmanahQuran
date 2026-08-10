package org.amanahquran.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingSessionTrackerTest {
    private class FakeClock(var millis: Long = 0L) {
        fun advance(seconds: Long) {
            millis += seconds * 1000
        }
    }

    @Test
    fun pauseWithoutResumeAccumulatesNothing() {
        val clock = FakeClock()
        val tracker = ReadingSessionTracker { clock.millis }
        tracker.pause()
        assertEquals(0, tracker.drainAccumulatedSeconds())
    }

    @Test
    fun resumeThenPauseAccumulatesElapsedSeconds() {
        val clock = FakeClock()
        val tracker = ReadingSessionTracker { clock.millis }
        tracker.resume()
        clock.advance(90)
        tracker.pause()
        assertEquals(90, tracker.drainAccumulatedSeconds())
        assertFalse(tracker.isTracking)
    }

    @Test
    fun timeWhileNotResumedIsNotCounted() {
        val clock = FakeClock()
        val tracker = ReadingSessionTracker { clock.millis }
        tracker.resume()
        clock.advance(30)
        tracker.pause()
        clock.advance(600) // background time — never resumed
        tracker.resume()
        clock.advance(20)
        tracker.pause()
        assertEquals(50, tracker.drainAccumulatedSeconds())
    }

    @Test
    fun drainDuringActiveIntervalCheckpointsWithoutEndingTracking() {
        val clock = FakeClock()
        val tracker = ReadingSessionTracker { clock.millis }
        tracker.resume()
        clock.advance(45)
        assertEquals(45, tracker.drainAccumulatedSeconds())
        assertTrue(tracker.isTracking)

        clock.advance(15)
        tracker.pause()
        assertEquals(15, tracker.drainAccumulatedSeconds())
    }

    @Test
    fun drainResetsAccumulatorSoValuesAreNotDoubleCounted() {
        val clock = FakeClock()
        val tracker = ReadingSessionTracker { clock.millis }
        tracker.resume()
        clock.advance(10)
        tracker.pause()
        assertEquals(10, tracker.drainAccumulatedSeconds())
        assertEquals(0, tracker.drainAccumulatedSeconds())
    }
}
