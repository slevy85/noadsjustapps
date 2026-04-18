package com.noadsjustapps.speedometer.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class TrackRepository(context: Context) {

    private val dao = SpeedometerDatabase.getInstance(context).trackRecordDao()

    val allTracks: Flow<List<TrackRecord>> = dao.getAllSortedByDate()

    suspend fun save(record: TrackRecord): Long = dao.insert(record)

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun updateLabel(id: Long, label: String) = dao.updateLabel(id, label)
}
