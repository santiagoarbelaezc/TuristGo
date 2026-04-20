package com.turistgo.app.features.notifications

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turistgo.app.R
import com.turistgo.app.domain.model.Notification
import com.turistgo.app.domain.model.NotificationType
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateToPostDetail: (String) -> Unit = {},
    viewModel: NotificationsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    // Manejo de Navegación desde Notificaciones
    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is NotificationNavigationEvent.ToPostDetail -> {
                    onNavigateToPostDetail(event.postId)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // --- HEADER PREMIUM ---
        Surface(
            modifier = Modifier.fillMaxWidth().shadow(4.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.notifications_title),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    if (notifications.any { !it.isRead }) {
                        IconButton(
                            onClick = { viewModel.markAllAsRead() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(Icons.Default.DoneAll, "Marcar todas como leídas", modifier = Modifier.size(20.dp))
                        }
                    }
                }
                
                Text(
                    text = if (notifications.count { !it.isRead } > 0) 
                        "Tienes ${notifications.count { !it.isRead }} notificaciones nuevas" 
                    else "Todas tus notificaciones están al día",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (notifications.isEmpty()) {
            EmptyNotificationsState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = { viewModel.onNotificationClick(notification) },
                        onAcceptFollow = { viewModel.acceptFollowRequest(notification.id) },
                        onRejectFollow = { viewModel.rejectFollowRequest(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit,
    onAcceptFollow: () -> Unit,
    onRejectFollow: () -> Unit
) {
    val (iconVec, iconColor) = when (notification.type) {
        NotificationType.NEW_POST     -> Icons.Default.Explore to Color(0xFF673AB7)
        NotificationType.VERIFICATION -> Icons.Default.VerifiedUser to Color(0xFF4CAF50)
        NotificationType.REPUTATION   -> Icons.Default.LocalFireDepartment to Color(0xFFFF9800)
        NotificationType.COMMENT      -> Icons.Default.Comment to Color(0xFFE91E63)
        NotificationType.FOLLOW_REQUEST -> Icons.Default.PersonSearch to Color(0xFF2196F3)
        NotificationType.FOLLOW_ACCEPTED -> Icons.Default.GroupAdd to Color(0xFF009688)
        else -> Icons.Default.Notifications to MaterialTheme.colorScheme.primary
    }

    val isUnread = !notification.isRead

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isUnread) 4.dp else 0.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = iconColor.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isUnread) Color.White else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                // Icono decorativo con gradiente suave de fondo
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(iconColor.copy(alpha = 0.1f), iconColor.copy(alpha = 0.2f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVec,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        fontWeight = if (isUnread) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isUnread) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = notification.message,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hace un momento", 
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium
                    )

                    // Acciones especiales para Solicitud de Seguimiento
                    if (notification.type == NotificationType.FOLLOW_REQUEST && !notification.isRead) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onAcceptFollow,
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("Aceptar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            OutlinedButton(
                                onClick = onRejectFollow,
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("Rechazar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(iconColor)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.NotificationsNone,
                null,
                Modifier.size(80.dp),
                MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No hay notificaciones", 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Te avisaremos cuando pase algo interesante",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp
            )
        }
    }
}
