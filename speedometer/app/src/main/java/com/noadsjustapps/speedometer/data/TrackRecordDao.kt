package com.noadsjustapps.speedometer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: TrackRecord): Long

    @Query("DELETE FROM track_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE track_records SET label = :label WHERE id = :id")
    suspend fun updateLabel(id: Long, label: String)

    @Query("SELECT * FROM track_records ORDER BY startTimeMillis DESC")
    fun getAllSortedByDate(): Flow<List<TrackRecord>>
}
