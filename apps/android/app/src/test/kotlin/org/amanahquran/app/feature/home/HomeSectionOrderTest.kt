package org.amanahquran.app.feature.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeSectionOrderTest {
    @Test
    fun browseShortcuts_followContinueReadingDirectly() {
        val order = homeSectionOrder()
        assertEquals(HomeSection.HEADER, order[0])
        assertEquals(HomeSection.CONTINUE_READING, order[1])
        // Search and Bookmarks live in BROWSE; keeping it third keeps them above the fold.
        assertEquals(HomeSection.BROWSE, order[2])
    }

    @Test
    fun everySectionAppearsExactlyOnce() {
        assertEquals(HomeSection.entries.toSet(), homeSectionOrder().toSet())
        assertEquals(HomeSection.entries.size, homeSectionOrder().size)
    }
}
