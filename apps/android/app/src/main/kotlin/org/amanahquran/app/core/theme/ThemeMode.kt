package org.amanahquran.app.core.theme

enum class ThemeMode(val storageKey: String, val displayName: String) {
    SYSTEM("system", "System"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark"),
    SEPIA("sepia", "Sepia"),

    /** Optional pure-black variant of Dark for OLED screens (no lit background pixels). */
    BLACK("black", "Black (OLED)"),

    /** High-contrast mode for visually impaired users with maximum legibility. */
    HIGH_CONTRAST("high_contrast", "High Contrast");

    val isDark: Boolean? get() = when (this) {
        SYSTEM -> null
        LIGHT, SEPIA -> false
        DARK, BLACK, HIGH_CONTRAST -> true
    }
    
    val isHighContrast: Boolean get() = this == HIGH_CONTRAST
}

