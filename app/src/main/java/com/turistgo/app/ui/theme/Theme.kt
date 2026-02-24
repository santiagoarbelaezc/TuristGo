package com.turistgo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VibrantRed,
    onPrimary = Color.White,
    secondary = Smoke,
    onSecondary = Color.White,
    tertiary = Charcoal,
    background = OffWhite,
    onBackground = Charcoal,
    surface = Color.White,
    onSurface = Charcoal,
    surfaceVariant = Mist,
    onSurfaceVariant = Charcoal
)

private val DarkColorScheme = darkColorScheme(
    primary = VibrantRed,
    onPrimary = Color.White,
    secondary = Smoke,
    background = Charcoal,
    onBackground = OffWhite,
    surface = Color(0xFF3D3D3D),
    onSurface = OffWhite
)

@Composable
fun TuristGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
