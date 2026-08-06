package org.amanahquran.app.core.model

/**
 * Seven semantic Quran text sizes. The multiplier is applied to a script's own
 * [org.amanahquran.app.core.theme.QuranTypographyProfile.baseFontSize], not to a fixed sp value,
 * so IndoPak and Uthmani (whose comfortable reading sizes differ) each scale from their own
 * baseline instead of converging on identical absolute sp values.
 */
enum class ReaderZoomLevel(val multiplier: Float) {
    COMPACT(0.78f),
    SMALL(0.90f),
    STANDARD(1.00f),
    LARGE(1.16f),
    ELDER(1.34f),
    EXTRA_LARGE(1.55f),
    MAXIMUM(1.80f);

    val isMinimum: Boolean get() = this == COMPACT
    val isMaximum: Boolean get() = this == MAXIMUM

    fun increased(): ReaderZoomLevel = entries.getOrElse(ordinal + 1) { this }

    fun decreased(): ReaderZoomLevel = entries.getOrElse(ordinal - 1) { this }

    companion object {
        val default: ReaderZoomLevel = STANDARD

        /** Elder Mode's default level per the product spec: Extra Large, not just one step up. */
        val elderDefault: ReaderZoomLevel = EXTRA_LARGE

        fun fromStoredName(name: String?): ReaderZoomLevel? = entries.firstOrNull { it.name == name }

        /** Nearest level to an arbitrary multiplier, used to migrate legacy free-form sp values. */
        fun nearestTo(multiplier: Float): ReaderZoomLevel {
            return entries.minByOrNull { kotlin.math.abs(it.multiplier - multiplier) } ?: default
        }
    }
}
