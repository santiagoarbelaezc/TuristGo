// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.notifications

// Importaciones de animaciones y UI
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importaciones de modelo de datos
import com.turistgo.app.R
import com.turistgo.app.domain.model.Notification
import com.turistgo.app.domain.model.NotificationType
import kotlinx.coroutines.flow.collectLatest // Para recolectar flujos de navegación
import java.text.SimpleDateFormat
import java.util.*

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de notificaciones
@Composable
fun NotificationsScreen(
    innerPadding: PaddingValues = PaddingValues(0.dp), // Padding de navegación (opcional)
    onNavigateToPostDetail: (String) -> Unit = {}, // Callback para navegar al detalle de un post (recibe ID)
    onNavigateToUserProfile: (String) -> Unit = {}, // Callback para navegar al perfil de usuario (recibe ID)
    viewModel: NotificationsViewModel = androidx.hilt.navigation.compose.hiltViewModel() // ViewModel inyectado por Hilt
) {
    // Observa la lista de notificaciones del ViewModel
    val notifications by viewModel.notifications.collectAsState()

    // ==================== MANEJO DE NAVEGACIÓN ====================
    // Escucha eventos de navegación del ViewModel
    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is NotificationNavigationEvent.ToPostDetail -> onNavigateToPostDetail(event.postId) // Navega al post
                is NotificationNavigationEvent.ToUserProfile -> onNavigateToUserProfile(event.userId) // Navega al perfil
            }
        }
    }

    // Agrupa las notificaciones por fecha (HOY, AYER, o fecha específica)
    val groupedNotifications = remember(notifications) {
        groupNotificationsByDate(notifications)
    }

    // Columna principal que ocupa toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding()) // Padding inferior
            .background(MaterialTheme.colorScheme.background) // Fondo del tema
            .statusBarsPadding() // Padding para la barra de estado
    ) {
        // --- HEADER PREMIUM (EFECTO VIDRIO/GLASS) ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp // Sin elevación
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
                    // Título "Notificaciones" con estilo moderno
                    Text(
                        text = stringResource(R.string.notifications_title),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp // Espaciado negativo para estilo moderno
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    // Botón "Marcar todas como leídas" (solo visible si hay notificaciones no leídas)
                    if (notifications.any { !it.isRead }) {
                        Surface(
                            onClick = { viewModel.markAllAsRead() }, // Marca todas como leídas
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                Icons.Default.DoneAll, 
                                "Leídas", // Descripción para accesibilidad
                                modifier = Modifier.padding(10.dp).size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Contenido principal: estado vacío o lista de notificaciones
        if (notifications.isEmpty()) {
            EmptyNotificationsState() // Muestra pantalla de estado vacío
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre elementos
            ) {
                // Itera sobre los grupos de notificaciones (por fecha)
                groupedNotifications.forEach { (dateHeader, items) ->
                    // Item: Encabezado de fecha (HOY, AYER, o "DD MES")
                    item {
                        Text(
                            text = dateHeader,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                        )
                    }
                    // Items: Notificaciones de este grupo
                    items(items, key = { it.id }) { notification -> // key = id para optimización
                        NotificationCard(
                            notification = notification,
                            onClick = { viewModel.onNotificationClick(notification) }, // Marca como leída y navega
                            onAcceptFollow = { viewModel.acceptFollowRequest(notification.id) }, // Acepta solicitud de seguimiento
                            onRejectFollow = { viewModel.rejectFollowRequest(notification.id) } // Rechaza solicitud
                        )
                    }
                }
                
                // Espaciador final para evitar que el contenido quede pegado al fondo
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// Componente que muestra una tarjeta de notificación individual
@Composable
fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit, // Callback al hacer clic en la tarjeta
    onAcceptFollow: () -> Unit, // Callback para aceptar solicitud de seguimiento
    onRejectFollow: () -> Unit // Callback para rechazar solicitud
) {
    // Determina ícono y color según el tipo de notificación
    val (iconVec, iconColor) = when (notification.type) {
        NotificationType.NEW_POST     -> Icons.Default.Map to Color(0xFF9C27B0) // Púrpura
        NotificationType.VERIFICATION -> Icons.Default.Verified to Color(0xFF2E7D32) // Verde oscuro
        NotificationType.REPUTATION   -> Icons.Default.Star to Color(0xFFFFA000) // Ámbar
        NotificationType.COMMENT      -> Icons.Default.ChatBubbleOutline to Color(0xFFE91E63) // Rosa
        NotificationType.FOLLOW_REQUEST -> Icons.Default.PersonAddAlt1 to Color(0xFF1976D2) // Azul
        NotificationType.FOLLOW_ACCEPTED -> Icons.Default.PersonOutline to Color(0xFF00BFA5) // Verde menta
        else -> Icons.Default.Notifications to MaterialTheme.colorScheme.primary // Por defecto
    }

    // Indica si la notificación no ha sido leída
    val isUnread = !notification.isRead

    // Tarjeta con sombra, bordes redondeados y efecto de gradiente si no está leída
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isUnread) 6.dp else 1.dp, // Más sombra si no está leída
                shape = RoundedCornerShape(24.dp),
                spotColor = if (isUnread) iconColor.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.05f) // Color de sombra acorde al tipo
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                color = MaterialTheme.colorScheme.surface // Fondo de superficie
            )
            .clickable { onClick() } // Toda la tarjeta es clickeable
            // Gradiente suave en el lateral izquierdo si no está leída
            .background(
                brush = if (isUnread) {
                    Brush.horizontalGradient(
                        colors = listOf(iconColor.copy(alpha = 0.08f), Color.Transparent)
                    )
                } else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .border(
                width = 1.dp,
                color = if (isUnread) iconColor.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.15f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(18.dp) // Padding interno
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // --- Círculo con ícono (con glow suave) ---
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isUnread) iconColor.copy(alpha = 0.15f) else iconColor.copy(alpha = 0.05f)),
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

            // --- Contenido de la notificación ---
            Column(modifier = Modifier.weight(1f)) {
                // Título (más negrita si no está leída)
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isUnread) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = if (isUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Mensaje
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // --- Botones de acción para solicitudes de seguimiento (solo si no leída) ---
                if (notification.type == NotificationType.FOLLOW_REQUEST && !notification.isRead) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Botón Aceptar
                        Button(
                            onClick = onAcceptFollow,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aceptar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        // Botón Rechazar
                        OutlinedButton(
                            onClick = onRejectFollow,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text("Rechazar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            // --- Indicador visual de no leído (punto de color) ---
            if (isUnread) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(iconColor)
                        .align(Alignment.Top)
                )
            }
        }
    }
}

// Componente que muestra el estado cuando no hay notificaciones
@Composable
fun EmptyNotificationsState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Ícono grande semitransparente
            Icon(
                Icons.Default.NotificationsNone,
                null,
                Modifier.size(100.dp).alpha(0.15f),
                MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Título
            Text(
                "Silencio total", 
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            // Descripción
            Text(
                "Te avisaremos cuando haya novedades en tu comunidad aventurera.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Función auxiliar para agrupar notificaciones por fecha
private fun groupNotificationsByDate(notifications: List<Notification>): Map<String, List<Notification>> {
    // LinkedHashMap mantiene el orden de inserción
    val groups = LinkedHashMap<String, MutableList<Notification>>()
    val now = Calendar.getInstance()
    val today = now.get(Calendar.DAY_OF_YEAR)
    val year = now.get(Calendar.YEAR)
    
    // Formateador para fechas: "DD MES" (ej: "15 MARZO")
    val sdf = SimpleDateFormat("dd MMMM", Locale("es", "CO"))

    notifications.forEach { notification ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = notification.timestamp // Timestamp de la notificación
        
        // Determina el encabezado según la fecha
        val header = when {
            cal.get(Calendar.YEAR) == year && cal.get(Calendar.DAY_OF_YEAR) == today -> "HOY"
            cal.get(Calendar.YEAR) == year && cal.get(Calendar.DAY_OF_YEAR) == today - 1 -> "AYER"
            else -> sdf.format(cal.time).uppercase() // Ej: "15 MARZO"
        }
        
        groups.getOrPut(header) { mutableListOf() }.add(notification)
    }
    return groups
}
