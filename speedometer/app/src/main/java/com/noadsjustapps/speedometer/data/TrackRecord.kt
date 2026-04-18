package com.noadsjustapps.speedometer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_records")
data class TrackRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeMillis: Long,
    val durationMillis: Long,
    val distanceMeters: Float,
    val maxSpeedMs: Float,
    val avgSpeedMs: Float,
    val label: String = ""
)
