package org.amanahquran.app.content.validation

import org.amanahquran.app.core.database.AmanahContentDatabase
import org.amanahquran.app.core.trust.TrustCenterAssetLoader

data class ContentValidationSnapshot(
    val dbLoaded: Boolean,
    val surahCount: Int,
    val ayahCount: Int,
    val uthmaniRows: Int,
    val indopakRows: Int,
    val searchIndexRows: Int,
    val contentSourceRows: Int,
    val validationRows: Int,
    val failedValidationRows: Int,
    val emptyUthmaniDisplayRows: Int,
    val emptyIndopakDisplayRows: Int,
    val fontInventoryRows: Int,
    val sampleUthmani11: String?,
    val sampleIndopak11: String?,
    val sampleUthmani2255: String?,
    val sampleIndopak2255: String?,
    val trustJsonLoaded: Boolean,
    val noModificationStatementPreview: String?,
) {
    val countsPassed: Boolean =
        surahCount == 114 &&
            ayahCount == 6236 &&
            uthmaniRows == 6236 &&
            indopakRows == 6236 &&
            searchIndexRows == 6236

    val samplesLoaded: Boolean =
        !sampleUthmani11.isNullOrBlank() &&
            !sampleIndopak11.isNullOrBlank() &&
            !sampleUthmani2255.isNullOrBlank() &&
            !sampleIndopak2255.isNullOrBlank()

    val passed: Boolean =
        dbLoaded &&
            countsPassed &&
            samplesLoaded &&
            emptyUthmaniDisplayRows == 0 &&
            emptyIndopakDisplayRows == 0 &&
            failedValidationRows == 0 &&
            trustJsonLoaded
}

class ContentValidationService(
    private val database: AmanahContentDatabase,
    private val trustCenterAssetLoader: TrustCenterAssetLoader,
) {
    suspend fun validatePackagedContent(): ContentValidationSnapshot {
        val quranTextDao = database.quranTextDao()
        val trustContent = runCatching { trustCenterAssetLoader.load() }.getOrNull()
        return ContentValidationSnapshot(
            dbLoaded = true,
            surahCount = database.surahDao().getSurahCount(),
            ayahCount = database.ayahDao().getAyahCount(),
            uthmaniRows = quranTextDao.getTextCountByScript(SCRIPT_UTHMANI),
            indopakRows = quranTextDao.getTextCountByScript(SCRIPT_INDOPAK),
            searchIndexRows = database.searchIndexDao().getSearchIndexCount(),
            contentSourceRows = database.contentSourceDao().getContentSourceCount(),
            validationRows = database.contentValidationDao().getContentValidationCount(),
            failedValidationRows = database.contentValidationDao().getFailedValidationCount(),
            emptyUthmaniDisplayRows = quranTextDao.getBlankDisplayTextCount(SCRIPT_UTHMANI),
            emptyIndopakDisplayRows = quranTextDao.getBlankDisplayTextCount(SCRIPT_INDOPAK),
            fontInventoryRows = database.fontInventoryDao().getFontInventoryCount(),
            sampleUthmani11 = quranTextDao.getTextByAyahAndScript("1:1", SCRIPT_UTHMANI)?.displayText,
            sampleIndopak11 = quranTextDao.getTextByAyahAndScript("1:1", SCRIPT_INDOPAK)?.displayText,
            sampleUthmani2255 = quranTextDao.getTextByAyahAndScript("2:255", SCRIPT_UTHMANI)?.displayText,
            sampleIndopak2255 = quranTextDao.getTextByAyahAndScript("2:255", SCRIPT_INDOPAK)?.displayText,
            trustJsonLoaded = trustContent != null,
            noModificationStatementPreview = trustContent?.noModificationStatementPreview,
        )
    }

    companion object {
        const val SCRIPT_UTHMANI = "UTHMANI"
        const val SCRIPT_INDOPAK = "INDOPAK"
    }
}
