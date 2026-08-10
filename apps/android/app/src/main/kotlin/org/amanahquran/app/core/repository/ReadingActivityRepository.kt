package org.amanahquran.app.core.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSource
import org.amanahquran.app.core.model.DailyReadingActivity
import org.amanahquran.app.core.model.StreakSummary
import org.amanahquran.app.core.util.StreakCalculator
import org.json.JSONArray
import org.json.JSONObject

interface ReadingActivityRepository {
    fun observeAllActivity(): Flow<List<DailyReadingActivity>>
    suspend fun getActivityForDate(date: LocalDate): DailyReadingActivity?

    /**
     * Merges a completed (or checkpointed) reading interval into [date]'s entry. Ayah/page sets
     * are unioned so re-reading the same content never inflates unique counts.
     */
    suspend fun recordSession(
        date: LocalDate,
        additionalDurationSeconds: Long,
        ayahKeysRead: Set<String> = emptySet(),
        pagesRead: Set<Int> = emptySet(),
        timestamp: Long = System.currentTimeMillis(),
    )

    suspend fun getStreakSummary(today: LocalDate = LocalDate.now(ZoneId.systemDefault())): StreakSummary

    /** Reactive counterpart of [getStreakSummary], recomputed whenever stored activity changes. */
    fun observeStreakSummary(today: () -> LocalDate = { LocalDate.now(ZoneId.systemDefault()) }): Flow<StreakSummary>

    suspend fun replaceAllActivity(records: List<DailyReadingActivity>)
}

class ReadingActivityRepositoryImpl(
    private val dataSource: AmanahPreferencesDataSource,
) : ReadingActivityRepository {
    private val activityFlow = dataSource.dataStore.data.map { preferences ->
        preferences[Keys.activityJson].orEmpty().toActivityList()
    }

    override fun observeAllActivity(): Flow<List<DailyReadingActivity>> = activityFlow

    override suspend fun getActivityForDate(date: LocalDate): DailyReadingActivity? =
        observeAllActivity().first().firstOrNull { it.date == date }

    override suspend fun recordSession(
        date: LocalDate,
        additionalDurationSeconds: Long,
        ayahKeysRead: Set<String>,
        pagesRead: Set<Int>,
        timestamp: Long,
    ): Unit = withContext(NonCancellable) {
        if (additionalDurationSeconds <= 0 && ayahKeysRead.isEmpty() && pagesRead.isEmpty()) return@withContext
        dataSource.dataStore.edit { preferences ->
            val records = preferences[Keys.activityJson].orEmpty().toActivityList().toMutableList()
            val existingIndex = records.indexOfFirst { it.date == date }
            if (existingIndex >= 0) {
                records[existingIndex] = records[existingIndex].mergedWith(
                    additionalDurationSeconds = additionalDurationSeconds,
                    additionalAyahKeys = ayahKeysRead,
                    additionalPages = pagesRead,
                    timestamp = timestamp,
                )
            } else {
                records.add(
                    DailyReadingActivity.startingSession(
                        date = date,
                        durationSeconds = additionalDurationSeconds,
                        ayahKeysRead = ayahKeysRead,
                        pagesRead = pagesRead,
                        timestamp = timestamp,
                    ),
                )
            }
            preferences[Keys.activityJson] = records.toJsonArray().toString()
        }
    }

    override suspend fun getStreakSummary(today: LocalDate): StreakSummary {
        val qualifyingDates = observeAllActivity().first()
            .filter { it.qualifiedForReadingDay }
            .map { it.date }
            .toSet()
        return StreakCalculator.calculate(qualifyingDates, today)
    }

    override fun observeStreakSummary(today: () -> LocalDate): Flow<StreakSummary> = activityFlow.map { records ->
        val qualifyingDates = records.filter { it.qualifiedForReadingDay }.map { it.date }.toSet()
        StreakCalculator.calculate(qualifyingDates, today())
    }

    override suspend fun replaceAllActivity(records: List<DailyReadingActivity>): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.activityJson] = records.toJsonArray().toString()
        }
    }

    private fun String.toActivityList(): List<DailyReadingActivity> {
        if (isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(this)
            buildList {
                for (index in 0 until array.length()) {
                    array.optJSONObject(index)?.toDailyReadingActivity()?.let(::add)
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun List<DailyReadingActivity>.toJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { array.put(it.toJson()) }
        return array
    }

    private fun DailyReadingActivity.toJson(): JSONObject = JSONObject()
        .put("date", date.toString())
        .put("durationSeconds", readingDurationSeconds)
        .put("ayahKeys", JSONArray(ayahKeysRead.toList()))
        .put("pages", JSONArray(pagesRead.toList()))
        .put("firstTimestamp", firstReadingTimestamp)
        .put("lastTimestamp", lastReadingTimestamp)

    private fun JSONObject.toDailyReadingActivity(): DailyReadingActivity? {
        val date = runCatching { LocalDate.parse(optString("date")) }.getOrNull() ?: return null
        val ayahKeysArray = optJSONArray("ayahKeys") ?: JSONArray()
        val pagesArray = optJSONArray("pages") ?: JSONArray()
        return DailyReadingActivity(
            date = date,
            readingDurationSeconds = optLong("durationSeconds"),
            ayahKeysRead = buildSet { for (i in 0 until ayahKeysArray.length()) add(ayahKeysArray.optString(i)) },
            pagesRead = buildSet { for (i in 0 until pagesArray.length()) add(pagesArray.optInt(i)) },
            firstReadingTimestamp = optLong("firstTimestamp"),
            lastReadingTimestamp = optLong("lastTimestamp"),
        )
    }

    private object Keys {
        val activityJson = stringPreferencesKey("reading_activity_json")
    }
}

fun readingActivityRepository(context: Context): ReadingActivityRepository =
    ReadingActivityRepositoryImpl(amanahPreferencesDataSource(context))
