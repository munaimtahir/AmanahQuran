package org.amanahquran.app.core.model

/**
 * The two supported presentation modes for reading Quran text.
 * - [CONTINUOUS]: Mode A — Flowing, book-like Quran reading experience with inline ayah markers.
 * - [AYAH]: Mode B — Verse-oriented reading with distinct individual ayah cards.
 *
 * Standalone Page View and Scroll View modes are removed; legacy stored preferences
 * representing them automatically migrate to [CONTINUOUS].
 */
enum class ReaderContentMode(val label: String) {
    CONTINUOUS("Continuous View"),
    AYAH("Ayah View");

    companion object {
        val default: ReaderContentMode = CONTINUOUS

        fun fromStoredName(name: String?): ReaderContentMode {
            if (name.isNullOrBlank()) return default
            return when (name.uppercase().trim()) {
                "AYAH", "AYAH_VIEW", "AYAH_BY_AYAH" -> AYAH
                "CONTINUOUS", "CONTINUOUS_VIEW", "CONTINUOUS_READING", "PAGE", "SCROLL", "BOOK", "BOOK_MODE" -> CONTINUOUS
                else -> entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: default
            }
        }
    }
}
