package com.example.view360.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = AutomotiveBackground,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = TextPrimary,
    secondary = TrackOrange,
    onSecondary = AutomotiveBackground,
    background = AutomotiveBackground,
    onBackground = TextPrimary,
    surface = AutomotiveSurface,
    onSurface = TextPrimary,
    surfaceVariant = AutomotiveSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = AutomotiveBorder,
    error = AlertRed
)

@Composable
fun View360Theme(
    darkTheme: Boolean = true, // Automotive cockpit UI is optimal in high-contrast dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
