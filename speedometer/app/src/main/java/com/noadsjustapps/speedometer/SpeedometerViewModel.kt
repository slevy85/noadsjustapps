package com.noadsjustapps.speedometer

import android.app.Application
import android.content.Intent
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noadsjustapps.speedometer.data.LocationData
import com.noadsjustapps.speedometer.data.RideData
import com.noadsjustapps.speedometer.data.SpeedUnit
import com.noadsjustapps.speedometer.data.TrackRecord
import com.noadsjustapps.speedometer.data.TrackRepository
import com.noadsjustapps.speedometer.data.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

class SpeedometerViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val trackRepository = TrackRepository(application)
    
    private val _rideData = MutableStateFlow(RideData())
    val rideData: StateFlow<RideData> = _rideData.asStateFlow()
    
    private val _speedUnit = MutableStateFlow(SpeedUnit.METRIC)
    val speedUnit: StateFlow<SpeedUnit> = _speedUnit.asStateFlow()
    
    private val _keepScreenOn = MutableStateFlow(true)
    val keepScreenOn: StateFlow<Boolean> = _keepScreenOn.asStateFlow()
    
    private val _isMapVisible = MutableStateFlow(false)
    val isMapVisible: StateFlow<Boolean> = _isMapVisible.asStateFlow()
    
    private var lastLocation: Location? = null
    private var totalDistance = 0f
    private var totalSpeedSum = 0f
    private var speedReadings = 0
    private var maxSpeed = 0f
    private var startTime = 0L
    private var elapsedTime = 0L
    private var pausedTime = 0L
    private var timerJob: Job? = null
    private var rideStartTime = 0L
    private val routePoints = mutableListOf<Pair<Double, Double>>()

    val trackHistory = trackRepository.allTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            userPreferences.speedUnit.collect { unit ->
                _speedUnit.value = unit
            }
        }
        
        viewModelScope.launch {
            userPreferences.keepScreenOn.collect { enabled ->
                _keepScreenOn.value = enabled
            }
        }
        
        viewModelScope.launch {
            LocationService.locationFlow.collect { locationData ->
                locationData?.let { processLocationUpdate(it) }
            }
        }
    }

    fun startTracking() {
        if (!_rideData.value.isTracking) {
            val intent = Intent(getApplication(), LocationService::class.java)
            ContextCompat.startForegroundService(getApplication(), intent)
            
            rideStartTime = System.currentTimeMillis()
            startTime = rideStartTime
            elapsedTime = 0L
            pausedTime = 0L
            
            _rideData.value = _rideData.value.copy(
                isTracking = true,
                isPaused = false
            )
            
            startTimer()
        }
    }

    fun pauseTracking() {
        if (_rideData.value.isTracking && !_rideData.value.isPaused) {
            pausedTime = System.currentTimeMillis()
            _rideData.value = _rideData.value.copy(isPaused = true)
            timerJob?.cancel()
        }
    }

    fun resumeTracking() {
        if (_rideData.value.isTracking && _rideData.value.isPaused) {
            val pauseDuration = System.currentTimeMillis() - pausedTime
            startTime += pauseDuration
            _rideData.value = _rideData.value.copy(isPaused = false)
            startTimer()
        }
    }

    fun stopTracking() {
        if (_rideData.value.isTracking) {
            val intent = Intent(getApplication(), LocationService::class.java)
            getApplication<Application>().stopService(intent)

            val snapshot = _rideData.value
            if (snapshot.distance > 0f || snapshot.elapsedTimeMillis > 0L) {
                viewModelScope.launch {
                    trackRepository.save(
                        TrackRecord(
                            startTimeMillis = rideStartTime,
                            durationMillis = snapshot.elapsedTimeMillis,
                            distanceMeters = snapshot.distance,
                            maxSpeedMs = snapshot.maxSpeed,
                            avgSpeedMs = snapshot.averageSpeed
                        )
                    )
                }
            }

            _rideData.value = _rideData.value.copy(
                isTracking = false,
                isPaused = false,
                currentSpeed = 0f
            )
            
            timerJob?.cancel()
        }
    }

    fun resetRide() {
        stopTracking()
        
        lastLocation = null
        totalDistance = 0f
        totalSpeedSum = 0f
        speedReadings = 0
        maxSpeed = 0f
        startTime = 0L
        elapsedTime = 0L
        pausedTime = 0L
        rideStartTime = 0L
        routePoints.clear()
        
        _rideData.value = RideData()
    }

    fun updateTrackLabel(id: Long, label: String) {
        viewModelScope.launch {
            trackRepository.updateLabel(id, label)
        }
    }

    fun deleteTrack(id: Long) {
        viewModelScope.launch {
            trackRepository.delete(id)
        }
    }

    fun toggleSpeedUnit() {
        viewModelScope.launch {
            val newUnit = if (_speedUnit.value == SpeedUnit.METRIC) {
                SpeedUnit.IMPERIAL
            } else {
                SpeedUnit.METRIC
            }
            userPreferences.setSpeedUnit(newUnit)
        }
    }

    fun toggleKeepScreenOn() {
        viewModelScope.launch {
            userPreferences.setKeepScreenOn(!_keepScreenOn.value)
        }
    }

    fun toggleMapVisibility() {
        _isMapVisible.value = !_isMapVisible.value
    }

    fun startLocationUpdates() {
        val intent = Intent(getApplication(), LocationService::class.java)
        ContextCompat.startForegroundService(getApplication(), intent)
    }

    fun stopLocationUpdates() {
        if (!_rideData.value.isTracking) {
            val intent = Intent(getApplication(), LocationService::class.java)
            getApplication<Application>().stopService(intent)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_rideData.value.isTracking && !_rideData.value.isPaused) {
                delay(1000)
                elapsedTime = System.currentTimeMillis() - startTime
                _rideData.value = _rideData.value.copy(
                    elapsedTimeMillis = elapsedTime
                )
            }
        }
    }

    private fun processLocationUpdate(locationData: LocationData) {
        val currentLocation = locationData.location
        val currentSpeed = locationData.speed

        if (_rideData.value.isTracking && !_rideData.value.isPaused) {
            lastLocation?.let { last ->
                if (currentLocation.accuracy < 50f) {
                    val distance = last.distanceTo(currentLocation)
                    if (distance > 2f) {
                        totalDistance += distance
                    }
                }
            }

            if (currentSpeed > 0.5f) {
                totalSpeedSum += currentSpeed
                speedReadings++
                maxSpeed = max(maxSpeed, currentSpeed)
            }

            val averageSpeed = if (speedReadings > 0) {
                totalSpeedSum / speedReadings
            } else {
                0f
            }

            if (currentLocation.accuracy < 50f) {
                routePoints.add(Pair(currentLocation.latitude, currentLocation.longitude))
            }

            _rideData.value = _rideData.value.copy(
                currentSpeed = currentSpeed,
                averageSpeed = averageSpeed,
                maxSpeed = maxSpeed,
                distance = totalDistance,
                gpsAccuracy = locationData.accuracy,
                currentLatitude = currentLocation.latitude,
                currentLongitude = currentLocation.longitude,
                waypoints = routePoints.toList()
            )

            lastLocation = currentLocation
        } else {
            _rideData.value = _rideData.value.copy(
                currentLatitude = currentLocation.latitude,
                currentLongitude = currentLocation.longitude,
                gpsAccuracy = locationData.accuracy
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
