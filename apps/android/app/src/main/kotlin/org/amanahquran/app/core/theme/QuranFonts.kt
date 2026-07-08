package org.amanahquran.app.core.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.amanahquran.app.R
import org.amanahquran.app.core.model.ScriptType

object QuranFonts {
    val IndoPak = FontFamily(
        Font(R.font.digital_khatt_indopak, weight = FontWeight.Normal)
    )

    val Uthmani = FontFamily(
        Font(R.font.digital_khatt_v2, weight = FontWeight.Normal),
        Font(R.font.indopak_nastaleeq, weight = FontWeight.Normal)
    )

    fun getFontFamily(scriptType: ScriptType): FontFamily {
        return when (scriptType) {
            ScriptType.INDOPAK -> IndoPak
            ScriptType.UTHMANI -> Uthmani
        }
    }
}
