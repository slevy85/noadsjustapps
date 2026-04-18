package com.noadsjustapps.speedometer.data

import android.location.Location

data class LocationData(
    val location: Location,
    val speed: Float,
    val accuracy: Float,
    val timestamp: Long
)
