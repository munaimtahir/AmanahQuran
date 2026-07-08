package org.amanahquran.app.core.repository

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import org.amanahquran.app.core.database.dao.ContentSourceDao
import org.amanahquran.app.core.database.dao.ContentValidationDao
import org.amanahquran.app.core.trust.TrustCenterAssetLoader

data class TrustCenterSourceInfo(
    val referenceType: String?,
    val scriptType: String?,
    val sourceName: String,
    val rawSource: String?,
    val sourceUrl: String?,
    val licenseName: String?,
    val licenseUrl: String?,
    val notes: String?,
    val validationStatus: String?,
)

data class MushafLayoutTrustInfo(
    val pageLayoutSource: String,
    val scriptType: String,
    val pageCount: String,
    val lineMappingStatus: String,
    val importDate: String,
    val checksum: String,
    val validationStatus: String,
    val manualReviewStatus: String,
)

data class TrustCenterUiState(
    val generatedAt: String? = null,
    val noModificationStatement: String? = null,
    val privacyPledge: String? = null,
    val appContentIntegrityPlaceholders: List<String> = emptyList(),
    val claimsNotMade: List<String> = emptyList(),
    val quranTextSourcesActuallyUsed: List<TrustCenterSourceInfo> = emptyList(),
    val sourceReferences: List<TrustCenterSourceInfo> = emptyList(),
    val mushafLayoutInfo: MushafLayoutTrustInfo? = null,
    val releaseApprovalStatus: String? = null,
    val releaseApprovalBy: String? = null,
    val releaseApprovalAt: String? = null,
    val appVersionName: String? = null,
    val appVersionCode: Int? = null,
    val contentSourceCount: Int = 0,
    val validationRowCount: Int = 0,
    val failedValidationRowCount: Int = 0,
    val publicReleaseAllowed: Boolean = false,
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
        val mushafInfo = json.optJSONObject("mushaf_page_layout")?.let {
            MushafLayoutTrustInfo(
                pageLayoutSource = it.optString("page_layout_source"),
                scriptType = it.optString("script_type"),
                pageCount = it.optString("page_count"),
                lineMappingStatus = it.optString("line_mapping_status"),
                importDate = it.optString("import_date"),
                checksum = it.optString("checksum"),
                validationStatus = it.optString("validation_status"),
                manualReviewStatus = it.optString("manual_review_status")
            )
        }
        val requestedReleaseStatus = json.optJSONObject("release_approval")
            ?.optString("status")
            ?.takeIf { it.isNotBlank() }
        val publicReleaseAllowed = isPublicReleaseAllowed(
            mushafInfo = mushafInfo,
            sources = json.optJSONArray("quran_text_sources_actually_used").toSourceInfoList(),
            requestedReleaseStatus = requestedReleaseStatus,
        )
        return TrustCenterUiState(
            generatedAt = json.optString("generated_at").takeIf { it.isNotBlank() },
            noModificationStatement = json.optString("no_modification_statement").takeIf { it.isNotBlank() },
            privacyPledge = json.optString("privacy_pledge").takeIf { it.isNotBlank() },
            appContentIntegrityPlaceholders = json.optJSONArray("app_content_integrity_placeholders").toStringList(),
            claimsNotMade = json.optJSONArray("claims_not_made").toStringList(),
            quranTextSourcesActuallyUsed = json.optJSONArray("quran_text_sources_actually_used").toSourceInfoList(),
            sourceReferences = json.optJSONArray("source_references").toSourceInfoList(),
            mushafLayoutInfo = mushafInfo,
            releaseApprovalStatus = if (publicReleaseAllowed) requestedReleaseStatus else "BLOCKED — INTERNAL TEST BUILD",
            releaseApprovalBy = json.optJSONObject("release_approval")?.optString("approved_by")
                ?.takeIf { publicReleaseAllowed && it.isNotBlank() },
            releaseApprovalAt = json.optJSONObject("release_approval")?.optString("approved_at")
                ?.takeIf { publicReleaseAllowed && it.isNotBlank() },
            appVersionName = json.optJSONObject("app_version")?.optString("version_name")?.takeIf { it.isNotBlank() },
            appVersionCode = json.optJSONObject("app_version")?.optInt("version_code")?.takeIf { it > 0 },
            contentSourceCount = contentSourceDao.getContentSourceCount(),
            validationRowCount = contentValidationDao.getContentValidationCount(),
            failedValidationRowCount = contentValidationDao.getFailedValidationCount(),
            publicReleaseAllowed = publicReleaseAllowed,
        )
    }

    private fun isPublicReleaseAllowed(
        mushafInfo: MushafLayoutTrustInfo?,
        sources: List<TrustCenterSourceInfo>,
        requestedReleaseStatus: String?,
    ): Boolean {
        if (!requestedReleaseStatus.equals("APPROVED", ignoreCase = true)) return false
        if (mushafInfo == null) return false
        if (mushafInfo.checksum.isBlank() || mushafInfo.checksum.contains("N/A", ignoreCase = true)) return false
        if (!mushafInfo.validationStatus.equals("VERIFIED", ignoreCase = true)) return false
        if (!mushafInfo.manualReviewStatus.equals("APPROVED", ignoreCase = true)) return false
        return sources
            .filter { it.scriptType == "INDOPAK" || it.scriptType == "UTHMANI" }
            .all { source ->
                source.sourceName.isNotBlank() &&
                    !source.sourceUrl.isNullOrBlank() &&
                    !source.licenseName.isNullOrBlank() &&
                    source.validationStatus.equals("GO", ignoreCase = true)
            }
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
                        referenceType = obj.optString("reference_type").takeIf { it.isNotBlank() },
                        scriptType = obj.optString("script_type").takeIf { it.isNotBlank() },
                        sourceName = obj.optString("source_name").ifBlank { obj.optString("source") },
                        rawSource = obj.optString("raw_source").takeIf { it.isNotBlank() },
                        sourceUrl = obj.optString("source_url").takeIf { it.isNotBlank() },
                        licenseName = obj.optString("license_name").takeIf { it.isNotBlank() },
                        licenseUrl = obj.optString("license_url").takeIf { it.isNotBlank() },
                        notes = obj.optString("notes").takeIf { it.isNotBlank() },
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
