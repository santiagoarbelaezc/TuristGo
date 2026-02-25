package com.turistgo.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Configuración", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                SettingsSectionTitle("Preferencias")
                SettingsItem(icon = Icons.Default.Language, title = "Idioma", subtitle = "Español (Colombia)")
                SettingsItem(icon = Icons.Default.Notifications, title = "Notificaciones", subtitle = "Configurar alertas")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SettingsSectionTitle("Información Legal")
                SettingsItem(icon = Icons.Default.Policy, title = "Política de Uso")
                SettingsItem(icon = Icons.Default.PrivacyTip, title = "Privacidad")
                SettingsItem(icon = Icons.Default.Info, title = "Acerca de TuristGo", subtitle = "Versión 1.0.0")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SettingsSectionTitle("Cuenta")
                SettingsItem(
                    icon = Icons.Default.Logout, 
                    title = "Cerrar Sesión", 
                    textColor = MaterialTheme.colorScheme.onBackground,
                    onClick = onLogout
                )
                SettingsItem(
                    icon = Icons.Default.DeleteForever, 
                    title = "Eliminar cuenta", 
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = { /* Confirm and Delete */ }
                )
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector, 
    title: String, 
    subtitle: String? = null,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (textColor == MaterialTheme.colorScheme.error) textColor else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                if (subtitle != null) {
                    Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
