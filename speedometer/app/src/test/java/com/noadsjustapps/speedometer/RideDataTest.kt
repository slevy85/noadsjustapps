package com.noadsjustapps.speedometer

import com.noadsjustapps.speedometer.data.RideData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RideDataTest {

    @Test
    fun `default RideData has zeroed numeric fields`() {
        val data = RideData()
        assertEquals(0f, data.currentSpeed)
        assertEquals(0f, data.averageSpeed)
        assertEquals(0f, data.maxSpeed)
        assertEquals(0f, data.distance)
        assertEquals(0L, data.elapsedTimeMillis)
        assertEquals(0f, data.gpsAccuracy)
        assertEquals(0.0, data.currentLatitude, 0.0)
        assertEquals(0.0, data.currentLongitude, 0.0)
    }

    @Test
    fun `default RideData has tracking disabled`() {
        val data = RideData()
        assertFalse(data.isTracking)
        assertFalse(data.isPaused)
    }

    @Test
    fun `copy preserves unchanged fields`() {
        val original = RideData(
            currentSpeed = 10f,
            averageSpeed = 8f,
            maxSpeed = 15f,
            distance = 1000f,
            elapsedTimeMillis = 60000L,
            isTracking = true,
            isPaused = false,
            gpsAccuracy = 5f,
            currentLatitude = 51.5,
            currentLongitude = -0.1
        )

        val copied = original.copy(currentSpeed = 12f)

        assertEquals(12f, copied.currentSpeed)
        assertEquals(original.averageSpeed, copied.averageSpeed)
        assertEquals(original.maxSpeed, copied.maxSpeed)
        assertEquals(original.distance, copied.distance)
        assertEquals(original.elapsedTimeMillis, copied.elapsedTimeMillis)
        assertEquals(original.isTracking, copied.isTracking)
        assertEquals(original.isPaused, copied.isPaused)
        assertEquals(original.gpsAccuracy, copied.gpsAccuracy)
        assertEquals(original.currentLatitude, copied.currentLatitude, 0.0)
        assertEquals(original.currentLongitude, copied.currentLongitude, 0.0)
    }

    @Test
    fun `two RideData with same values are equal`() {
        val a = RideData(currentSpeed = 5f, isTracking = true)
        val b = RideData(currentSpeed = 5f, isTracking = true)
        assertEquals(a, b)
    }

    @Test
    fun `RideData copy with isPaused true reflects correctly`() {
        val tracking = RideData(isTracking = true, isPaused = false)
        val paused = tracking.copy(isPaused = true)
        assertEquals(true, paused.isTracking)
        assertEquals(true, paused.isPaused)
    }
}
