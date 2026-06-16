package org.amanahquran.app.core.repository

import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.dao.LastReadDao
import org.amanahquran.app.core.database.entity.LastReadEntity
import org.amanahquran.app.core.model.ScriptType

interface LastReadRepository {
    fun getLastRead(): Flow<LastReadEntity?>
    suspend fun saveLastRead(
        ayahKey: String,
        surahNumber: Int,
        ayahNumber: Int,
        juzNumber: Int,
        pageNumber: Int,
        scriptType: ScriptType,
        scrollOffset: Int? = null
    )
}

class LastReadRepositoryImpl(
    private val lastReadDao: LastReadDao
) : LastReadRepository {
    override fun getLastRead(): Flow<LastReadEntity?> = lastReadDao.getLastRead()

    override suspend fun saveLastRead(
        ayahKey: String,
        surahNumber: Int,
        ayahNumber: Int,
        juzNumber: Int,
        pageNumber: Int,
        scriptType: ScriptType,
        scrollOffset: Int?
    ) {
        lastReadDao.upsertLastRead(
            LastReadEntity(
                id = 1,
                ayahKey = ayahKey,
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                juzNumber = juzNumber,
                pageNumber = pageNumber,
                scriptType = scriptType,
                scrollOffset = scrollOffset,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
