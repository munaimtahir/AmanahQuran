package org.amanahquran.app.core.repository

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import org.amanahquran.app.core.database.dao.ContentSourceDao
import org.amanahquran.app.core.database.dao.ContentValidationDao
import org.amanahquran.app.core.trust.TrustCenterAssetLoader

data class TrustCenterSourceInfo(
    val scriptType: String?,
    val sourceName: String,
    val rawSource: String?,
    val validationStatus: String?,
)

data class TrustCenterUiState(
    val generatedAt: String? = null,
    val noModificationStatement: String? = null,
    val privacyPledge: String? = null,
    val appContentIntegrityPlaceholders: List<String> = emptyList(),
    val claimsNotMade: List<String> = emptyList(),
    val quranTextSourcesActuallyUsed: List<TrustCenterSourceInfo> = emptyList(),
    val contentSourceCount: Int = 0,
    val validationRowCount: Int = 0,
    val failedValidationRowCount: Int = 0,
)

interface TrustCenterRepository {
    suspend fun loadTrustCenterUiState(): TrustCenterUiState
}

class TrustCenterRepositoryImpl(
    private val context: Context,
    private val contentSourceDao: ContentSourceDao,
    private val contentValidationDao: ContentValidationDao,
) : TrustCenterRepository {
    override suspend fun loadTrustCenterUiState(): TrustCenterUiState {
        val rawJson = TrustCenterAssetLoader(context).load().rawJson
        val json = JSONObject(rawJson)
        return TrustCenterUiState(
            generatedAt = json.optString("generated_at").takeIf { it.isNotBlank() },
            noModificationStatement = json.optString("no_modification_statement").takeIf { it.isNotBlank() },
            privacyPledge = json.optString("privacy_pledge").takeIf { it.isNotBlank() },
            appContentIntegrityPlaceholders = json.optJSONArray("app_content_integrity_placeholders").toStringList(),
            claimsNotMade = json.optJSONArray("claims_not_made").toStringList(),
            quranTextSourcesActuallyUsed = json.optJSONArray("quran_text_sources_actually_used").toSourceInfoList(),
            contentSourceCount = contentSourceDao.getContentSourceCount(),
            validationRowCount = contentValidationDao.getContentValidationCount(),
            failedValidationRowCount = contentValidationDao.getFailedValidationCount(),
        )
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                optString(index).takeIf { it.isNotBlank() }?.let(::add)
            }
        }
    }

    private fun JSONArray?.toSourceInfoList(): List<TrustCenterSourceInfo> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                val obj = optJSONObject(index) ?: continue
                add(
                    TrustCenterSourceInfo(
                        scriptType = obj.optString("script_type").takeIf { it.isNotBlank() },
                        sourceName = obj.optString("source"),
                        rawSource = obj.optString("raw_source").takeIf { it.isNotBlank() },
                        validationStatus = obj.optString("validation_status").takeIf { it.isNotBlank() },
                    ),
                )
            }
        }
    }
}

fun trustCenterRepository(
    context: Context,
    contentSourceDao: ContentSourceDao,
    contentValidationDao: ContentValidationDao,
): TrustCenterRepository {
    return TrustCenterRepositoryImpl(context, contentSourceDao, contentValidationDao)
}
