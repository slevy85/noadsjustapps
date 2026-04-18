package com.noadsjustapps.speedometer.data

enum class SpeedUnit {
    METRIC,
    IMPERIAL;

    fun speedLabel(): String = when (this) {
        METRIC -> "km/h"
        IMPERIAL -> "mph"
    }

    fun distanceLabel(): String = when (this) {
        METRIC -> "km"
        IMPERIAL -> "mi"
    }

    fun convertSpeed(speedMps: Float): Float = when (this) {
        METRIC -> speedMps * 3.6f
        IMPERIAL -> speedMps * 2.23694f
    }

    fun convertDistance(distanceMeters: Float): Float = when (this) {
        METRIC -> distanceMeters / 1000f
        IMPERIAL -> distanceMeters / 1609.34f
    }
}
