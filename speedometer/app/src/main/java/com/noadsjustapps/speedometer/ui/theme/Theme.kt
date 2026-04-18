package com.noadsjustapps.speedometer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SpeedometerColorScheme = darkColorScheme(
    primary = SpeedGreen,
    onPrimary = SpeedWhite,
    secondary = SpeedYellow,
    onSecondary = SpeedBlack,
    tertiary = SpeedGreen,
    onTertiary = SpeedWhite,
    background = SpeedBlack,
    onBackground = SpeedWhite,
    surface = SpeedBlack,
    onSurface = SpeedWhite,
    error = SpeedRed,
    onError = SpeedWhite,
    surfaceVariant = SpeedDarkGray,
    onSurfaceVariant = SpeedLightGray
)

@Composable
fun SpeedometerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SpeedometerColorScheme,
        typography = Typography,
        content = content
    )
}