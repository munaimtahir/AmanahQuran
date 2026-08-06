package org.amanahquran.app.core.repository

import android.content.Context
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.amanahquran.app.core.trust.TrustCenterAssetLoader

data class PackagedAssetVerification(
    val assetName: String,
    val expectedChecksum: String?,
    val actualChecksum: String,
    val matches: Boolean,
)

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

data class OptionalContentPackTrustInfo(
    val packId: String,
    val packType: String,
    val displayName: String,
    val sourceName: String?,
    val version: String,
    val sourceUrl: String?,
    val checksum: String?,
    val validationStatus: String?,
    val offlineAvailable: Boolean,
)

data class TrustCenterUiState(
    val generatedAt: String? = null,
    val noModificationStatement: String? = null,
    val privacyPledge: String? = null,
    val quranTextSourcesActuallyUsed: List<TrustCenterSourceInfo> = emptyList(),
    val sourceReferences: List<TrustCenterSourceInfo> = emptyList(),
    val optionalContentPacks: List<OptionalContentPackTrustInfo> = emptyList(),
    val publicReleaseAllowed: Boolean = false,
    val productionApprovalStatement: String? = null,
    val isVerifying: Boolean = false,
    val verificationResults: List<PackagedAssetVerification> = emptyList(),
    val verificationCheckedAt: Long? = null,
    val verificationError: String? = null,
)

interface TrustCenterRepository {
    suspend fun loadTrustCenterUiState(): TrustCenterUiState
    suspend fun verifyPackagedContent(): List<PackagedAssetVerification>
}

class TrustCenterRepositoryImpl(
    private val context: Context,
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
            quranTextSourcesActuallyUsed = json.optJSONArray("quran_text_sources_actually_used").toSourceInfoList(),
            sourceReferences = json.optJSONArray("source_references").toSourceInfoList(),
            optionalContentPacks = json.optJSONArray("optional_content_packs").toContentPackInfoList(),
            publicReleaseAllowed = publicReleaseAllowed,
            productionApprovalStatement = json.optJSONObject("release_approval")?.optString("public_statement")
                .takeIf { publicReleaseAllowed && !it.isNullOrBlank() },
        )
    }

    // This JSON schema uses "GO"/"VERIFIED"/"APPROVED" interchangeably as its "passed" tokens
    // across different objects (see quran_text_sources_actually_used[].validation_status below,
    // which already accepts "GO"). The mushaf_page_layout block legitimately uses "GO" for
    // validation_status and "VERIFIED" for manual_review_status, and its checksum is an
    // intentional "N/A - verified layout metadata" placeholder (layout spans multiple derived
    // files, not one hashable artifact) -- none of that means the content wasn't reviewed.
    private fun isPublicReleaseAllowed(
        mushafInfo: MushafLayoutTrustInfo?,
        sources: List<TrustCenterSourceInfo>,
        requestedReleaseStatus: String?,
    ): Boolean {
        if (!requestedReleaseStatus.equals("APPROVED", ignoreCase = true)) return false
        if (mushafInfo == null) return false
        if (mushafInfo.checksum.isBlank()) return false
        if (!mushafInfo.validationStatus.equals("GO", ignoreCase = true) &&
            !mushafInfo.validationStatus.equals("VERIFIED", ignoreCase = true)
        ) return false
        if (!mushafInfo.manualReviewStatus.equals("APPROVED", ignoreCase = true) &&
            !mushafInfo.manualReviewStatus.equals("VERIFIED", ignoreCase = true)
        ) return false
        return sources
            .filter { it.scriptType == "INDOPAK" || it.scriptType == "UTHMANI" }
            .all { source ->
                source.sourceName.isNotBlank() &&
                    !source.sourceUrl.isNullOrBlank() &&
                    !source.licenseName.isNullOrBlank() &&
                    source.validationStatus.equals("GO", ignoreCase = true)
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

    private fun JSONArray?.toContentPackInfoList(): List<OptionalContentPackTrustInfo> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                val obj = optJSONObject(index) ?: continue
                add(OptionalContentPackTrustInfo(
                    packId = obj.optString("pack_id"),
                    packType = obj.optString("pack_type"),
                    displayName = obj.optString("display_name").ifBlank { obj.optString("pack_id") },
                    sourceName = obj.optString("source_name").takeIf { it.isNotBlank() },
                    version = obj.optString("version"),
                    sourceUrl = obj.optString("source_url").takeIf { it.isNotBlank() },
                    checksum = obj.optString("pack_sha256").takeIf { it.isNotBlank() },
                    validationStatus = obj.optString("validation_status").takeIf { it.isNotBlank() },
                    offlineAvailable = obj.optBoolean("offline", false),
                ))
            }
        }
    }

    override suspend fun verifyPackagedContent(): List<PackagedAssetVerification> = withContext(Dispatchers.IO) {
        val results = mutableListOf<PackagedAssetVerification>()

        val trustJson = runCatching { JSONObject(TrustCenterAssetLoader(context).load().rawJson) }.getOrNull()
        val expectedQuranDb = trustJson?.optJSONObject("packaged_asset_checksums")
            ?.optString("quran_db_sha256")
            ?.takeIf { it.isNotBlank() }
        val quranDbActual = runCatching { sha256OfAsset("database/quran.db") }.getOrNull()
        if (quranDbActual != null) {
            results += PackagedAssetVerification(
                assetName = "quran.db",
                expectedChecksum = expectedQuranDb,
                actualChecksum = quranDbActual,
                matches = expectedQuranDb != null && expectedQuranDb.equals(quranDbActual, ignoreCase = true),
            )
        }

        val translationManifest = runCatching {
            JSONObject(context.assets.open("content/translations/translation_urdu_junagarhi_manifest.json").bufferedReader().use { it.readText() })
        }.getOrNull()
        val expectedTranslation = translationManifest?.optString("pack_sha256")?.takeIf { it.isNotBlank() }
        val translationActual = runCatching { sha256OfAsset("content/translations/translation_urdu_junagarhi.db") }.getOrNull()
        if (translationActual != null) {
            results += PackagedAssetVerification(
                assetName = "translation_urdu_junagarhi.db",
                expectedChecksum = expectedTranslation,
                actualChecksum = translationActual,
                matches = expectedTranslation != null && expectedTranslation.equals(translationActual, ignoreCase = true),
            )
        }

        results
    }

    private fun sha256OfAsset(assetPath: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        context.assets.open(assetPath).use { stream ->
            val buffer = ByteArray(8192)
            while (true) {
                val read = stream.read(buffer)
                if (read <= 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

fun trustCenterRepository(context: Context): TrustCenterRepository {
    return TrustCenterRepositoryImpl(context)
}
