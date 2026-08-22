package org.amanahquran.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.amanahquran.app.core.navigation.DeepLinkRequest
import org.amanahquran.app.core.repository.readerSettingsRepository

class MainActivity : ComponentActivity() {
    private val pendingDeepLink = mutableStateOf<DeepLinkRequest?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            val context = LocalContext.current
            val settingsRepository = remember(context) { readerSettingsRepository(context) }
            val settings by settingsRepository.settings.collectAsState(
                initial = org.amanahquran.app.core.repository.ReaderSettings(),
            )
            AmanahQuranApp(
                themeMode = settings.selectedTheme,
                elderMode = settings.elderModeEnabled,
                pendingDeepLink = pendingDeepLink.value,
                onDeepLinkConsumed = { pendingDeepLink.value = null },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_OPEN_CONTINUE_READING, false) == true) {
            pendingDeepLink.value = DeepLinkRequest.ContinueReading
        }
        intent?.getStringExtra(EXTRA_OPEN_AYAH_KEY)?.takeIf { it.matches(Regex("\\d{1,3}:\\d{1,3}")) }?.let {
            pendingDeepLink.value = DeepLinkRequest.ExactAyah(it)
        }
    }

    companion object {
        const val EXTRA_OPEN_CONTINUE_READING = "open_continue_reading"
        const val EXTRA_OPEN_AYAH_KEY = "open_ayah_key"
    }
}
