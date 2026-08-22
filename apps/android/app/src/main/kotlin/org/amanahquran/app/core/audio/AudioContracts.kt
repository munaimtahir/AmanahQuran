package org.amanahquran.app.core.audio

/** Source-neutral boundary for authentic recitation. No arbitrary or generated audio is allowed. */
data class Reciter(val id: String, val name: String, val sourceUrl: String, val license: String)
data class AudioSource(val id: String, val reciter: Reciter, val baseUri: String, val approved: Boolean)
data class AudioAyah(val ayahKey: String, val source: AudioSource, val uri: String)

interface AudioRepository {
    suspend fun audioFor(ayahKey: String): AudioAyah?
}

/** Until a source is approved and packaged/cached, the audio action remains unavailable. */
object NoApprovedAudioRepository : AudioRepository {
    override suspend fun audioFor(ayahKey: String): AudioAyah? = null
}

interface PlaybackController {
    fun play(audio: AudioAyah)
    fun pause()
    fun replay(audio: AudioAyah)
    fun release()
}
