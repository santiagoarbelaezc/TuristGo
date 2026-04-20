package com.turistgo.app.features.moderator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.filled.Logout
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorSettingsScreen(
    innerPadding: PaddingValues = PaddingValues(),
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    val brandLogoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // --- 1. HEADER (SUBTITLE + TITLE + LOGO) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Sesión",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Configuración",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            AsyncImage(
                model = brandLogoUrl,
                contentDescription = null,
                modifier = Modifier.size(45.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. SECCIÓN DE PERFIL (ICONO ESCUDO) ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color(0xFFFFF1F0) // Fondo rosado muy suave
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Shield, 
                        null, 
                        Modifier.size(48.dp), 
                        Color(0xFFE53935) // Rojo TuristGo
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Sesión de Moderador", 
                fontSize = 20.sp, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "admin@turistgo.com", 
                fontSize = 15.sp, 
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. ITEMS DE CONFIGURACIÓN (CARDS BLANCAS) ---
            SettingsItemCard(
                icon = Icons.Default.Language, 
                title = "Idioma de la interfaz", 
                subtitle = "Español"
            )
            SettingsItemCard(
                icon = Icons.Default.Notifications, 
                title = "Alertas de revisión", 
                subtitle = "Activado"
            )
            SettingsItemCard(
                icon = Icons.Default.Shield, 
                title = "Políticas de moderación", 
                subtitle = "Ver guía"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- 4. BOTÓN CERRAR SESIÓN (ESPECIFICO DEL MOCKUP) ---
            Surface(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFEBEE), // Rojo muy tenue
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout, 
                        null, 
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Cerrar Sesión Administrativa", 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFFD32F2F)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SettingsItemCard(icon: ImageVector, title: String, subtitle: String) {
    Surface(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, 
                null, 
                Modifier.size(28.dp), 
                Color(0xFF555555) // Gris oscuro para iconos
            )
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    subtitle, 
                    fontSize = 13.sp, 
                    color = Color.Gray
                )
            }
            Icon(
                Icons.Default.ChevronRight, 
                null, 
                Modifier.size(18.dp), 
                Color.LightGray
            )
        }
    }
}
