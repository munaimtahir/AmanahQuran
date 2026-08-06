package org.amanahquran.app.core.model

/**
 * How the Quran text is laid out in the reader. [AYAH] is the existing one-ayah-per-row
 * experience; [CONTINUOUS] flows consecutive ayahs together like a printed Mushaf, with inline
 * ayah markers instead of a card per ayah. Both modes read the same underlying ayah data --
 * this only changes presentation.
 */
enum class ReaderContentMode {
    AYAH,
    CONTINUOUS;

    companion object {
        val default: ReaderContentMode = AYAH

        fun fromStoredName(name: String?): ReaderContentMode? = entries.firstOrNull { it.name == name }
    }
}
