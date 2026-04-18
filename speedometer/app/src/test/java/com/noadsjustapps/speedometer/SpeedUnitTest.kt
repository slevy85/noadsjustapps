package com.noadsjustapps.speedometer

import com.noadsjustapps.speedometer.data.SpeedUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class SpeedUnitTest {

    private val delta = 0.001f

    // convertSpeed

    @Test
    fun `convertSpeed METRIC converts m-s to km-h correctly`() {
        assertEquals(36.0f, SpeedUnit.METRIC.convertSpeed(10f), delta)
    }

    @Test
    fun `convertSpeed METRIC zero speed stays zero`() {
        assertEquals(0f, SpeedUnit.METRIC.convertSpeed(0f), delta)
    }

    @Test
    fun `convertSpeed IMPERIAL converts m-s to mph correctly`() {
        assertEquals(22.3694f, SpeedUnit.IMPERIAL.convertSpeed(10f), delta)
    }

    @Test
    fun `convertSpeed IMPERIAL zero speed stays zero`() {
        assertEquals(0f, SpeedUnit.IMPERIAL.convertSpeed(0f), delta)
    }

    @Test
    fun `convertSpeed METRIC 1 m-s is 3_6 km-h`() {
        assertEquals(3.6f, SpeedUnit.METRIC.convertSpeed(1f), delta)
    }

    @Test
    fun `convertSpeed IMPERIAL 1 m-s is 2_23694 mph`() {
        assertEquals(2.23694f, SpeedUnit.IMPERIAL.convertSpeed(1f), delta)
    }

    // convertDistance

    @Test
    fun `convertDistance METRIC converts meters to km`() {
        assertEquals(1.0f, SpeedUnit.METRIC.convertDistance(1000f), delta)
    }

    @Test
    fun `convertDistance METRIC 500m is 0_5 km`() {
        assertEquals(0.5f, SpeedUnit.METRIC.convertDistance(500f), delta)
    }

    @Test
    fun `convertDistance IMPERIAL converts meters to miles`() {
        assertEquals(1.0f, SpeedUnit.IMPERIAL.convertDistance(1609.34f), delta)
    }

    @Test
    fun `convertDistance IMPERIAL 800m converts correctly`() {
        val expected = 800f / 1609.34f
        assertEquals(expected, SpeedUnit.IMPERIAL.convertDistance(800f), delta)
    }

    @Test
    fun `convertDistance zero stays zero for both units`() {
        assertEquals(0f, SpeedUnit.METRIC.convertDistance(0f), delta)
        assertEquals(0f, SpeedUnit.IMPERIAL.convertDistance(0f), delta)
    }

    // speedLabel

    @Test
    fun `speedLabel METRIC returns km-h`() {
        assertEquals("km/h", SpeedUnit.METRIC.speedLabel())
    }

    @Test
    fun `speedLabel IMPERIAL returns mph`() {
        assertEquals("mph", SpeedUnit.IMPERIAL.speedLabel())
    }

    // distanceLabel

    @Test
    fun `distanceLabel METRIC returns km`() {
        assertEquals("km", SpeedUnit.METRIC.distanceLabel())
    }

    @Test
    fun `distanceLabel IMPERIAL returns mi`() {
        assertEquals("mi", SpeedUnit.IMPERIAL.distanceLabel())
    }
}
