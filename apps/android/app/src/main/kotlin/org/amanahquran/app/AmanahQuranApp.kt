package org.amanahquran.app

import androidx.compose.runtime.Composable
import org.amanahquran.app.core.navigation.AmanahQuranNavHost
import org.amanahquran.app.core.navigation.DeepLinkRequest
import org.amanahquran.app.core.theme.AmanahQuranTheme
import org.amanahquran.app.core.theme.ThemeMode

@Composable
fun AmanahQuranApp(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    elderMode: Boolean = false,
    pendingDeepLink: DeepLinkRequest? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    AmanahQuranTheme(themeMode = themeMode, elderMode = elderMode) {
        AmanahQuranNavHost(pendingDeepLink = pendingDeepLink, onDeepLinkConsumed = onDeepLinkConsumed)
    }
}

