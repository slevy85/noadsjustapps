package com.noadsjustapps.speedometer

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SpeedometerScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: SpeedometerViewModel = viewModel()
) {
    val rideData by viewModel.rideData.collectAsState()
    val speedUnit by viewModel.speedUnit.collectAsState()
    val isMapVisible by viewModel.isMapVisible.collectAsState()
    
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            viewModel.startLocationUpdates()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (locationPermissions.allPermissionsGranted) {
            SpeedometerContent(
                rideData = rideData,
                speedUnit = speedUnit,
                isMapVisible = isMapVisible,
                onStartTracking = { viewModel.startTracking() },
                onPauseTracking = { viewModel.pauseTracking() },
                onResumeTracking = { viewModel.resumeTracking() },
                onStopTracking = { viewModel.stopTracking() },
                onResetRide = { viewModel.resetRide() },
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToHistory = onNavigateToHistory,
                onToggleMap = { viewModel.toggleMapVisibility() }
            )
        } else {
            PermissionRequestScreen(
                onRequestPermissions = { locationPermissions.launchMultiplePermissionRequest() }
            )
        }
    }
}

@Composable
fun SpeedometerContent(
    rideData: com.noadsjustapps.speedometer.data.RideData,
    speedUnit: com.noadsjustapps.speedometer.data.SpeedUnit,
    isMapVisible: Boolean,
    onStartTracking: () -> Unit,
    onPauseTracking: () -> Unit,
    onResumeTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onResetRide: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onToggleMap: () -> Unit
) {
    if (isMapVisible) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapView(
                latitude = rideData.currentLatitude,
                longitude = rideData.currentLongitude,
                waypoints = rideData.waypoints,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onToggleMap) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Toggle Map",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = onNavigateToHistory) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.80f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format("%.0f", speedUnit.convertSpeed(rideData.currentSpeed)),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 80.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = speedUnit.speedLabel(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                MetricsGrid(rideData = rideData, speedUnit = speedUnit)

                Spacer(modifier = Modifier.height(16.dp))

                ControlButtons(
                    isTracking = rideData.isTracking,
                    isPaused = rideData.isPaused,
                    onStartTracking = onStartTracking,
                    onPauseTracking = onPauseTracking,
                    onResumeTracking = onResumeTracking,
                    onStopTracking = onStopTracking,
                    onResetRide = onResetRide
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onToggleMap) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Toggle Map",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = onNavigateToHistory) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.0f", speedUnit.convertSpeed(rideData.currentSpeed)),
                        fontSize = 160.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 160.sp
                    )
                    Text(
                        text = speedUnit.speedLabel(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFE0E0E0),
                        textAlign = TextAlign.Center
                    )
                }
            }

            MetricsGrid(rideData = rideData, speedUnit = speedUnit)

            Spacer(modifier = Modifier.height(24.dp))

            ControlButtons(
                isTracking = rideData.isTracking,
                isPaused = rideData.isPaused,
                onStartTracking = onStartTracking,
                onPauseTracking = onPauseTracking,
                onResumeTracking = onResumeTracking,
                onStopTracking = onStopTracking,
                onResetRide = onResetRide
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MetricsGrid(
    rideData: com.noadsjustapps.speedometer.data.RideData,
    speedUnit: com.noadsjustapps.speedometer.data.SpeedUnit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricItem(
                label = "DISTANCE",
                value = String.format("%.2f", speedUnit.convertDistance(rideData.distance)),
                unit = speedUnit.distanceLabel(),
                modifier = Modifier.weight(1f)
            )
            
            MetricItem(
                label = "TIME",
                value = formatTime(rideData.elapsedTimeMillis),
                unit = "",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricItem(
                label = "AVG SPEED",
                value = String.format("%.1f", speedUnit.convertSpeed(rideData.averageSpeed)),
                unit = speedUnit.speedLabel(),
                modifier = Modifier.weight(1f)
            )
            
            MetricItem(
                label = "MAX SPEED",
                value = String.format("%.1f", speedUnit.convertSpeed(rideData.maxSpeed)),
                unit = speedUnit.speedLabel(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricItem(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFE0E0E0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ControlButtons(
    isTracking: Boolean,
    isPaused: Boolean,
    onStartTracking: () -> Unit,
    onPauseTracking: () -> Unit,
    onResumeTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onResetRide: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (!isTracking) {
            Button(
                onClick = onStartTracking,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("START", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = onResetRide,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF757575)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("RESET", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            if (isPaused) {
                Button(
                    onClick = onResumeTracking,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Resume",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RESUME", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onPauseTracking,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PAUSE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Button(
                onClick = onStopTracking,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("STOP", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(
    onRequestPermissions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Location Permission Required",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This app needs location access to track your speed and distance while cycling.",
            fontSize = 16.sp,
            color = Color(0xFFE0E0E0),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onRequestPermissions,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text("GRANT PERMISSION", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun formatTime(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
