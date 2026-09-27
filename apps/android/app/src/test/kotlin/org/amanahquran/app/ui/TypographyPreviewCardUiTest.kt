package org.amanahquran.app.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.TranslationSelection
import org.amanahquran.app.core.theme.AmanahQuranTheme
import org.amanahquran.app.core.theme.ThemeMode
import org.amanahquran.app.feature.reader.quranContentRepository
import org.amanahquran.app.feature.settings.TYPOGRAPHY_PREVIEW_AYAH_KEY
import org.amanahquran.app.feature.settings.TypographyPreviewCard
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** The Settings preview renders the verified display text from the bundled database, unmodified. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TypographyPreviewCardUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun previewShowsVerifiedDisplayTextForSelectedScript() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expected = runBlocking {
            quranContentRepository(context).getAyahDisplay(TYPOGRAPHY_PREVIEW_AYAH_KEY, ScriptType.UTHMANI.name)?.displayText
        }
        assertNotNull(expected)

        composeRule.setContent {
            AmanahQuranTheme(themeMode = ThemeMode.BLACK, elderMode = true) {
                TypographyPreviewCard(
                    scriptType = ScriptType.UTHMANI,
                    arabicFontSizeSp = 30f,
                    arabicLineSpacingMultiplier = 2f,
                    translationSelection = TranslationSelection.OFF,
                    translationFontSizeSp = 18f,
                )
            }
        }
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTextCount(expected!!) > 0
        }
        composeRule.onNodeWithText(expected!!).assertIsDisplayed()
    }

    private fun androidx.compose.ui.test.junit4.ComposeContentTestRule.onAllNodesWithTextCount(text: String): Int =
        onAllNodes(androidx.compose.ui.test.hasText(text)).fetchSemanticsNodes().size
}
