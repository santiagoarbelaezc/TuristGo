package com.turistgo.app.core.theme

import androidx.compose.runtime.mutableStateOf

/**
 * Global state for the application theme.
 * isDarkMode:
 * - null: Use system default
 * - true: Force dark mode
 * - false: Force light mode
 */
object ThemeState {
    val isDarkMode = mutableStateOf<Boolean?>(null)
}
