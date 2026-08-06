package org.amanahquran.app.core.backup

import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRecord
import org.amanahquran.app.core.repository.ReaderSettings
import org.amanahquran.app.core.repository.LastReadState
import org.amanahquran.app.core.model.ValidationHelpers
import org.amanahquran.app.core.theme.ThemeMode
import org.json.JSONArray
import org.json.JSONObject

data class UserBackupPayload(
    val bookmarks: List<BookmarkRecord>,
    val collectionsJson: String,
    val settings: ReaderSettings,
    val lastRead: LastReadState? = null,
)

object UserBackupCodec {
    const val CURRENT_VERSION = 1

    fun encode(payload: UserBackupPayload): String = JSONObject().apply {
        put("format", "amanah-quran-user-backup")
        put("version", CURRENT_VERSION)
        put("bookmarks", JSONArray().apply { payload.bookmarks.forEach { put(bookmarkToJson(it)) } })
        put("collections", JSONArray(payload.collectionsJson))
        put("settings", JSONObject().apply {
            put("script", payload.settings.selectedScript.name)
            put("theme", payload.settings.selectedTheme.name)
            put("arabicFontSizeSp", payload.settings.arabicFontSizeSp)
            put("elderMode", payload.settings.elderModeEnabled)
            put("bookMode", payload.settings.bookModeEnabled)
            put("translationEnabled", payload.settings.translationEnabled)
            put("translationFontSizeSp", payload.settings.translationFontSizeSp)
            put("arabicLineSpacingMultiplier", payload.settings.arabicLineSpacingMultiplier)
            put("readerHorizontalPaddingDp", payload.settings.readerHorizontalPaddingDp)
        })
        payload.lastRead?.let { lastRead -> put("lastRead", JSONObject().apply {
            put("ayahKey", lastRead.ayahKey)
            put("surahNumber", lastRead.surahNumber)
            put("ayahNumber", lastRead.ayahNumber)
            put("pageNumber", lastRead.pageNumber)
            put("juzNumber", lastRead.juzNumber)
            put("scriptType", lastRead.scriptType.name)
            put("updatedAt", lastRead.updatedAt)
        }) }
    }.toString(2)

    fun validateAndParse(json: String): UserBackupPayload {
        val root = JSONObject(json)
        require(root.optString("format") == "amanah-quran-user-backup") { "Unsupported backup format" }
        require(root.optInt("version") == CURRENT_VERSION) { "Unsupported backup version" }
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
        val settings = ReaderSettings(
            selectedScript = enumOrDefault(settingsJson.optString("script"), ScriptType.INDOPAK),
            selectedTheme = enumOrDefault(settingsJson.optString("theme"), ThemeMode.SYSTEM),
            arabicFontSizeSp = settingsJson.optDouble("arabicFontSizeSp", 24.0).toFloat().coerceIn(16f, 42f),
            elderModeEnabled = settingsJson.optBoolean("elderMode", false),
            bookModeEnabled = settingsJson.optBoolean("bookMode", false),
            translationEnabled = settingsJson.optBoolean("translationEnabled", false),
            translationFontSizeSp = settingsJson.optDouble("translationFontSizeSp", 18.0).toFloat().coerceIn(14f, 32f),
            arabicLineSpacingMultiplier = settingsJson.optDouble("arabicLineSpacingMultiplier", 1.88).toFloat().coerceIn(1.5f, 2.4f),
            readerHorizontalPaddingDp = settingsJson.optDouble("readerHorizontalPaddingDp", 16.0).toFloat().coerceIn(8f, 32f),
        )
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
        return UserBackupPayload(bookmarks, root.getJSONArray("collections").toString(), settings, lastRead)
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

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String, default: T): T =
        runCatching { enumValueOf<T>(value) }.getOrDefault(default)
}
