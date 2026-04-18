package com.noadsjustapps.speedometer

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noadsjustapps.speedometer.ui.theme.SpeedometerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun buildViewModel(): SpeedometerViewModel {
        val application = ApplicationProvider.getApplicationContext<Application>()
        return SpeedometerViewModel(application)
    }

    // --- Title and navigation ---

    @Test
    fun settingsTitleIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun backButtonIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun backButtonTriggersNavigationCallback() {
        var navigatedBack = false
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(
                    onNavigateBack = { navigatedBack = true },
                    viewModel = buildViewModel()
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(navigatedBack)
    }

    // --- Speed unit section ---

    @Test
    fun speedUnitSectionTitleIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("Speed Unit").assertIsDisplayed()
    }

    @Test
    fun metricButtonIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("METRIC").assertIsDisplayed()
    }

    @Test
    fun imperialButtonIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("IMPERIAL").assertIsDisplayed()
    }

    @Test
    fun kmhLabelIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("km/h").assertIsDisplayed()
    }

    @Test
    fun mphLabelIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("mph").assertIsDisplayed()
    }

    // --- Keep screen on section ---

    @Test
    fun keepScreenOnSectionTitleIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("Keep Screen On").assertIsDisplayed()
    }

    @Test
    fun keepScreenOnSwitchIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("Enabled").assertIsDisplayed()
    }

    // --- About section ---

    @Test
    fun aboutSectionIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("About").assertIsDisplayed()
    }

    @Test
    fun appVersionIsDisplayed() {
        composeTestRule.setContent {
            SpeedometerTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = buildViewModel())
            }
        }
        composeTestRule.onNodeWithText("Speedometer v1.0").assertIsDisplayed()
    }
}
