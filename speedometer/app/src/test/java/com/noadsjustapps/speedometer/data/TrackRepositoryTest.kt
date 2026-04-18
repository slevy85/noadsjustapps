package com.noadsjustapps.speedometer.data

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class, sdk = [33])
class TrackRepositoryTest {

    private lateinit var repository: TrackRepository
    private lateinit var database: SpeedometerDatabase

    @Before
    fun setup() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        // Note: Repository uses the singleton SpeedometerDatabase.getInstance(context)
        // This will create a real DB file on the JVM's filesystem. 
        // In a real project, we should inject the database into the repository.
        repository = TrackRepository(application)
        database = SpeedometerDatabase.getInstance(application)
    }

    @After
    fun teardown() {
        database.close()
        // Delete the database file to ensure test isolation
        val application = ApplicationProvider.getApplicationContext<Application>()
        application.getDatabasePath("speedometer_database").delete()
    }

    @Test
    fun saveAndGetAllTracks() = runTest {
        val track = TrackRecord(
            startTimeMillis = 1000L,
            durationMillis = 60000L,
            distanceMeters = 500f,
            maxSpeedMs = 10f,
            avgSpeedMs = 5f,
            label = "Morning Ride"
        )
        
        repository.save(track)
        
        repository.allTracks.test {
            val history = awaitItem()
            assertEquals(1, history.size)
            assertEquals("Morning Ride", history[0].label)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateLabel() = runTest {
        val track = TrackRecord(
            startTimeMillis = 1000L,
            durationMillis = 60000L,
            distanceMeters = 500f,
            maxSpeedMs = 10f,
            avgSpeedMs = 5f,
            label = "Old Label"
        )
        
        val id = repository.save(track)
        repository.updateLabel(id, "New Label")
        
        repository.allTracks.test {
            val history = awaitItem()
            assertEquals("New Label", history[0].label)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteTrack() = runTest {
        val track = TrackRecord(
            startTimeMillis = 1000L,
            durationMillis = 60000L,
            distanceMeters = 500f,
            maxSpeedMs = 10f,
            avgSpeedMs = 5f
        )
        
        val id = repository.save(track)
        repository.delete(id)
        
        repository.allTracks.test {
            val history = awaitItem()
            assertTrue(history.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
