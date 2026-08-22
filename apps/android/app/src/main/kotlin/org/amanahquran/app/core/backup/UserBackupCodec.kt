package org.amanahquran.app.core.backup

import java.time.LocalDate
import org.amanahquran.app.core.model.AutoScrollPace
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.DailyReadingActivity
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderHeaderFormat
import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ReminderSettings
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.TranslationSelection
import org.amanahquran.app.core.repository.BookmarkRecord
import org.amanahquran.app.core.repository.ReaderSettings
import org.amanahquran.app.core.repository.LastReadState
import org.amanahquran.app.core.model.ValidationHelpers
import org.amanahquran.app.core.theme.ThemeMode
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek

data class UserBackupPayload(
    val bookmarks: List<BookmarkRecord>,
    val collectionsJson: String,
    val settings: ReaderSettings,
    val lastRead: LastReadState? = null,
    val readingActivity: List<DailyReadingActivity> = emptyList(),
    val reminderSettings: ReminderSettings = ReminderSettings(),
)

object UserBackupCodec {
    /**
     * v1: bookmarks/collections/a subset of settings/last-read only. v2 adds reading activity,
     * reminder settings, and the remaining reader settings fields (zoom levels, auto-scroll pace,
     * content mode, header format, keep-awake/full-screen defaults) that v1 never captured.
     * Both versions are accepted on import -- v1 fields simply default -- so a backup exported by
     * an earlier release still restores cleanly instead of being rejected as incompatible.
     */
    const val CURRENT_VERSION = 2
    private val SUPPORTED_VERSIONS = setOf(1, 2)

    fun encode(payload: UserBackupPayload): String = JSONObject().apply {
        put("format", "amanah-quran-user-backup")
        put("version", CURRENT_VERSION)
        put("bookmarks", JSONArray().apply { payload.bookmarks.forEach { put(bookmarkToJson(it)) } })
        put("collections", JSONArray(payload.collectionsJson))
        put("settings", settingsToJson(payload.settings))
        payload.lastRead?.let { lastRead -> put("lastRead", JSONObject().apply {
            put("ayahKey", lastRead.ayahKey)
            put("surahNumber", lastRead.surahNumber)
            put("ayahNumber", lastRead.ayahNumber)
            put("pageNumber", lastRead.pageNumber)
            put("juzNumber", lastRead.juzNumber)
            put("scriptType", lastRead.scriptType.name)
            put("updatedAt", lastRead.updatedAt)
        }) }
        put("readingActivity", JSONArray().apply { payload.readingActivity.forEach { put(activityToJson(it)) } })
        put("reminderSettings", reminderSettingsToJson(payload.reminderSettings))
    }.toString(2)

    fun validateAndParse(json: String): UserBackupPayload {
        val root = JSONObject(json)
        require(root.optString("format") == "amanah-quran-user-backup") { "Unsupported backup format" }
        val version = root.optInt("version", -1)
        require(version in SUPPORTED_VERSIONS) { "Unsupported backup version" }

        val bookmarks = buildList {
            val array = root.optJSONArray("bookmarks") ?: JSONArray()
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val type = runCatching { BookmarkType.valueOf(item.getString("bookmarkType")) }.getOrNull()
                    ?: error("Invalid bookmark type")
                val ayahKey = item.optString("ayahKey").takeIf { it.isNotBlank() }
                require(ayahKey == null || ValidationHelpers.isValidAyahKey(ayahKey)) { "Invalid canonical ayah key" }
                val pageReferenceType = item.optString("pageReferenceType")
                    .takeIf { it.isNotBlank() }
                    ?.let { runCatching { PageReferenceType.valueOf(it) }.getOrNull() }
                require(type != BookmarkType.PAGE || pageReferenceType != null) { "Page bookmark is missing page reference type" }
                add(BookmarkRecord(item.getLong("id"), type, ayahKey, item.optIntOrNull("surahNumber"), item.optIntOrNull("ayahNumber"), item.optIntOrNull("pageNumber"), pageReferenceType, item.getLong("createdAt"), item.getLong("updatedAt")))
            }
        }
        require(root.optJSONArray("collections") != null) { "Missing collections" }
        val settingsJson = root.optJSONObject("settings") ?: error("Missing settings")
        val settings = settingsFromJson(settingsJson)
        val lastRead = root.optJSONObject("lastRead")?.let { item ->
            val ayahKey = item.optString("ayahKey")
            require(ValidationHelpers.isValidAyahKey(ayahKey)) { "Invalid last-read ayah key" }
            LastReadState(
                ayahKey = ayahKey,
                surahNumber = item.getInt("surahNumber"),
                ayahNumber = item.getInt("ayahNumber"),
                pageNumber = item.optIntOrNull("pageNumber"),
                juzNumber = item.optIntOrNull("juzNumber"),
                scriptType = enumOrDefault(item.optString("scriptType"), ScriptType.INDOPAK),
                updatedAt = item.optLong("updatedAt"),
            )
        }
        val readingActivity = buildList {
            val array = root.optJSONArray("readingActivity") ?: JSONArray()
            for (index in 0 until array.length()) {
                array.optJSONObject(index)?.let { activityFromJson(it) }?.let(::add)
            }
        }
        val reminderSettings = root.optJSONObject("reminderSettings")?.let { reminderSettingsFromJson(it) } ?: ReminderSettings()

