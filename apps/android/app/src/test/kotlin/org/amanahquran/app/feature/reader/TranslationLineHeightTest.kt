package org.amanahquran.app.feature.reader

import org.amanahquran.app.core.model.TranslationDirection
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslationLineHeightTest {
    @Test
    fun urduNastaliq_getsMoreLeadingThanEnglish_atEverySize() {
        for (size in 14..40) {
            val urdu = translationLineHeightSp(size.toFloat(), TranslationDirection.RTL)
            val english = translationLineHeightSp(size.toFloat(), TranslationDirection.LTR)
            assertTrue("urdu at $size", urdu > english)
            assertTrue("min 1.4x at $size", english >= size * 1.4f && urdu >= size * 1.4f)
        }
    }
}
