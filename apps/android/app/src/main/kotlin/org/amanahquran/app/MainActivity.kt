package org.amanahquran.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.amanahquran.app.core.repository.readerSettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val settingsRepository = remember(context) { readerSettingsRepository(context) }
            val settings by settingsRepository.settings.collectAsState(
                initial = org.amanahquran.app.core.repository.ReaderSettings(),
            )
            AmanahQuranApp(
                themeMode = settings.selectedTheme,
                elderMode = settings.elderModeEnabled,
            )
        }
    }
}
