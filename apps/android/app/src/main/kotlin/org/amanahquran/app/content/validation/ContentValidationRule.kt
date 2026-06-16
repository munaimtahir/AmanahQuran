package org.amanahquran.app.content.validation

import org.amanahquran.app.core.database.dao.*
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.ValidationSeverity

sealed interface ContentValidationRule {
    val name: String
    suspend fun validate(
        surahDao: SurahDao,
        ayahDao: AyahDao,
        quranTextDao: QuranTextDao,
        searchIndexDao: SearchIndexDao,
        contentSourceDao: ContentSourceDao
    ): ContentValidationResult
}

class SurahCountRule : ContentValidationRule {
    override val name = "SurahCountRule"
    override suspend fun validate(
        surahDao: SurahDao, ayahDao: AyahDao, quranTextDao: QuranTextDao,
        searchIndexDao: SearchIndexDao, contentSourceDao: ContentSourceDao
    ): ContentValidationResult {
        val count = surahDao.getSurahCount()
        val expected = 114
        return ContentValidationResult(
            validationName = name,
            expectedValue = expected.toString(),
            actualValue = count.toString(),
            passed = count == expected,
            severity = ValidationSeverity.CRITICAL
        )
    }
}

class AyahCountRule : ContentValidationRule {
    override val name = "AyahCountRule"
    override suspend fun validate(
        surahDao: SurahDao, ayahDao: AyahDao, quranTextDao: QuranTextDao,
        searchIndexDao: SearchIndexDao, contentSourceDao: ContentSourceDao
    ): ContentValidationResult {
        val count = ayahDao.getAyahCount()
        val expected = 6236
        return ContentValidationResult(
            validationName = name,
            expectedValue = expected.toString(),
            actualValue = count.toString(),
            passed = count == expected,
            severity = ValidationSeverity.CRITICAL
        )
    }
}

class NoDuplicateAyahKeysRule : ContentValidationRule {
    override val name = "NoDuplicateAyahKeysRule"
    override suspend fun validate(
        surahDao: SurahDao, ayahDao: AyahDao, quranTextDao: QuranTextDao,
        searchIndexDao: SearchIndexDao, contentSourceDao: ContentSourceDao
    ): ContentValidationResult {
        val duplicateCount = ayahDao.getDuplicateAyahKeyCount()
        return ContentValidationResult(
            validationName = name,
            expectedValue = "0",
            actualValue = duplicateCount.toString(),
            passed = duplicateCount == 0,
            severity = ValidationSeverity.CRITICAL
        )
    }
}

class NoBlankDisplayTextRule(private val scriptType: ScriptType) : ContentValidationRule {
    override val name = "NoBlankDisplayTextRule_${scriptType.name}"
    override suspend fun validate(
        surahDao: SurahDao, ayahDao: AyahDao, quranTextDao: QuranTextDao,
        searchIndexDao: SearchIndexDao, contentSourceDao: ContentSourceDao
    ): ContentValidationResult {
        val blankCount = quranTextDao.getBlankDisplayTextCount(scriptType)
        return ContentValidationResult(
            validationName = name,
            expectedValue = "0",
            actualValue = blankCount.toString(),
            passed = blankCount == 0,
            severity = ValidationSeverity.CRITICAL
        )
    }
}

class SearchIndexCompletenessRule : ContentValidationRule {
    override val name = "SearchIndexCompletenessRule"
    override suspend fun validate(
        surahDao: SurahDao, ayahDao: AyahDao, quranTextDao: QuranTextDao,
        searchIndexDao: SearchIndexDao, contentSourceDao: ContentSourceDao
    ): ContentValidationResult {
        val ayahCount = ayahDao.getAyahCount()
        val searchIndexCount = searchIndexDao.getSearchIndexCount()
        return ContentValidationResult(
            validationName = name,
            expectedValue = ayahCount.toString(),
            actualValue = searchIndexCount.toString(),
            passed = ayahCount == searchIndexCount,
            severity = ValidationSeverity.ERROR
        )
    }
}

class ContentSourceCompletenessRule : ContentValidationRule {
    override val name = "ContentSourceCompletenessRule"
    override suspend fun validate(
        surahDao: SurahDao, ayahDao: AyahDao, quranTextDao: QuranTextDao,
        searchIndexDao: SearchIndexDao, contentSourceDao: ContentSourceDao
    ): ContentValidationResult {
        val blankChecksumCount = contentSourceDao.getBlankChecksumCount()
        return ContentValidationResult(
            validationName = name,
            expectedValue = "0",
            actualValue = blankChecksumCount.toString(),
            passed = blankChecksumCount == 0,
            severity = ValidationSeverity.CRITICAL
        )
    }
}
