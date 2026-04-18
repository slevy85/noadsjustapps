package com.noadsjustapps.speedometer.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackRecordDaoTest {

    private lateinit var database: SpeedometerDatabase
    private lateinit var dao: TrackRecordDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SpeedometerDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.trackRecordDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetTracks() = runBlocking {
        val track = TrackRecord(
            startTimeMillis = 1000L,
            durationMillis = 60000L,
            distanceMeters = 500f,
            maxSpeedMs = 10f,
            avgSpeedMs = 5f,
            label = "Morning Ride"
        )
        dao.insert(track)

        val tracks = dao.getAllSortedByDate().first()
        assertEquals(1, tracks.size)
        assertEquals("Morning Ride", tracks[0].label)
        assertEquals(1000L, tracks[0].startTimeMillis)
    }

    @Test
    fun deleteById() = runBlocking {
        val track = TrackRecord(
            startTimeMillis = 1000L,
            durationMillis = 60000L,
            distanceMeters = 500f,
            maxSpeedMs = 10f,
            avgSpeedMs = 5f
        )
        val id = dao.insert(track)
        
        dao.deleteById(id)
        
        val tracks = dao.getAllSortedByDate().first()
        assertTrue(tracks.isEmpty())
    }

    @Test
    fun updateLabel() = runBlocking {
        val track = TrackRecord(
            startTimeMillis = 1000L,
            durationMillis = 60000L,
            distanceMeters = 500f,
            maxSpeedMs = 10f,
            avgSpeedMs = 5f,
            label = "Old Label"
        )
        val id = dao.insert(track)
        
        dao.updateLabel(id, "New Label")
        
        val tracks = dao.getAllSortedByDate().first()
        assertEquals("New Label", tracks[0].label)
    }

    @Test
    fun getAllSortedByDate() = runBlocking {
        val track1 = TrackRecord(startTimeMillis = 1000L, durationMillis = 10L, distanceMeters = 1f, maxSpeedMs = 1f, avgSpeedMs = 1f)
        val track2 = TrackRecord(startTimeMillis = 3000L, durationMillis = 10L, distanceMeters = 1f, maxSpeedMs = 1f, avgSpeedMs = 1f)
        val track3 = TrackRecord(startTimeMillis = 2000L, durationMillis = 10L, distanceMeters = 1f, maxSpeedMs = 1f, avgSpeedMs = 1f)
        
        dao.insert(track1)
        dao.insert(track2)
        dao.insert(track3)
        
        val tracks = dao.getAllSortedByDate().first()
        assertEquals(3, tracks.size)
        assertEquals(3000L, tracks[0].startTimeMillis)
        assertEquals(2000L, tracks[1].startTimeMillis)
        assertEquals(1000L, tracks[2].startTimeMillis)
    }
}
