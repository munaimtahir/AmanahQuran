package org.amanahquran.app.core.repository

import org.amanahquran.app.core.database.dao.ContentSourceDao
import org.amanahquran.app.core.database.dao.ContentValidationDao
import org.amanahquran.app.core.database.entity.ContentSourceEntity

data class ContentValidationSummary(
    val totalRows: Int,
    val failedRows: Int,
)

data class TrustCenterContent(
    val rawJson: String,
    val noModificationStatementPreview: String?,
)

interface TrustContentRepository {
    suspend fun getContentSources(): List<ContentSourceEntity>
    suspend fun getContentValidationSummary(): ContentValidationSummary
}

class TrustContentRepositoryImpl(
    private val contentSourceDao: ContentSourceDao,
    private val contentValidationDao: ContentValidationDao,
) : TrustContentRepository {
    override suspend fun getContentSources(): List<ContentSourceEntity> = contentSourceDao.getContentSources()

    override suspend fun getContentValidationSummary(): ContentValidationSummary {
        return ContentValidationSummary(
            totalRows = contentValidationDao.getContentValidationCount(),
            failedRows = contentValidationDao.getFailedValidationCount(),
        )
    }
}
