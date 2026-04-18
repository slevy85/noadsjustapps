package com.noadsjustapps.speedometer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackRecord::class], version = 1, exportSchema = false)
abstract class SpeedometerDatabase : RoomDatabase() {

    abstract fun trackRecordDao(): TrackRecordDao

    companion object {
        @Volatile
        private var INSTANCE: SpeedometerDatabase? = null

        fun getInstance(context: Context): SpeedometerDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SpeedometerDatabase::class.java,
                    "speedometer_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
