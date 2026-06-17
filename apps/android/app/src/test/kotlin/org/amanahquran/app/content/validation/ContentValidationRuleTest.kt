package org.amanahquran.app.content.validation

import kotlinx.coroutines.runBlocking
import org.amanahquran.app.core.database.dao.*
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.ValidationSeverity
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

class ContentValidationRuleTest {

    @Test
    fun `SurahCountRule fails if count is not 114`() = runBlocking {
        val surahDao = mock(SurahDao::class.java)
        `when`(surahDao.getSurahCount()).thenReturn(100)
        
        val rule = SurahCountRule()
        val result = rule.validate(surahDao, mock(AyahDao::class.java), mock(QuranTextDao::class.java), mock(SearchIndexDao::class.java), mock(ContentSourceDao::class.java))
        
        assertFalse(result.passed)
        assertEquals("114", result.expectedValue)
        assertEquals("100", result.actualValue)
        assertEquals(ValidationSeverity.CRITICAL, result.severity)
    }

    @Test
    fun `SurahCountRule passes if count is 114`() = runBlocking {
        val surahDao = mock(SurahDao::class.java)
        `when`(surahDao.getSurahCount()).thenReturn(114)
        
        val rule = SurahCountRule()
        val result = rule.validate(surahDao, mock(AyahDao::class.java), mock(QuranTextDao::class.java), mock(SearchIndexDao::class.java), mock(ContentSourceDao::class.java))
        
        assertTrue(result.passed)
    }

    @Test
    fun `AyahCountRule fails if count is not 6236`() = runBlocking {
        val ayahDao = mock(AyahDao::class.java)
        `when`(ayahDao.getAyahCount()).thenReturn(6000)
        
        val rule = AyahCountRule()
        val result = rule.validate(mock(SurahDao::class.java), ayahDao, mock(QuranTextDao::class.java), mock(SearchIndexDao::class.java), mock(ContentSourceDao::class.java))
        
        assertFalse(result.passed)
        assertEquals("6236", result.expectedValue)
    }

    @Test
    fun `NoBlankDisplayTextRule fails if blank text exists`() = runBlocking {
        val quranTextDao = mock(QuranTextDao::class.java)
        `when`(quranTextDao.getBlankDisplayTextCount(ScriptType.INDOPAK.name)).thenReturn(5)
        
        val rule = NoBlankDisplayTextRule(ScriptType.INDOPAK)
        val result = rule.validate(mock(SurahDao::class.java), mock(AyahDao::class.java), quranTextDao, mock(SearchIndexDao::class.java), mock(ContentSourceDao::class.java))
        
        assertFalse(result.passed)
        assertEquals("5", result.actualValue)
    }
}
