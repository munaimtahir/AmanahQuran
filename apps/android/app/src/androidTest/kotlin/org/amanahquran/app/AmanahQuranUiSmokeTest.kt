package org.amanahquran.app

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
@Suppress("DEPRECATION")
class AmanahQuranUiSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun waitForHome() {
        composeRule.setContent { AmanahQuranApp() }
        composeRule.waitUntilAtLeastOneExists(hasText("Amanah Quran"), timeoutMillis = 30_000)
        composeRule.enableAccessibilityChecks()
    }

    @Test
    fun home_hasAccessibleSemantics() {
        composeRule.onNodeWithText("Amanah Quran").assertIsDisplayed()
        composeRule.onRoot().tryPerformAccessibilityChecks()
    }

    @Test
    fun sacredReaderDestinations_openAndPassAccessibilityChecks() {
        openTextDestination("Surah Index", "Surahs")
        openTextDestination("Juz Index", "Juz")
        openTextDestination("Page Index", "Pages")
        openTextDestination("Search", "Search")
        openTextDestination("Bookmarks", "Bookmarks")

        openDescriptionDestination("Settings", "Settings")
        openDescriptionDestination("Trust Center", "Trust Center")
    }

    @Test
    fun reader_opensOfflineAndPassesAccessibilityChecks() {
        composeRule.onNode(hasText("Open Mushaf Page") or hasText("Continue Reading"))
            .performScrollTo()
            .performClick()
        composeRule.waitUntilAtLeastOneExists(
            matcher = hasContentDescription("Go back"),
            timeoutMillis = 30_000,
        )
        composeRule.onNodeWithContentDescription("Go back").assertIsDisplayed()
        composeRule.onRoot().tryPerformAccessibilityChecks()
    }

    private fun openTextDestination(action: String, title: String) {
        composeRule.onNodeWithText(action)
            .performScrollTo()
            .performClick()
        composeRule.waitUntilAtLeastOneExists(hasText(title), timeoutMillis = 30_000)
        composeRule.onNodeWithText(title).assertIsDisplayed()
        composeRule.onRoot().tryPerformAccessibilityChecks()
        returnHome()
    }

    private fun openDescriptionDestination(action: String, title: String) {
        composeRule.onNodeWithContentDescription(action).performClick()
        composeRule.waitUntilAtLeastOneExists(hasText(title), timeoutMillis = 30_000)
        composeRule.onNodeWithText(title).assertIsDisplayed()
        composeRule.onRoot().tryPerformAccessibilityChecks()
        returnHome()
    }

    private fun returnHome() {
        composeRule.onNodeWithContentDescription("Go back").performClick()
        composeRule.waitUntilAtLeastOneExists(hasText("Amanah Quran"), timeoutMillis = 30_000)
    }
}
