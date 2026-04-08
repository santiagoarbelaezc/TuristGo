package com.turistgo.app.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType {
    NEW_POST, VERIFICATION, REPUTATION, SYSTEM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen() {
    val initialNotifications = listOf(
        Notification(1, "Nueva publicación en tu zona", "Se ha reportado una zona segura en el Valle del Cocora.", "Hace 5 min", NotificationType.NEW_POST, false),
        Notification(2, "Publicación Verificada", "¡Felicidades! Tu publicación en Cartagena ha sido verificada.", "Hace 1 hora", NotificationType.VERIFICATION, true),
        Notification(3, "Nuevo Logro Alcanzado", "Has ganado la insignia 'Primer Explorador'.", "Hace 3 horas", NotificationType.REPUTATION, false),
        Notification(4, "Recordatorio de Seguridad", "Revisa las nuevas pautas para el senderismo en época de lluvias.", "Hace 1 día", NotificationType.SYSTEM, true),
        Notification(5, "Alguien comentó tu post", "Santiago comentó: '¡Excelente información, gracias!'", "Hace 2 días", NotificationType.NEW_POST, true)
    )
    
    var notifications by remember { mutableStateOf(initialNotifications) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Notificaciones", fontWeight = FontWeight.Bold) },
            actions = {
                if (notifications.any { !it.isRead }) {
                    TextButton(onClick = { 
                        notifications = notifications.map { it.copy(isRead = true) }
                    }) {
                        Text("Leer todo")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsNone, null, Modifier.size(64.dp), MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No tienes notificaciones", color = MaterialTheme.colorScheme.secondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(notifications, key = { notification -> notification.id }) { notification ->
                    NotificationItem(notification) {
                        notifications = notifications.map { n ->
                            if (n.id == notification.id) n.copy(isRead = true) else n
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    val icon = when (notification.type) {
        NotificationType.NEW_POST -> Icons.Default.Map
        NotificationType.VERIFICATION -> Icons.Default.Verified
        NotificationType.REPUTATION -> Icons.Default.EmojiEvents
        NotificationType.SYSTEM -> Icons.Default.Info
    }

    val iconContainerColor = when (notification.type) {
        NotificationType.NEW_POST -> MaterialTheme.colorScheme.primaryContainer
        NotificationType.VERIFICATION -> Color(0xFFE8F5E9) // Green light
        NotificationType.REPUTATION -> Color(0xFFFFF3E0) // Orange light
        NotificationType.SYSTEM -> MaterialTheme.colorScheme.secondaryContainer
    }

    val iconTint = when (notification.type) {
        NotificationType.NEW_POST -> MaterialTheme.colorScheme.primary
        NotificationType.VERIFICATION -> Color(0xFF2E7D32) // Green
        NotificationType.REPUTATION -> Color(0xFFEF6C00) // Orange
        NotificationType.SYSTEM -> MaterialTheme.colorScheme.secondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (notification.isRead) Color.Transparent else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
            .padding(20.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icono
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Contenido
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.time,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
