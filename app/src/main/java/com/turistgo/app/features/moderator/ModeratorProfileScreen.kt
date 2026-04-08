package com.turistgo.app.features.moderator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// NavController import omitted
import coil.compose.AsyncImage
import com.turistgo.app.core.navigation.MainRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorProfileScreen(
    onLogout: () -> Unit
) {
    // Datos "quemados" del Moderador
    val adminName = "Admin TuristGo"
    val adminEmail = "admin@turistgo.com"
    val adminImageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772044271/turistgo-logo_evi36h.png" // O una imagen de admin

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Perfil de Moderador", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                // Foto de Perfil Admin
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = adminImageUrl,
                        contentDescription = "Foto de administrador",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentScale = ContentScale.Fit
                    )
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = adminName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = adminEmail,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Estadísticas de Moderación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ModeratorStat(label = "Verificadas", value = "142")
                    ModeratorStat(label = "Rechazadas", value = "28")
                    ModeratorStat(label = "Reportes", value = "12")
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Opciones de Administración
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Configuración de Seguridad", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    
                    AdminLargeButton(
                        title = "Bitácora de Actividad",
                        icon = Icons.Default.History,
                        onClick = {}
                    )
                    AdminLargeButton(
                        title = "Preferencias de Notificación",
                        icon = Icons.Default.Notifications,
                        onClick = {}
                    )
                    AdminLargeButton(
                        title = "Cambiar Contraseña de Acceso",
                        icon = Icons.Default.Lock,
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Soporte Técnico", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    AdminLargeButton(
                        title = "Reportar Bug del Sistema",
                        icon = Icons.Default.BugReport,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
fun ModeratorStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun AdminLargeButton(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}
