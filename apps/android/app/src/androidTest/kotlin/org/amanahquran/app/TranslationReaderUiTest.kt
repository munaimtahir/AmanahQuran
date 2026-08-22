package org.amanahquran.app

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Device-level coverage for the Manifest English / Irfan-ul-Quran Urdu translation integration:
 * Settings selection persists and renders correctly in the reader for both translations,
 * including the neutral SOURCE_MISSING placeholder at 1:1 (never fabricated text) and correct
 * verbatim translation text at 1:2 in each language.
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class TranslationReaderUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun waitForHome() {
        composeRule.setContent { AmanahQuranApp() }
        composeRule.waitUntilAtLeastOneExists(hasText("Amanah Quran"), timeoutMillis = 30_000)
    }

    @Test
    fun selectingEnglishTranslationShowsManifestTextAndSourceMissingPlaceholder() {
        selectTranslation("English")
        openAlFatihah()

        composeRule.waitUntilAtLeastOneExists(hasText("Translation not provided in this source"), timeoutMillis = 30_000)
        composeRule.onNodeWithText("Translation not provided in this source").assertIsDisplayed()
        composeRule.waitUntilAtLeastOneExists(hasText("All praise be to Allah", substring = true), timeoutMillis = 10_000)
        composeRule.onNode(hasText("All praise be to Allah", substring = true)).assertIsDisplayed()
    }

    @Test
    fun selectingUrduTranslationShowsIrfanTextAndSourceMissingPlaceholder() {
        selectTranslation("Urdu")
        openAlFatihah()

        composeRule.waitUntilAtLeastOneExists(hasText("Translation not provided in this source"), timeoutMillis = 30_000)
        composeRule.onNodeWithText("Translation not provided in this source").assertIsDisplayed()
        composeRule.waitUntilAtLeastOneExists(hasText("سب تعریفیں اللہ ہی کے لئے ہیں", substring = true), timeoutMillis = 10_000)
        composeRule.onNode(hasText("سب تعریفیں اللہ ہی کے لئے ہیں", substring = true)).assertIsDisplayed()
    }

    @Test
    fun translationOffShowsNeitherManifestNorIrfanText() {
        selectTranslation("Off")
        openAlFatihah()

        // Reader is loaded once its back button exists; give the composition a moment to settle.
        composeRule.waitUntilAtLeastOneExists(hasContentDescription("Go back"), timeoutMillis = 30_000)
        composeRule.onAllNodesWithText("All praise be to Allah", substring = true).assertCountEquals(0)
        composeRule.onAllNodesWithText("سب تعریفیں اللہ ہی کے لئے ہیں", substring = true).assertCountEquals(0)
        composeRule.onAllNodesWithText("Translation not provided in this source").assertCountEquals(0)
    }

    private fun selectTranslation(label: String) {
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.waitUntilAtLeastOneExists(hasText("Settings"), timeoutMillis = 30_000)
        composeRule.onNodeWithText(label).performScrollTo().performClick()
        composeRule.onNodeWithContentDescription("Go back").performClick()
        composeRule.waitUntilAtLeastOneExists(hasText("Amanah Quran"), timeoutMillis = 30_000)
    }

    private fun openAlFatihah() {
        composeRule.onNodeWithText("Surah Index").performScrollTo().performClick()
        composeRule.waitUntilAtLeastOneExists(hasText("Al-Fatihah"), timeoutMillis = 30_000)
        composeRule.onNodeWithText("Al-Fatihah").performClick()
        composeRule.waitUntilAtLeastOneExists(hasContentDescription("Go back"), timeoutMillis = 30_000)
    }
}
