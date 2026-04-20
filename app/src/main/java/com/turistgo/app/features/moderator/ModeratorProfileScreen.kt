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
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R
import com.turistgo.app.core.navigation.MainRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorProfileScreen(
    innerPadding: PaddingValues = PaddingValues(),
    onLogout: () -> Unit
) {
    val adminName = "Admin TuristGo"
    val adminEmail = "admin@turistgo.com"
    val adminImageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772044271/turistgo-logo_evi36h.png" 
    val brandLogoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- 1. HEADER MODERNO CON LOGO ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = brandLogoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Panel de Control",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Moderador",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Surface(
                onClick = onLogout,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp, 
                        null, 
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- 2. AVATAR PREMIUM ---
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(4.dp, Color.White),
                            shadowElevation = 8.dp
                        ) {
                            AsyncImage(
                                model = adminImageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Surface(
                            modifier = Modifier.size(32.dp).offset(x = (-2).dp, y = (-2).dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Icon(Icons.Default.Security, null, Modifier.padding(6.dp), Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(adminName, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text(adminEmail, fontSize = 13.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- 3. STATS GRID (GLASSMORPHIC) ---
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(vertical = 24.dp)) {
                            // Primera Fila
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ModeratorStat(label = "Aprobadas", value = "142", color = Color(0xFF2E7D32))
                                VerticalDivider()
                                ModeratorStat(label = "Rechazadas", value = "28", color = Color(0xFFC62828))
                                VerticalDivider()
                                ModeratorStat(label = "Reportes", value = "12", color = Color(0xFFF9A825))
                            }
                            
                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 32.dp),
                                color = Color.LightGray.copy(alpha = 0.3f)
                            )
                            Spacer(Modifier.height(20.dp))
                            
                            // Segunda Fila
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ModeratorStat(label = "Baneados", value = "4", color = Color.Black)
                                VerticalDivider()
                                ModeratorStat(label = "Comentarios", value = "856", color = MaterialTheme.colorScheme.primary)
                                VerticalDivider()
                                ModeratorStat(label = "Precisión", value = "99%", color = Color(0xFF00ACC1))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // --- 4. SECCIONES DE CONFIGURACIÓN ---
                    SettingsSection("Seguridad de Acceso") {
                        AdminLargeButton("Bitácora de Actividad", Icons.Default.History)
                        AdminLargeButton("Preferencias de Alertas", Icons.Default.NotificationsActive)
                        AdminLargeButton("Cambiar Contraseña", Icons.Default.VpnKey)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SettingsSection("Asistencia") {
                        AdminLargeButton("Reportar Bug del Sistema", Icons.Default.BugReport)
                        AdminLargeButton("Guía de Moderación", Icons.Default.MenuBook)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(4.dp).height(16.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
fun ModeratorStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Composable
fun AdminLargeButton(title: String, icon: ImageVector) {
    Surface(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, Modifier.size(18.dp), Color.LightGray)
        }
    }
}
