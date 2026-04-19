package com.turistgo.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = BrandRed,
    onPrimary          = Color.White,
    primaryContainer   = BrandRedLight,
    onPrimaryContainer = BrandRed,
    secondary          = TextSecondary,
    onSecondary        = Color.White,
    tertiary           = TextPrimary,
    background         = AppBackground,       // beige unificado
    onBackground       = TextPrimary,
    surface            = Color.White,
    onSurface          = TextPrimary,
    surfaceVariant     = CardBackground,      // cards / secciones
    onSurfaceVariant   = TextPrimary,
    outline            = Mist,
    outlineVariant     = Mist,
    error              = BrandRed
)

private val DarkColorScheme = darkColorScheme(
    primary            = BrandRed,
    onPrimary          = Color.White,
    secondary          = TextSecondary,
    background         = Color(0xFF1C1C1E),
    onBackground       = Color(0xFFF5F0EB),
    surface            = Color(0xFF2C2C2E),
    onSurface          = Color(0xFFF5F0EB)
)

@Composable
fun TuristGoTheme(
    darkTheme: Boolean = ThemeState.isDarkMode.value ?: isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = Shapes,
        content     = content
    )
}
