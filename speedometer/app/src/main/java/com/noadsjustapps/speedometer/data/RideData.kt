package com.noadsjustapps.speedometer.data

data class RideData(
    val currentSpeed: Float = 0f,
    val averageSpeed: Float = 0f,
    val maxSpeed: Float = 0f,
    val distance: Float = 0f,
    val elapsedTimeMillis: Long = 0L,
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val gpsAccuracy: Float = 0f,
    val currentLatitude: Double = 0.0,
    val currentLongitude: Double = 0.0,
    val waypoints: List<Pair<Double, Double>> = emptyList()
)
