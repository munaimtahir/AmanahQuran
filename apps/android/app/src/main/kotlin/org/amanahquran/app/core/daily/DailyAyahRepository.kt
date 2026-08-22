package org.amanahquran.app.core.daily

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import org.amanahquran.app.content.translation.TranslationRepository
import org.amanahquran.app.core.datastore.amanahPreferencesDataSource
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.QuranContentRepositoryImpl
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.core.database.AmanahContentDatabaseProvider
import java.time.LocalDate
import java.time.ZoneId
import org.json.JSONArray
import org.json.JSONObject

interface DailyAyahRepository {
    suspend fun getToday(zoneId: ZoneId = ZoneId.systemDefault()): DailyAyahContent?
    suspend fun getForDate(date: LocalDate): DailyAyahContent?
    suspend fun history(limit: Int = 30): List<DailyAyahRecord>
}

class DailyAyahRepositoryImpl(
    private val quran: QuranContentRepository,
    private val settings: ReaderSettingsRepository,
    private val translations: TranslationRepository,
    private val store: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>,
    private val eligibility: List<DailyAyahEligibility> = emptyList(),
) : DailyAyahRepository {
    override suspend fun getToday(zoneId: ZoneId): DailyAyahContent? = getForDate(LocalDate.now(zoneId))

    override suspend fun getForDate(date: LocalDate): DailyAyahContent? {
        val currentSettings = settings.settings.first()
        val existing = records().firstOrNull { it.date == date }
        val record = existing ?: createRecord(date, currentSettings.selectedScript, currentSettings.translationSelection.translationId)
            ?: return null
        val ayah = quran.getReaderAyah(record.ayahKey, currentSettings.selectedScript.name) ?: return null
        val translation = record.translationId?.let { translations.getAyah(it, record.ayahKey)?.displayText }
        return DailyAyahContent(record, ayah.displayText, translation, ayah.surahNameSimple, ayah.ayahNumber)
    }

    override suspend fun history(limit: Int): List<DailyAyahRecord> = records().take(limit.coerceIn(1, 30))

    private suspend fun createRecord(date: LocalDate, script: ScriptType, translationId: String?): DailyAyahRecord? {
        // A curated eligibility file is intentionally not inferred from content. Until scholarly
        // review supplies one, use a stable sequential walk over the verified packaged corpus.
        val allKeys = quran.getReaderAyahs(
            org.amanahquran.app.core.model.ReaderOpenMode.Surah(1), script.name,
        ).let { firstSurah ->
            // The database repository exposes ordered slices, so obtain the canonical key order
            // from the 114 verified surahs without touching display text.
            buildList {
                quran.getAllSurahs().forEach { surah ->
                    quran.getAyahsForSurah(surah.number, script.name).forEach { add(it.ayahKey) }
                }
            }
        }
        val reviewedKeys = eligibility.filter { it.eligible && it.reviewStatus == "APPROVED" }.map { it.ayahKey }
        val recent = records().take(30).map { it.ayahKey }.toSet()
        val mode = if (reviewedKeys.isNotEmpty()) DailyAyahSelectionMode.CURATED else DailyAyahSelectionMode.SEQUENTIAL
        val key = if (mode == DailyAyahSelectionMode.CURATED) {
            DailyAyahSelector.reviewedRandomKey(date, reviewedKeys, recent)
        } else {
            DailyAyahSelector.sequentialKey(date, allKeys.size, allKeys)
        } ?: return null
        val record = DailyAyahRecord(date, key, mode, translationId)
        val previous = records()
        store.edit { it[Keys.history] = (listOf(record) + previous.filterNot { r -> r.date == date }).take(30).toJson().toString() }
        return record
    }

    private suspend fun records(): List<DailyAyahRecord> = runCatching {
        val array = JSONArray(store.data.first()[Keys.history].orEmpty())
        buildList {
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val date = runCatching { LocalDate.parse(item.optString("date")) }.getOrNull() ?: continue
                val key = item.optString("ayahKey").takeIf(String::isNotBlank) ?: continue
                val mode = runCatching { DailyAyahSelectionMode.valueOf(item.optString("mode")) }.getOrDefault(DailyAyahSelectionMode.SEQUENTIAL)
                add(DailyAyahRecord(date, key, mode, item.optString("translationId").takeIf(String::isNotBlank)))
            }
        }.sortedByDescending { it.date }
    }.getOrDefault(emptyList())

    private fun List<DailyAyahRecord>.toJson(): JSONArray = JSONArray().also { array ->
        forEach { record ->
            array.put(JSONObject().put("date", record.date.toString()).put("ayahKey", record.ayahKey)
                .put("mode", record.selectionMode.name).put("translationId", record.translationId ?: ""))
        }
    }

    private object Keys { val history = stringPreferencesKey("daily_ayah_history_v1") }
}

fun dailyAyahRepository(context: Context): DailyAyahRepository {
    val db = AmanahContentDatabaseProvider.getDatabase(context)
    val quran = QuranContentRepositoryImpl(db.surahDao(), db.ayahDao(), db.quranTextDao(), db.mushafLayoutReferenceDao())
    return DailyAyahRepositoryImpl(
        quran = quran,
        settings = readerSettingsRepository(context),
        translations = TranslationRepository(context),
        store = amanahPreferencesDataSource(context).dataStore,
    )
}
