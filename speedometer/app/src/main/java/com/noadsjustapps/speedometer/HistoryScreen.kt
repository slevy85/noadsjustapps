package com.noadsjustapps.speedometer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.noadsjustapps.speedometer.data.SpeedUnit
import com.noadsjustapps.speedometer.data.TrackRecord
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: SpeedometerViewModel = viewModel()
) {
    val tracks by viewModel.trackHistory.collectAsState()
    val speedUnit by viewModel.speedUnit.collectAsState()

    var trackToDelete by remember { mutableStateOf<TrackRecord?>(null) }
    var trackToLabel by remember { mutableStateOf<TrackRecord?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Ride History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Divider(color = Color(0xFF2A2A2A))

            if (tracks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No rides yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E9E9E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start tracking and your rides\nwill appear here.",
                            fontSize = 14.sp,
                            color = Color(0xFF616161),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tracks, key = { it.id }) { track ->
                        TrackCard(
                            track = track,
                            speedUnit = speedUnit,
                            onEditLabel = { trackToLabel = track },
                            onDelete = { trackToDelete = track }
                        )
                    }
                }
            }
        }

        if (trackToDelete != null) {
            DeleteConfirmDialog(
                onConfirm = {
                    viewModel.deleteTrack(trackToDelete!!.id)
                    trackToDelete = null
                },
                onDismiss = { trackToDelete = null }
            )
        }

        if (trackToLabel != null) {
            EditLabelDialog(
                currentLabel = trackToLabel!!.label,
                onConfirm = { newLabel ->
                    viewModel.updateTrackLabel(trackToLabel!!.id, newLabel)
                    trackToLabel = null
                },
                onDismiss = { trackToLabel = null }
            )
        }
    }
}

@Composable
private fun TrackCard(
    track: TrackRecord,
    speedUnit: SpeedUnit,
    onEditLabel: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(track.startTimeMillis) {
        SimpleDateFormat("EEE, dd MMM yyyy  HH:mm", Locale.getDefault())
            .format(Date(track.startTimeMillis))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 13.sp,
                    color = Color(0xFF9E9E9E)
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onEditLabel),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (track.label.isBlank()) "Add label…" else track.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (track.label.isBlank()) Color(0xFF616161) else Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit label",
                    tint = Color(0xFF616161),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HistoryMetric(
                    label = "DISTANCE",
                    value = String.format("%.2f", speedUnit.convertDistance(track.distanceMeters)),
                    unit = speedUnit.distanceLabel()
                )
                HistoryMetric(
                    label = "DURATION",
                    value = formatTime(track.durationMillis),
                    unit = ""
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HistoryMetric(
                    label = "AVG SPEED",
                    value = String.format("%.1f", speedUnit.convertSpeed(track.avgSpeedMs)),
                    unit = speedUnit.speedLabel()
                )
                HistoryMetric(
                    label = "MAX SPEED",
                    value = String.format("%.1f", speedUnit.convertSpeed(track.maxSpeedMs)),
                    unit = speedUnit.speedLabel()
                )
            }
        }
    }
}

@Composable
private fun HistoryMetric(
    label: String,
    value: String,
    unit: String
) {
    Column(
        modifier = Modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text("Delete ride?", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Text("This ride will be permanently removed from history.", color = Color(0xFFE0E0E0))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("DELETE", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color(0xFF9E9E9E))
            }
        }
    )
}

@Composable
private fun EditLabelDialog(
    currentLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentLabel) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text("Add label", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("e.g. Morning commute", color = Color(0xFF616161)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color(0xFF424242),
                    cursorColor = Color(0xFF4CAF50)
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text.trim()) }) {
                Text("SAVE", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color(0xFF9E9E9E))
            }
        }
    )
}
