package com.turistgo.app.core.theme

import androidx.compose.ui.graphics.Color

// ── TuristGo · Paleta unificada ────────────────────────────────────────────

// Rojo principal (acento de marca)
val BrandRed      = Color(0xFFD32F2F)   // usado en botones, ícons, selección
val BrandRedLight = Color(0xFFFFEBEB)   // fondo rosado suave (bottom bar, chips)

// Fondos
val AppBackground = Color(0xFFF5F0EB)   // beige cálido — pantalla principal para TODAS las pantallas autenticadas
val CardBackground = Color(0xFFEFEAE4)  // beige ligeramente más oscuro para cards / secciones

// Tipografía
val TextPrimary   = Color(0xFF1A1A1A)   // negro suave para títulos / cuerpo
val TextSecondary = Color(0xFF888888)   // gris medio para secundarios / timestamps

// Mantenemos compatibilidad con el ColorScheme de Material3
val VibrantRed  = BrandRed
val Charcoal    = TextPrimary
val Smoke       = TextSecondary
val Mist        = Color(0xFFE8E3DD)      // borde / divisor
val OffWhite    = AppBackground

val Primary      = BrandRed
val OnPrimary    = Color.White
val Background   = AppBackground
val OnBackground = TextPrimary
val Surface      = Color.White
val OnSurface    = TextPrimary
