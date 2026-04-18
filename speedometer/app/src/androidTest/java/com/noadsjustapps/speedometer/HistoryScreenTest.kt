package com.noadsjustapps.speedometer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.noadsjustapps.speedometer.data.SpeedUnit
import com.noadsjustapps.speedometer.data.TrackRecord
import com.noadsjustapps.speedometer.ui.theme.SpeedometerTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class HistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyHistory_showsEmptyStateMessage() {
        val viewModel = mockk<SpeedometerViewModel>(relaxed = true)
        every { viewModel.trackHistory } returns MutableStateFlow(emptyList())
        every { viewModel.speedUnit } returns MutableStateFlow(SpeedUnit.METRIC)

        composeTestRule.setContent {
            SpeedometerTheme {
                HistoryScreen(
                    onNavigateBack = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("No rides yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start tracking and your rides\nwill appear here.").assertIsDisplayed()
    }

    @Test
    fun tracksPresent_showsTrackCards() {
        val viewModel = mockk<SpeedometerViewModel>(relaxed = true)
        val tracks = listOf(
            TrackRecord(
                id = 1L,
                startTimeMillis = 1672531200000L, // 2023-01-01 00:00
                durationMillis = 3600000L, // 1 hour
                distanceMeters = 10000f, // 10 km
                maxSpeedMs = 10f, // 36 km/h
                avgSpeedMs = 5f, // 18 km/h
                label = "Morning Ride"
            )
        )
        every { viewModel.trackHistory } returns MutableStateFlow(tracks)
        every { viewModel.speedUnit } returns MutableStateFlow(SpeedUnit.METRIC)

        composeTestRule.setContent {
            SpeedometerTheme {
                HistoryScreen(
                    onNavigateBack = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Morning Ride").assertIsDisplayed()
        composeTestRule.onNodeWithText("10.00").assertIsDisplayed() // Distance in km
        composeTestRule.onNodeWithText("1:00:00").assertIsDisplayed() // Time
        composeTestRule.onNodeWithText("18.0").assertIsDisplayed() // Avg Speed
        composeTestRule.onNodeWithText("36.0").assertIsDisplayed() // Max Speed
    }

    @Test
    fun clickDelete_showsConfirmationDialog() {
        val viewModel = mockk<SpeedometerViewModel>(relaxed = true)
        val tracks = listOf(
            TrackRecord(
                id = 1L,
                startTimeMillis = 1672531200000L,
                durationMillis = 3600000L,
                distanceMeters = 10000f,
                maxSpeedMs = 10f,
                avgSpeedMs = 5f,
                label = "Morning Ride"
            )
        )
        every { viewModel.trackHistory } returns MutableStateFlow(tracks)
        every { viewModel.speedUnit } returns MutableStateFlow(SpeedUnit.METRIC)

        composeTestRule.setContent {
            SpeedometerTheme {
                HistoryScreen(
                    onNavigateBack = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        
        composeTestRule.onNodeWithText("Delete ride?").assertIsDisplayed()
        composeTestRule.onNodeWithText("This ride will be permanently removed from history.").assertIsDisplayed()
        composeTestRule.onNodeWithText("DELETE").assertIsDisplayed()
        composeTestRule.onNodeWithText("CANCEL").assertIsDisplayed()
    }

    @Test
    fun clickLabel_showsEditDialog() {
        val viewModel = mockk<SpeedometerViewModel>(relaxed = true)
        val tracks = listOf(
            TrackRecord(
                id = 1L,
                startTimeMillis = 1672531200000L,
                durationMillis = 3600000L,
                distanceMeters = 10000f,
                maxSpeedMs = 10f,
                avgSpeedMs = 5f,
                label = ""
            )
        )
        every { viewModel.trackHistory } returns MutableStateFlow(tracks)
        every { viewModel.speedUnit } returns MutableStateFlow(SpeedUnit.METRIC)

        composeTestRule.setContent {
            SpeedometerTheme {
                HistoryScreen(
                    onNavigateBack = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Add label…").performClick()
        
        composeTestRule.onNodeWithText("Add label").assertIsDisplayed()
        composeTestRule.onNodeWithText("SAVE").assertIsDisplayed()
        composeTestRule.onNodeWithText("CANCEL").assertIsDisplayed()
    }
}