        return UserBackupPayload(bookmarks, root.getJSONArray("collections").toString(), settings, lastRead, readingActivity, reminderSettings)
    }

    private fun settingsToJson(settings: ReaderSettings) = JSONObject().apply {
        put("script", settings.selectedScript.name)
        put("theme", settings.selectedTheme.name)
        put("arabicFontSizeSp", settings.arabicFontSizeSp)
        put("elderMode", settings.elderModeEnabled)
        put("bookMode", settings.bookModeEnabled)
        put("translationSelection", settings.translationSelection.name)
        put("translationFontSizeSp", settings.translationFontSizeSp)
        put("arabicLineSpacingMultiplier", settings.arabicLineSpacingMultiplier)
        put("readerHorizontalPaddingDp", settings.readerHorizontalPaddingDp)
        put("indoPakZoomLevel", settings.indoPakZoomLevel.name)
        put("uthmaniZoomLevel", settings.uthmaniZoomLevel.name)
        put("indoPakElderZoomLevel", settings.indoPakElderZoomLevel.name)
        put("uthmaniElderZoomLevel", settings.uthmaniElderZoomLevel.name)
        put("autoScrollPace", settings.autoScrollPace.name)
        put("pinchToResizeEnabled", settings.pinchToResizeEnabled)
        put("readerContentMode", settings.readerContentMode.name)
        put("translationZoomLevel", settings.translationZoomLevel.name)
        put("linkedZoomEnabled", settings.linkedZoomEnabled)
        put("readerHeaderFormat", settings.readerHeaderFormat.name)
        put("keepScreenAwakeEnabled", settings.keepScreenAwakeEnabled)
        put("fullScreenReadingDefault", settings.fullScreenReadingDefault)
    }

    private fun settingsFromJson(settingsJson: JSONObject): ReaderSettings = ReaderSettings(
        selectedScript = enumOrDefault(settingsJson.optString("script"), ScriptType.INDOPAK),
        selectedTheme = enumOrDefault(settingsJson.optString("theme"), ThemeMode.SYSTEM),
        arabicFontSizeSp = settingsJson.optDouble("arabicFontSizeSp", 24.0).toFloat().coerceIn(16f, 42f),
        elderModeEnabled = settingsJson.optBoolean("elderMode", false),
        bookModeEnabled = settingsJson.optBoolean("bookMode", false),
        // A backup written by an app version before the Manifest/Irfan translation integration
        // carries the old boolean flag instead -- fall back to it the same way the live DataStore
        // migration does (see ReaderSettingsRepository), rather than silently restoring to Off.
        translationSelection = TranslationSelection.fromStoredName(settingsJson.optString("translationSelection").ifBlank { null })
            ?: if (settingsJson.optBoolean("translationEnabled", false)) TranslationSelection.IRFAN_UR else TranslationSelection.OFF,
        translationFontSizeSp = settingsJson.optDouble("translationFontSizeSp", 18.0).toFloat().coerceIn(14f, 32f),
        arabicLineSpacingMultiplier = settingsJson.optDouble("arabicLineSpacingMultiplier", 1.88).toFloat().coerceIn(1.5f, 2.4f),
        readerHorizontalPaddingDp = settingsJson.optDouble("readerHorizontalPaddingDp", 16.0).toFloat().coerceIn(8f, 32f),
        indoPakZoomLevel = zoomOrDefault(settingsJson.optString("indoPakZoomLevel"), ReaderZoomLevel.default),
        uthmaniZoomLevel = zoomOrDefault(settingsJson.optString("uthmaniZoomLevel"), ReaderZoomLevel.default),
        indoPakElderZoomLevel = zoomOrDefault(settingsJson.optString("indoPakElderZoomLevel"), ReaderZoomLevel.elderDefault),
        uthmaniElderZoomLevel = zoomOrDefault(settingsJson.optString("uthmaniElderZoomLevel"), ReaderZoomLevel.elderDefault),
        autoScrollPace = AutoScrollPace.fromStoredName(settingsJson.optString("autoScrollPace")) ?: AutoScrollPace.default,
        pinchToResizeEnabled = settingsJson.optBoolean("pinchToResizeEnabled", true),
        readerContentMode = ReaderContentMode.fromStoredName(settingsJson.optString("readerContentMode")) ?: ReaderContentMode.default,
        translationZoomLevel = zoomOrDefault(settingsJson.optString("translationZoomLevel"), ReaderZoomLevel.default),
        linkedZoomEnabled = settingsJson.optBoolean("linkedZoomEnabled", true),
        readerHeaderFormat = enumOrDefault(settingsJson.optString("readerHeaderFormat"), ReaderHeaderFormat.SURAH_PAGE),
        keepScreenAwakeEnabled = settingsJson.optBoolean("keepScreenAwakeEnabled", false),
        fullScreenReadingDefault = settingsJson.optBoolean("fullScreenReadingDefault", true),
    )

    private fun activityToJson(activity: DailyReadingActivity) = JSONObject().apply {
        put("date", activity.date.toString())
        put("durationSeconds", activity.readingDurationSeconds)
        put("ayahKeys", JSONArray(activity.ayahKeysRead.toList()))
        put("pages", JSONArray(activity.pagesRead.toList()))
        put("firstTimestamp", activity.firstReadingTimestamp)
        put("lastTimestamp", activity.lastReadingTimestamp)
    }

    private fun activityFromJson(item: JSONObject): DailyReadingActivity? {
        val date = runCatching { LocalDate.parse(item.optString("date")) }.getOrNull() ?: return null
        val ayahKeysArray = item.optJSONArray("ayahKeys") ?: JSONArray()
        val pagesArray = item.optJSONArray("pages") ?: JSONArray()
        return DailyReadingActivity(
            date = date,
            readingDurationSeconds = item.optLong("durationSeconds"),
            ayahKeysRead = buildSet { for (i in 0 until ayahKeysArray.length()) add(ayahKeysArray.optString(i)) },
            pagesRead = buildSet { for (i in 0 until pagesArray.length()) add(pagesArray.optInt(i)) },
            firstReadingTimestamp = item.optLong("firstTimestamp"),
            lastReadingTimestamp = item.optLong("lastTimestamp"),
        )
    }

    private fun reminderSettingsToJson(settings: ReminderSettings) = JSONObject().apply {
        put("enabled", settings.enabled)
        put("hour", settings.hour)
        put("minute", settings.minute)
        put("repeatDays", JSONArray(settings.repeatDays.map { it.name }))
        put("smartReminderEnabled", settings.smartReminderEnabled)
    }

    private fun reminderSettingsFromJson(json: JSONObject): ReminderSettings {
        val daysArray = json.optJSONArray("repeatDays")
        val days = if (daysArray != null) {
            buildSet {
                for (i in 0 until daysArray.length()) {
                    runCatching { DayOfWeek.valueOf(daysArray.getString(i)) }.getOrNull()?.let(::add)
                }
            }
        } else {
            DayOfWeek.entries.toSet()
        }
        return ReminderSettings(
            enabled = json.optBoolean("enabled", false),
            hour = json.optInt("hour", 20),
            minute = json.optInt("minute", 0),
            repeatDays = days,
            smartReminderEnabled = json.optBoolean("smartReminderEnabled", true),
        )
    }

    private fun bookmarkToJson(bookmark: BookmarkRecord) = JSONObject().apply {
        put("id", bookmark.id)
        put("bookmarkType", bookmark.bookmarkType.name)
        put("ayahKey", bookmark.ayahKey)
        put("surahNumber", bookmark.surahNumber)
        put("ayahNumber", bookmark.ayahNumber)
        put("pageNumber", bookmark.pageNumber)
        put("pageReferenceType", bookmark.pageReferenceType?.name)
        put("createdAt", bookmark.createdAt)
        put("updatedAt", bookmark.updatedAt)
    }

    private fun JSONObject.optIntOrNull(key: String): Int? = if (has(key) && !isNull(key)) optInt(key) else null

    private fun zoomOrDefault(value: String, default: ReaderZoomLevel): ReaderZoomLevel =
        ReaderZoomLevel.fromStoredName(value) ?: default

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String, default: T): T =
        runCatching { enumValueOf<T>(value) }.getOrDefault(default)
}
