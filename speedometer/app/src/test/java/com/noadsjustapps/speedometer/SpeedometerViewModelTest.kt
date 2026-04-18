package com.noadsjustapps.speedometer

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.noadsjustapps.speedometer.data.SpeedUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class, sdk = [33])
class SpeedometerViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SpeedometerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = SpeedometerViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Initial state ---

    @Test
    fun `initial rideData is not tracking`() {
        val data = viewModel.rideData.value
        assertFalse(data.isTracking)
        assertFalse(data.isPaused)
    }

    @Test
    fun `initial rideData has zero speeds and distance`() {
        val data = viewModel.rideData.value
        assertEquals(0f, data.currentSpeed)
        assertEquals(0f, data.averageSpeed)
        assertEquals(0f, data.maxSpeed)
        assertEquals(0f, data.distance)
        assertEquals(0L, data.elapsedTimeMillis)
    }

    @Test
    fun `initial speedUnit is METRIC`() {
        assertEquals(SpeedUnit.METRIC, viewModel.speedUnit.value)
    }

    @Test
    fun `initial isMapVisible is false`() {
        assertFalse(viewModel.isMapVisible.value)
    }

    @Test
    fun `initial keepScreenOn is true`() {
        assertTrue(viewModel.keepScreenOn.value)
    }

    // --- startTracking ---

    @Test
    fun `startTracking sets isTracking to true`() {
        viewModel.startTracking()
        assertTrue(viewModel.rideData.value.isTracking)
    }

    @Test
    fun `startTracking sets isPaused to false`() {
        viewModel.startTracking()
        assertFalse(viewModel.rideData.value.isPaused)
    }

    @Test
    fun `startTracking is idempotent when already tracking`() {
        viewModel.startTracking()
        viewModel.startTracking()
        assertTrue(viewModel.rideData.value.isTracking)
        assertFalse(viewModel.rideData.value.isPaused)
    }

    // --- pauseTracking ---

    @Test
    fun `pauseTracking sets isPaused when tracking`() {
        viewModel.startTracking()
        viewModel.pauseTracking()
        assertTrue(viewModel.rideData.value.isTracking)
        assertTrue(viewModel.rideData.value.isPaused)
    }

    @Test
    fun `pauseTracking has no effect when not tracking`() {
        viewModel.pauseTracking()
        assertFalse(viewModel.rideData.value.isPaused)
        assertFalse(viewModel.rideData.value.isTracking)
    }

    @Test
    fun `pauseTracking has no effect when already paused`() {
        viewModel.startTracking()
        viewModel.pauseTracking()
        viewModel.pauseTracking()
        assertTrue(viewModel.rideData.value.isPaused)
    }

    // --- resumeTracking ---

    @Test
    fun `resumeTracking clears isPaused`() {
        viewModel.startTracking()
        viewModel.pauseTracking()
        viewModel.resumeTracking()
        assertTrue(viewModel.rideData.value.isTracking)
        assertFalse(viewModel.rideData.value.isPaused)
    }

    @Test
    fun `resumeTracking has no effect when not paused`() {
        viewModel.startTracking()
        viewModel.resumeTracking()
        assertTrue(viewModel.rideData.value.isTracking)
        assertFalse(viewModel.rideData.value.isPaused)
    }

    @Test
    fun `resumeTracking has no effect when not tracking`() {
        viewModel.resumeTracking()
        assertFalse(viewModel.rideData.value.isTracking)
        assertFalse(viewModel.rideData.value.isPaused)
    }

    // --- stopTracking ---

    @Test
    fun `stopTracking clears isTracking`() {
        viewModel.startTracking()
        viewModel.stopTracking()
        assertFalse(viewModel.rideData.value.isTracking)
    }

    @Test
    fun `stopTracking sets currentSpeed to zero`() {
        viewModel.startTracking()
        viewModel.stopTracking()
        assertEquals(0f, viewModel.rideData.value.currentSpeed)
    }

    @Test
    fun `stopTracking clears isPaused`() {
        viewModel.startTracking()
        viewModel.pauseTracking()
        viewModel.stopTracking()
        assertFalse(viewModel.rideData.value.isPaused)
    }

    @Test
    fun `stopTracking has no effect when not tracking`() {
        viewModel.stopTracking()
        assertFalse(viewModel.rideData.value.isTracking)
    }

    // --- resetRide ---

    @Test
    fun `resetRide returns rideData to default`() {
        viewModel.startTracking()
        viewModel.resetRide()
        val data = viewModel.rideData.value
        assertFalse(data.isTracking)
        assertFalse(data.isPaused)
        assertEquals(0f, data.currentSpeed)
        assertEquals(0f, data.averageSpeed)
        assertEquals(0f, data.maxSpeed)
        assertEquals(0f, data.distance)
        assertEquals(0L, data.elapsedTimeMillis)
    }

    @Test
    fun `resetRide works even when not tracking`() {
        viewModel.resetRide()
        assertFalse(viewModel.rideData.value.isTracking)
    }

    // --- toggleMapVisibility ---

    @Test
    fun `toggleMapVisibility flips isMapVisible from false to true`() {
        assertFalse(viewModel.isMapVisible.value)
        viewModel.toggleMapVisibility()
        assertTrue(viewModel.isMapVisible.value)
    }

    @Test
    fun `toggleMapVisibility flips isMapVisible back to false`() {
        viewModel.toggleMapVisibility()
        viewModel.toggleMapVisibility()
        assertFalse(viewModel.isMapVisible.value)
    }

    // --- tracking state machine full cycle ---

    @Test
    fun `full ride cycle start-pause-resume-stop`() {
        assertFalse(viewModel.rideData.value.isTracking)

        viewModel.startTracking()
        assertTrue(viewModel.rideData.value.isTracking)
        assertFalse(viewModel.rideData.value.isPaused)

        viewModel.pauseTracking()
        assertTrue(viewModel.rideData.value.isTracking)
        assertTrue(viewModel.rideData.value.isPaused)

        viewModel.resumeTracking()
        assertTrue(viewModel.rideData.value.isTracking)
        assertFalse(viewModel.rideData.value.isPaused)

        viewModel.stopTracking()
        assertFalse(viewModel.rideData.value.isTracking)
        assertFalse(viewModel.rideData.value.isPaused)
    }
}
