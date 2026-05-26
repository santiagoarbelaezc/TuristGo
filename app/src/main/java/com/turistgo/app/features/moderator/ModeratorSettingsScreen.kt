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
import com.turistgo.app.core.locale.AppLanguage
import com.turistgo.app.core.locale.LanguageState
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorSettingsScreen(
    innerPadding: PaddingValues = PaddingValues(),
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    val brandLogoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"

    val context = LocalContext.current
    val lang by LanguageState.current

    var showLanguagePicker by remember { mutableStateOf(false) }
    var showPoliciesDialog by remember { mutableStateOf(false) }
    var alertsEnabled by remember { mutableStateOf(true) }

    if (showLanguagePicker) {
        AlertDialog(
            onDismissRequest = { showLanguagePicker = false },
            title = { Text("Seleccionar idioma", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    AppLanguage.entries.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = lang == language,
                                onClick = {
                                    LanguageState.current.value = language
                                    showLanguagePicker = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(language.displayName, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguagePicker = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showPoliciesDialog) {
        AlertDialog(
            onDismissRequest = { showPoliciesDialog = false },
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guía de Moderación", fontWeight = FontWeight.Bold) 
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Como moderador de TuristGo, debes asegurar que el contenido compartido cumpla con los estándares de la comunidad:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    ModerationRule(
                        number = "1.",
                        title = "Contenido Temático",
                        desc = "Solo se permiten publicaciones relacionadas con turismo, eventos, gastronomía, naturaleza o cultura en Colombia."
                    )
                    ModerationRule(
                        number = "2.",
                        title = "Respeto y Convivencia",
                        desc = "Rechaza inmediatamente cualquier post con contenido violento, discriminatorio, ofensivo o de odio."
                    )
                    ModerationRule(
                        number = "3.",
                        title = "Información Verídica",
                        desc = "Verifica que la ubicación geográfica y descripción correspondan a un lugar o evento real."
                    )
                    ModerationRule(
                        number = "4.",
                        title = "Calidad de Imagen",
                        desc = "Las fotos deben ser nítidas y descriptivas del lugar o evento. Evita imágenes borrosas o inapropiadas."
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPoliciesDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido")
                }
            }
        )
    }

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
                subtitle = lang.displayName,
                onClick = { showLanguagePicker = true }
            )
            SettingsItemCard(
                icon = Icons.Default.Notifications, 
                title = "Alertas de revisión", 
                subtitle = if (alertsEnabled) "Activado" else "Desactivado",
                onClick = {
                    alertsEnabled = !alertsEnabled
                    Toast.makeText(
                        context, 
                        "Alertas de revisión ${if (alertsEnabled) "activadas" else "desactivadas"}", 
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            SettingsItemCard(
                icon = Icons.Default.Shield, 
                title = "Políticas de moderación", 
                subtitle = "Ver guía",
                onClick = { showPoliciesDialog = true }
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
fun SettingsItemCard(
    icon: ImageVector, 
    title: String, 
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
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

@Composable
fun ModerationRule(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = number,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = desc,
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}
