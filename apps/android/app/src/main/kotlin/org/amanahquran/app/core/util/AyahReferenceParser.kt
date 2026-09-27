package org.amanahquran.app.core.util

/** A canonical `surah:ayah` reference typed by the user; not yet checked against ayah counts. */
data class AyahReference(val surahNumber: Int, val ayahNumber: Int) {
    val ayahKey: String get() = "$surahNumber:$ayahNumber"
}

/**
 * Parses reader "jump" input. Accepts `2:255` (also `2.255`, `2 255`, `2/255`, `2-255`) or a bare
 * ayah number, which resolves against [defaultSurah]. Only structural bounds are checked here
 * (surah 1..114, ayah >= 1); the per-surah ayah count is checked against the content database.
 */
object AyahReferenceParser {
    private val fullReference = Regex("""^(\d{1,3})\s*[:./\-\s]\s*(\d{1,3})$""")
    private val bareAyah = Regex("""^\d{1,3}$""")

    fun parse(input: String, defaultSurah: Int?): AyahReference? {
        val trimmed = input.trim()
        fullReference.matchEntire(trimmed)?.let { match ->
            val surah = match.groupValues[1].toInt()
            val ayah = match.groupValues[2].toInt()
            return AyahReference(surah, ayah).takeIf { surah in 1..114 && ayah >= 1 }
        }
        if (defaultSurah != null && bareAyah.matches(trimmed)) {
            val ayah = trimmed.toInt()
            return AyahReference(defaultSurah, ayah).takeIf { defaultSurah in 1..114 && ayah >= 1 }
        }
        return null
    }
}
