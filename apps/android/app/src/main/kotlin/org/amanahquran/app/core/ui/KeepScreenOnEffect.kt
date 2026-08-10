package org.amanahquran.app.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/** Applies FLAG_KEEP_SCREEN_ON to the current window only while [enabled] and this reader
 *  composable is in the foreground -- cleared automatically when either becomes false. */
@Composable
fun KeepScreenOnEffect(enabled: Boolean) {
    val view = LocalView.current
    DisposableEffect(enabled, view) {
        if (enabled) view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}
