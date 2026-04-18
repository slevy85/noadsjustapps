package com.noadsjustapps.speedometer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noadsjustapps.speedometer.data.RideData
import com.noadsjustapps.speedometer.data.SpeedUnit
import com.noadsjustapps.speedometer.ui.theme.SpeedometerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpeedometerContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        rideData: RideData = RideData(),
        speedUnit: SpeedUnit = SpeedUnit.METRIC,
        isMapVisible: Boolean = false,
        onStartTracking: () -> Unit = {},
        onPauseTracking: () -> Unit = {},
        onResumeTracking: () -> Unit = {},
        onStopTracking: () -> Unit = {},
        onResetRide: () -> Unit = {},
        onNavigateToSettings: () -> Unit = {},
        onToggleMap: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            SpeedometerTheme {
                SpeedometerContent(
                    rideData = rideData,
                    speedUnit = speedUnit,
                    isMapVisible = isMapVisible,
                    onStartTracking = onStartTracking,
                    onPauseTracking = onPauseTracking,
                    onResumeTracking = onResumeTracking,
                    onStopTracking = onStopTracking,
                    onResetRide = onResetRide,
                    onNavigateToSettings = onNavigateToSettings,
                    onToggleMap = onToggleMap
                )
            }
        }
    }

    // --- Speed display ---

    @Test
    fun speedDisplayShowsZeroWhenNotTracking() {
        setContent(rideData = RideData(currentSpeed = 0f), speedUnit = SpeedUnit.METRIC)
        composeTestRule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun speedUnitLabelKmhIsDisplayedForMetric() {
        setContent(speedUnit = SpeedUnit.METRIC)
        composeTestRule.onAllNodesWithText("km/h")[0].assertIsDisplayed()
    }

    @Test
    fun speedUnitLabelMphIsDisplayedForImperial() {
        setContent(speedUnit = SpeedUnit.IMPERIAL)
        composeTestRule.onAllNodesWithText("mph")[0].assertIsDisplayed()
    }

    @Test
    fun metricLabelsDistanceAndTimeAreDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("DISTANCE").assertIsDisplayed()
        composeTestRule.onNodeWithText("TIME").assertIsDisplayed()
    }

    @Test
    fun metricLabelsAvgAndMaxSpeedAreDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("AVG SPEED").assertIsDisplayed()
        composeTestRule.onNodeWithText("MAX SPEED").assertIsDisplayed()
    }

    // --- Control buttons: not tracking ---

    @Test
    fun whenNotTracking_startButtonIsDisplayed() {
        setContent(rideData = RideData(isTracking = false))
        composeTestRule.onNodeWithText("START").assertIsDisplayed()
    }

    @Test
    fun whenNotTracking_resetButtonIsDisplayed() {
        setContent(rideData = RideData(isTracking = false))
        composeTestRule.onNodeWithText("RESET").assertIsDisplayed()
    }

    @Test
    fun whenNotTracking_pauseButtonIsAbsent() {
        setContent(rideData = RideData(isTracking = false))
        composeTestRule.onNodeWithText("PAUSE").assertDoesNotExist()
    }

    @Test
    fun whenNotTracking_stopButtonIsAbsent() {
        setContent(rideData = RideData(isTracking = false))
        composeTestRule.onNodeWithText("STOP").assertDoesNotExist()
    }

    // --- Control buttons: tracking, not paused ---

    @Test
    fun whenTracking_pauseButtonIsDisplayed() {
        setContent(rideData = RideData(isTracking = true, isPaused = false))
        composeTestRule.onNodeWithText("PAUSE").assertIsDisplayed()
    }

    @Test
    fun whenTracking_stopButtonIsDisplayed() {
        setContent(rideData = RideData(isTracking = true, isPaused = false))
        composeTestRule.onNodeWithText("STOP").assertIsDisplayed()
    }

    @Test
    fun whenTracking_startButtonIsAbsent() {
        setContent(rideData = RideData(isTracking = true, isPaused = false))
        composeTestRule.onNodeWithText("START").assertDoesNotExist()
    }

    // --- Control buttons: tracking, paused ---

    @Test
    fun whenPaused_resumeButtonIsDisplayed() {
        setContent(rideData = RideData(isTracking = true, isPaused = true))
        composeTestRule.onNodeWithText("RESUME").assertIsDisplayed()
    }

    @Test
    fun whenPaused_stopButtonIsDisplayed() {
        setContent(rideData = RideData(isTracking = true, isPaused = true))
        composeTestRule.onNodeWithText("STOP").assertIsDisplayed()
    }

    @Test
    fun whenPaused_pauseButtonIsAbsent() {
        setContent(rideData = RideData(isTracking = true, isPaused = true))
        composeTestRule.onNodeWithText("PAUSE").assertDoesNotExist()
    }

    // --- Button callbacks ---

    @Test
    fun startButtonTriggerCallback() {
        var called = false
        setContent(
            rideData = RideData(isTracking = false),
            onStartTracking = { called = true }
        )
        composeTestRule.onNodeWithText("START").performClick()
        assertTrue(called)
    }

    @Test
    fun resetButtonTriggersCallback() {
        var called = false
        setContent(
            rideData = RideData(isTracking = false),
            onResetRide = { called = true }
        )
        composeTestRule.onNodeWithText("RESET").performClick()
        assertTrue(called)
    }

    @Test
    fun pauseButtonTriggersCallback() {
        var called = false
        setContent(
            rideData = RideData(isTracking = true, isPaused = false),
            onPauseTracking = { called = true }
        )
        composeTestRule.onNodeWithText("PAUSE").performClick()
        assertTrue(called)
    }

    @Test
    fun stopButtonTriggersCallback() {
        var called = false
        setContent(
            rideData = RideData(isTracking = true, isPaused = false),
            onStopTracking = { called = true }
        )
        composeTestRule.onNodeWithText("STOP").performClick()
        assertTrue(called)
    }

    @Test
    fun resumeButtonTriggersCallback() {
        var called = false
        setContent(
            rideData = RideData(isTracking = true, isPaused = true),
            onResumeTracking = { called = true }
        )
        composeTestRule.onNodeWithText("RESUME").performClick()
        assertTrue(called)
    }

    @Test
    fun settingsIconTriggersNavigationCallback() {
        var called = false
        setContent(onNavigateToSettings = { called = true })
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        assertTrue(called)
    }

    @Test
    fun mapIconToggleTriggersCallback() {
        var called = false
        setContent(onToggleMap = { called = true })
        composeTestRule.onNodeWithContentDescription("Toggle Map").performClick()
        assertTrue(called)
    }
}
