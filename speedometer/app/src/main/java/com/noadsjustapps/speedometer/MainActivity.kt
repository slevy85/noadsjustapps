package com.noadsjustapps.speedometer

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noadsjustapps.speedometer.ui.theme.SpeedometerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedometerTheme {
                val navController = rememberNavController()
                val viewModel: SpeedometerViewModel = viewModel()
                val keepScreenOn by viewModel.keepScreenOn.collectAsState()
                val rideData by viewModel.rideData.collectAsState()

                if (keepScreenOn && rideData.isTracking) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "speedometer"
                    ) {
                        composable("speedometer") {
                            SpeedometerScreen(
                                onNavigateToSettings = {
                                    navController.navigate("settings")
                                },
                                onNavigateToHistory = {
                                    navController.navigate("history")
                                },
                                viewModel = viewModel
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                viewModel = viewModel
                            )
                        }
                        composable("history") {
                            HistoryScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}