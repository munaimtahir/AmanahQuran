package org.amanahquran.app.core.model

object ValidationHelpers {
    /**
     * Checks if the ayah key is in the format "surahNumber:ayahNumber" (e.g., "2:255").
     */
    fun isValidAyahKey(ayahKey: String): Boolean {
        if (ayahKey.isBlank()) return false
        val parts = ayahKey.split(":")
        if (parts.size != 2) return false
        
        return parts.all { part ->
            part.toIntOrNull()?.let { it > 0 } ?: false
        }
    }

    /**
     * Checks if the checksum is not blank.
     */
    fun isValidChecksum(checksum: String?): Boolean {
        return !checksum.isNullOrBlank()
    }

    /**
     * Parses a string into a ScriptType, if valid.
     */
    fun parseScriptType(value: String?): ScriptType? {
        return try {
            value?.let { ScriptType.valueOf(it.uppercase()) }
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    /**
     * Validates bookmark target based on its type.
     */
    fun isValidBookmarkTarget(
        type: BookmarkType,
        ayahKey: String?,
        pageNumber: Int?
    ): Boolean {
        return when (type) {
            BookmarkType.AYAH -> !ayahKey.isNullOrBlank() && isValidAyahKey(ayahKey)
            BookmarkType.PAGE -> pageNumber != null && pageNumber > 0
        }
    }
}
