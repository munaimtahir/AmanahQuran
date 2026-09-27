package org.amanahquran.app.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.TranslationSelection
import org.amanahquran.app.core.theme.AmanahQuranTheme
import org.amanahquran.app.core.theme.ThemeMode
import org.amanahquran.app.feature.reader.ReaderSettingsBottomSheet
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Reader settings sheet at 200% system font scale and in Elder Mode (UI refinement R1/R4):
 * every control must stay reachable (the sheet scrolls, chip rows wrap) and changes propagate.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h640dp-xxhdpi")
class ReaderSettingsSheetUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun setSheet(
        elderMode: Boolean,
        onTheme: (ThemeMode) -> Unit = {},
        onScript: (ScriptType) -> Unit = {},
        onLinked: (Boolean) -> Unit = {},
    ) {
        composeRule.setContent {
            var theme by remember { mutableStateOf(ThemeMode.LIGHT) }
            var linked by remember { mutableStateOf(true) }
            AmanahQuranTheme(themeMode = theme, elderMode = elderMode) {
                ReaderSettingsBottomSheet(
                    onDismiss = {},
                    zoomLevel = ReaderZoomLevel.STANDARD,
                    translationZoomLevel = ReaderZoomLevel.STANDARD,
                    linkedZoomEnabled = linked,
                    onSetLinkedZoomEnabled = { linked = it; onLinked(it) },
                    onApplyZoom = { _, _ -> },
                    contentMode = ReaderContentMode.CONTINUOUS,
                    onSetContentMode = {},
                    hasTranslation = true,
                    selectedScript = ScriptType.INDOPAK,
                    onSelectScript = onScript,
                    translationSelection = TranslationSelection.IRFAN_UR,
                    onSelectTranslation = {},
                    selectedTheme = theme,
                    onSelectTheme = { theme = it; onTheme(it) },
                )
            }
        }
    }

    @Test
    fun atDoubleFontScale_everyControlIsReachable_andBlackThemeSelectable() {
        RuntimeEnvironment.setFontScale(2f)
        var selectedTheme: ThemeMode? = null
        setSheet(elderMode = false, onTheme = { selectedTheme = it })

        listOf("IndoPak", "Uthmani", "Off", "English", "Urdu", "Link Arabic and translation size", "Reset text size", "Continuous", "Ayah by Ayah")
            .forEach { composeRule.onNodeWithText(it).performScrollTo().assertIsDisplayed() }
        composeRule.onNodeWithText("Black (OLED)").performScrollTo().assertIsDisplayed()
        // Robolectric can't inject touches into the sheet's popup window; use the a11y click action
        // (what TalkBack triggers), which exercises the same onClick wiring.
        composeRule.onNodeWithText("Black (OLED)").performSemanticsAction(SemanticsActions.OnClick)
        composeRule.waitForIdle()
        assertEquals(ThemeMode.BLACK, selectedTheme)
    }

    @Test
    fun inElderMode_linkToggleRevealsSeparateTranslationSlider() {
        var linked: Boolean? = null
        setSheet(elderMode = true, onLinked = { linked = it })

        composeRule.onNodeWithText("Text Size").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Link Arabic and translation size").performScrollTo().performSemanticsAction(SemanticsActions.OnClick)
        composeRule.waitForIdle()
        assertEquals(false, linked)
        composeRule.onNodeWithText("Arabic Text Size").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Translation Size").performScrollTo().assertIsDisplayed()
    }
}
