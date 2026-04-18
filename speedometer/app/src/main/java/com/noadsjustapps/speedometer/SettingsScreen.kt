package com.noadsjustapps.speedometer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.noadsjustapps.speedometer.data.SpeedUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SpeedometerViewModel = viewModel()
) {
    val speedUnit by viewModel.speedUnit.collectAsState()
    val keepScreenOn by viewModel.keepScreenOn.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SettingItem(
                title = "Speed Unit",
                description = "Choose between metric and imperial units"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { 
                            if (speedUnit != SpeedUnit.METRIC) {
                                viewModel.toggleSpeedUnit()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (speedUnit == SpeedUnit.METRIC) 
                                Color(0xFF4CAF50) 
                            else 
                                Color(0xFF424242)
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("METRIC", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("km/h", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { 
                            if (speedUnit != SpeedUnit.IMPERIAL) {
                                viewModel.toggleSpeedUnit()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (speedUnit == SpeedUnit.IMPERIAL) 
                                Color(0xFF4CAF50) 
                            else 
                                Color(0xFF424242)
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("IMPERIAL", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("mph", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingItem(
                title = "Keep Screen On",
                description = "Prevent screen from sleeping during tracking"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (keepScreenOn) "Enabled" else "Disabled",
                        fontSize = 16.sp,
                        color = if (keepScreenOn) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Switch(
                        checked = keepScreenOn,
                        onCheckedChange = { viewModel.toggleKeepScreenOn() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color(0xFF9E9E9E),
                            uncheckedTrackColor = Color(0xFF424242)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Divider(color = Color(0xFF424242), thickness = 1.dp)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "About",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Speedometer v1.0",
                fontSize = 16.sp,
                color = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "A hyper-legible, zero-distraction speedometer for high-performance cyclists.",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        content()
    }
}
