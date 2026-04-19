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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorSettingsScreen(
    innerPadding: PaddingValues = PaddingValues(),
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Header matches Feed/Dashboard style
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Sesión",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Configuración",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        
        Spacer(modifier = Modifier.height(32.dp))

        // Profile Section
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Security, null, Modifier.size(48.dp), MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Sesión de Moderador", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("admin@turistgo.com", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // Settings Groups
        SettingsItem(icon = Icons.Default.Language, title = "Idioma de la interfaz", subtitle = "Español")
        SettingsItem(icon = Icons.Default.Notifications, title = "Alertas de revisión", subtitle = "Activado")
        SettingsItem(icon = Icons.Default.Shield, title = "Políticas de moderación", subtitle = "Ver guía")
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Logout, null)
            Spacer(Modifier.width(12.dp))
            Text("Cerrar Sesión Administrativa", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, Modifier.size(24.dp), Color(0xFF555555))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, Modifier.size(20.dp), Color.LightGray)
        }
    }
}
