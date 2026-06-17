package org.amanahquran.app.core.trust

import android.content.Context
import org.amanahquran.app.core.repository.TrustCenterContent

class TrustCenterAssetLoader(
    private val context: Context,
) {
    fun load(): TrustCenterContent {
        val rawJson = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
        return TrustCenterContent(
            rawJson = rawJson,
            noModificationStatementPreview = extractStringValue(rawJson, "no_modification_statement"),
        )
    }

    private fun extractStringValue(json: String, key: String): String? {
        val regex = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"")
        return regex.find(json)?.groupValues?.getOrNull(1)
    }

    companion object {
        const val ASSET_PATH = "trust/trust_center_content.json"
    }
}
