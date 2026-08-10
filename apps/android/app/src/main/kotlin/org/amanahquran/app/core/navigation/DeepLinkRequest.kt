package org.amanahquran.app.core.navigation

sealed interface DeepLinkRequest {
    data object ContinueReading : DeepLinkRequest
}
