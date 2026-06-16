package org.amanahquran.app.core.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.amanahquran.app.core.database.dao.ContentManifestDao
import org.amanahquran.app.core.database.dao.ContentSourceDao
import org.amanahquran.app.core.database.dao.ContentValidationDao
import org.amanahquran.app.core.database.entity.ContentManifestEntity
import org.amanahquran.app.core.database.entity.ContentSourceEntity
import org.amanahquran.app.core.database.entity.ContentValidationEntity
import org.amanahquran.app.core.model.ValidationStatus

interface TrustRepository {
    fun getAllContentSources(): Flow<List<ContentSourceEntity>>
    fun getAllValidationResults(): Flow<List<ContentValidationEntity>>
    fun getLatestManifest(): Flow<ContentManifestEntity?>
    fun isContentReleaseSafe(): Flow<Boolean>
}

class TrustRepositoryImpl(
    private val contentSourceDao: ContentSourceDao,
    private val contentValidationDao: ContentValidationDao,
    private val contentManifestDao: ContentManifestDao
) : TrustRepository {
    override fun getAllContentSources(): Flow<List<ContentSourceEntity>> = contentSourceDao.getAllContentSources()

    override fun getAllValidationResults(): Flow<List<ContentValidationEntity>> =
        contentValidationDao.getAllValidationResults()

    override fun getLatestManifest(): Flow<ContentManifestEntity?> = contentManifestDao.getLatestManifest()

    override fun isContentReleaseSafe(): Flow<Boolean> = contentManifestDao.getLatestManifest().map { manifest ->
        manifest?.validationStatus == ValidationStatus.PASSED
    }
}
