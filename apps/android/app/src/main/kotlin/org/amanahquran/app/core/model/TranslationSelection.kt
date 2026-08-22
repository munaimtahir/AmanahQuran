package org.amanahquran.app.core.model

/** Text direction a translation should render with. Arabic script direction is unrelated -- see [ScriptType]. */
enum class TranslationDirection { LTR, RTL }

/**
 * The user-facing translation choice. [translationId] is the stable, immutable identity used
 * everywhere translation content is keyed (database rows, Trust Center, footnotes) -- never the
 * enum name or a display string. [OFF] carries no translation identity or direction.
 */
enum class TranslationSelection(
    val translationId: String?,
    val direction: TranslationDirection?,
) {
    OFF(translationId = null, direction = null),
    MANIFEST_EN(translationId = "TAHIR_QADRI_MANIFEST_EN", direction = TranslationDirection.LTR),
    IRFAN_UR(translationId = "TAHIR_QADRI_IRFAN_UR", direction = TranslationDirection.RTL),
    ;

    companion object {
        val default = OFF

        fun fromStoredName(name: String?): TranslationSelection? = entries.firstOrNull { it.name == name }
    }
}
