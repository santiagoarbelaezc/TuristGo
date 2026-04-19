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
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R

data class Notification(
    val id: Int,
    val titleKey: String,   // key into strings
    val messageKey: String,
    val timeKey: String,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType { NEW_POST, VERIFICATION, REPUTATION, SYSTEM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen() {
    val lang by LanguageState.current
    val s = AppStrings.get(lang)

    val initialNotifications = listOf(
        Notification(1, s.notifNewPost,  s.notifNewPostMsg,  s.timeAgo5m, NotificationType.NEW_POST,     false),
        Notification(2, s.notifVerified, s.notifVerifiedMsg, s.timeAgo1h, NotificationType.VERIFICATION, true),
        Notification(3, s.notifBadge,    s.notifBadgeMsg,    s.timeAgo3h, NotificationType.REPUTATION,   false),
        Notification(4, s.notifSecurity, s.notifSecurityMsg, s.timeAgo1d, NotificationType.SYSTEM,       true),
    )

    var notifications by remember(lang) { mutableStateOf(initialNotifications) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar — same pattern as ProfileScreen for consistent alignment
        TopAppBar(
            title = {
                Text(
                    text = s.notificationsTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            actions = {
                if (notifications.any { !it.isRead }) {
                    TextButton(
                        onClick = { notifications = notifications.map { it.copy(isRead = true) } }
                    ) {
                        Text(
                            text = s.readAll,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        null,
                        Modifier.size(64.dp),
                        MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(s.noNotifications, color = MaterialTheme.colorScheme.secondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = {
                            notifications = notifications.map { n ->
                                if (n.id == notification.id) n.copy(isRead = true) else n
                            }
                        }
                    )
                    if (notification.id != notifications.last().id) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val (iconVec, iconBg, iconTint) = when (notification.type) {
        NotificationType.NEW_POST     -> Triple(Icons.Default.Map,         Color(0xFFE8D5F5), Color(0xFF7B1FA2))
        NotificationType.VERIFICATION -> Triple(Icons.Default.Verified,    Color(0xFFE8F5E9), Color(0xFF2E7D32))
        NotificationType.REPUTATION   -> Triple(Icons.Default.EmojiEvents, Color(0xFFFFF3E0), Color(0xFFEF6C00))
        NotificationType.SYSTEM       -> Triple(Icons.Default.Info,        Color(0xFFE3F2FD), Color(0xFF1565C0))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (notification.isRead) Color.Transparent
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVec,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.titleKey,
                    fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                if (!notification.isRead) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.messageKey,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = notification.timeKey,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
