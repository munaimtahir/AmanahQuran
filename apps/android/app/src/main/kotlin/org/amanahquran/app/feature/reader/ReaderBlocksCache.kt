package org.amanahquran.app.feature.reader

import java.util.LinkedHashMap
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ScriptType

internal object ReaderBlocksCache {
    private const val MAX_ENTRIES = 10

    private val cache = object : LinkedHashMap<String, List<ReaderStructuralItem>>(MAX_ENTRIES, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<ReaderStructuralItem>>?): Boolean {
            return size > MAX_ENTRIES
        }
    }

    @Synchronized
    fun get(openMode: ReaderOpenMode, scriptType: ScriptType): List<ReaderStructuralItem>? {
        return cache[cacheKey(openMode, scriptType)]
    }

    @Synchronized
    fun put(openMode: ReaderOpenMode, scriptType: ScriptType, blocks: List<ReaderStructuralItem>) {
        cache[cacheKey(openMode, scriptType)] = blocks.toList()
    }

    private fun cacheKey(openMode: ReaderOpenMode, scriptType: ScriptType): String {
        return when (openMode) {
            is ReaderOpenMode.Surah -> "SURAH:${openMode.surahNumber}:${scriptType.name}:v1"
            is ReaderOpenMode.Juz -> "JUZ:${openMode.juzNumber}:${scriptType.name}:v1"
            is ReaderOpenMode.Page -> "PAGE:${openMode.pageNumber}:${openMode.pageReferenceType.name}:${scriptType.name}:v1"
            is ReaderOpenMode.AyahTarget -> "AYAH:${openMode.ayahKey}:${scriptType.name}:v1"
        }
    }
}
