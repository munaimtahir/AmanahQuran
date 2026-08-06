package org.amanahquran.app.core.model

/** Six validated hands-free auto-scroll paces, expressed as an indicative minutes-per-Juz target. */
enum class AutoScrollPace(val label: String, val approximateMinutesPerJuz: Int) {
    VERY_SLOW("Very slow", 28),
    SLOW("Slow", 20),
    COMFORTABLE("Comfortable", 15),
    MODERATELY_FAST("Moderately fast", 10),
    FAST("Fast", 7),
    VERY_FAST("Very fast", 5);

    val isSlowest: Boolean get() = this == VERY_SLOW
    val isFastest: Boolean get() = this == VERY_FAST

    fun faster(): AutoScrollPace = entries.getOrElse(ordinal + 1) { this }

    fun slower(): AutoScrollPace = entries.getOrElse(ordinal - 1) { this }

    companion object {
        val default: AutoScrollPace = COMFORTABLE

        fun fromStoredName(name: String?): AutoScrollPace? = entries.firstOrNull { it.name == name }
    }
}

/**
 * Auto-scroll is a playback-style state machine, not a boolean. [COMPLETED] is a terminal state
 * reached only by running off the end of the current reading range; every other exit (manual
 * interaction, zoom, script switch, lifecycle interruption, explicit close) goes through
 * [PAUSED] or back to [INACTIVE], and nothing may transition out of [PAUSED] except an explicit
 * user Resume -- auto-scroll must never silently resume on its own.
 */
enum class AutoScrollState {
    INACTIVE,
    STARTING,
    RUNNING,
    PAUSED,
    COMPLETED,
}
