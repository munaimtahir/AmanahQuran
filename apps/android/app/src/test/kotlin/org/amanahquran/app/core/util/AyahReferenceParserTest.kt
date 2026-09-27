package org.amanahquran.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AyahReferenceParserTest {
    @Test
    fun parsesFullReferences_includingBoundaryAyahs() {
        assertEquals(AyahReference(1, 1), AyahReferenceParser.parse("1:1", defaultSurah = 5))
        assertEquals(AyahReference(2, 286), AyahReferenceParser.parse("2:286", defaultSurah = null))
        assertEquals(AyahReference(114, 6), AyahReferenceParser.parse(" 114 : 6 ", defaultSurah = null))
        assertEquals(AyahReference(2, 255), AyahReferenceParser.parse("2.255", defaultSurah = null))
        assertEquals("2:255", AyahReferenceParser.parse("2 255", defaultSurah = null)?.ayahKey)
    }

    @Test
    fun bareAyahNumber_resolvesAgainstCurrentSurah() {
        assertEquals(AyahReference(36, 58), AyahReferenceParser.parse("58", defaultSurah = 36))
        assertNull(AyahReferenceParser.parse("58", defaultSurah = null))
    }

    @Test
    fun rejectsOutOfRangeAndMalformedInput() {
        assertNull(AyahReferenceParser.parse("115:1", defaultSurah = 1))
        assertNull(AyahReferenceParser.parse("0:1", defaultSurah = 1))
        assertNull(AyahReferenceParser.parse("2:0", defaultSurah = 1))
        assertNull(AyahReferenceParser.parse("0", defaultSurah = 1))
        assertNull(AyahReferenceParser.parse("abc", defaultSurah = 1))
        assertNull(AyahReferenceParser.parse("", defaultSurah = 1))
        assertNull(AyahReferenceParser.parse("2:255:1", defaultSurah = 1))
    }
}
