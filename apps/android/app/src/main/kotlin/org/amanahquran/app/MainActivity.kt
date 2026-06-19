package org.amanahquran.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import org.amanahquran.app.core.repository.readerSettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val settingsRepository = remember(context) { readerSettingsRepository(context) }
            val settings by settingsRepository.settings.collectAsStateWithLifecycle(
                initialValue = org.amanahquran.app.core.repository.ReaderSettings(),
            )
            AmanahQuranApp(
                themeMode = settings.selectedTheme,
                elderMode = settings.elderModeEnabled,
            )
        }
    }
}
