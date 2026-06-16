package org.amanahquran.app.content.validation

import org.amanahquran.app.core.database.dao.*
import org.amanahquran.app.core.model.ScriptType

class ContentValidationEngine(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val quranTextDao: QuranTextDao,
    private val searchIndexDao: SearchIndexDao,
    private val contentSourceDao: ContentSourceDao
) {
    private val rules = listOf(
        SurahCountRule(),
        AyahCountRule(),
        NoDuplicateAyahKeysRule(),
        NoBlankDisplayTextRule(ScriptType.INDOPAK),
        NoBlankDisplayTextRule(ScriptType.UTHMANI),
        SearchIndexCompletenessRule(),
        ContentSourceCompletenessRule()
    )

    suspend fun runAllValidations(): List<ContentValidationResult> {
        return rules.map { rule ->
            rule.validate(
                surahDao,
                ayahDao,
                quranTextDao,
                searchIndexDao,
                contentSourceDao
            )
        }
    }
}
