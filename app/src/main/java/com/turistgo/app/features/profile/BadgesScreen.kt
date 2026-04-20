// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.profile

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState // Para observar flujos de datos
import androidx.compose.runtime.getValue // Delegación de propiedades para estados
import androidx.hilt.navigation.compose.hiltViewModel // Para inyectar ViewModel con Hilt
import com.turistgo.app.R

// Clase de datos que representa una insignia (modelo de UI)
data class BadgeData(
    val title: String, // Título de la insignia (ej: "Primer Paso")
    val description: String, // Descripción de cómo obtenerla (ej: "Publicaste tu primer destino")
    val icon: ImageVector, // Ícono de la insignia
    val isUnlocked: Boolean = false, // Indica si la insignia está desbloqueada
    val progress: Float = 0f // Progreso hacia el desbloqueo (0f a 1f)
)

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de insignias
@Composable
fun BadgesScreen(
    onBack: () -> Unit = {}, // Callback para volver atrás (opcional con valor por defecto vacío)
    viewModel: ProfileViewModel = hiltViewModel() // ViewModel inyectado por Hilt (comparte datos con ProfileScreen)
) {
    // Color de fondo cálido para toda la pantalla
    val warmBg = Color(0xFFFBFAF5)
    // Observa las estadísticas del perfil desde el ViewModel (postsCount, savedCount, likedCount, etc.)
    val stats by viewModel.profileStats.collectAsState()
    
    // Lista de todas las insignias disponibles en la aplicación
    val badges = listOf(
        // Insignia: Primer Paso - Se desbloquea con al menos 1 publicación
        BadgeData(
            title = "Primer Paso", 
            description = "Publicaste tu primer destino", 
            icon = Icons.Default.Public, 
            isUnlocked = stats.postsCount >= 1, // Desbloqueada si postsCount >= 1
            progress = if (stats.postsCount >= 1) 1f else 0f // Progreso: 100% si desbloqueada
        ),
        // Insignia: Curador - Se desbloquea guardando 5 destinos
        BadgeData(
            title = "Curador", 
            description = "Guardaste 5 destinos favoritos", 
            icon = Icons.Default.Bookmark, 
            isUnlocked = stats.savedCount >= 5, // Desbloqueada si savedCount >= 5
            progress = (stats.savedCount.toFloat() / 5f).coerceAtMost(1f) // Progreso: savedCount / 5 (máx 1)
        ),
        // Insignia: Entusiasta - Se desbloquea dando 10 "Me gusta"
        BadgeData(
            title = "Entusiasta", 
            description = "Diste 10 'Me gusta' a otros", 
            icon = Icons.Default.Favorite, 
            isUnlocked = stats.likedCount >= 10, // Desbloqueada si likedCount >= 10
            progress = (stats.likedCount.toFloat() / 10f).coerceAtMost(1f) // Progreso: likedCount / 10 (máx 1)
        ),
        // Insignia: Crítico Experto - Próximamente (siempre bloqueada por ahora)
        BadgeData(
            title = "Crítico Experto", 
            description = "Diste 10 reseñas verificadas", 
            icon = Icons.Default.Star, 
            isUnlocked = false, // Aún no implementada
            progress = 0f // Sin progreso
        ),
        // Insignia: Verificador - Próximamente (siempre bloqueada por ahora)
        BadgeData(
            title = "Verificador", 
            description = "Ayudaste a verificar 5 lugares", 
            icon = Icons.Default.Verified, 
            isUnlocked = false, // Aún no implementada
            progress = 0f // Sin progreso
        ),
        // Insignia: Socialite - Próximamente (con progreso de ejemplo)
        BadgeData(
            title = "Socialite", 
            description = "Conectaste con 20 viajeros", 
            icon = Icons.Default.Group, 
            isUnlocked = false, // Aún no implementada
            progress = 0.1f // Progreso de ejemplo: 10% (1 de 20 conexiones)
        )
    )

    // Scaffold proporciona la estructura base con top bar y contenido
    Scaffold(
        topBar = {
            // Barra superior centrada con título y botón de retroceso
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.badges_title), fontWeight = FontWeight.Bold) }, // "Insignias"
                navigationIcon = {
                    IconButton(onClick = onBack) { // Botón de flecha hacia atrás
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = warmBg) // Mismo color de fondo
            )
        },
        containerColor = warmBg // Color de fondo del Scaffold
    ) { padding -> // padding interno para evitar la top bar
        // Columna principal que ocupa toda la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Aplica el padding del Scaffold
                .padding(horizontal = 24.dp) // Padding lateral de 24dp
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Espaciado superior
            
            // Título principal de la sección
            Text(
                text = "Tu Colección de Logros",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A) // Color gris muy oscuro casi negro
            )
            // Subtítulo descriptivo
            Text(
                text = "Desbloquea insignias explorando el mundo",
                fontSize = 14.sp,
                color = Color(0xFF666666) // Color gris medio
            )

            Spacer(modifier = Modifier.height(24.dp)) // Espaciado

            // Grid vertical de 2 columnas fijas para mostrar las insignias
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columnas fijas
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Espacio horizontal entre columnas: 16dp
                verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio vertical entre filas: 16dp
                contentPadding = PaddingValues(bottom = 24.dp) // Padding inferior de 24dp
            ) {
                // Itera sobre la lista de insignias y muestra una tarjeta por cada una
                items(badges) { badge ->
                    BadgeCard(badge) // Componente que muestra una insignia individual
                }
            }
        }
    }
}

// Componente que muestra una tarjeta de insignia individual
@Composable
fun BadgeCard(badge: BadgeData) {
    // Tarjeta con bordes redondeados de 24dp
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) Color.White else Color.White.copy(alpha = 0.5f) // Blanca sólida si desbloqueada, semitransparente si no
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp) // Sombra solo si está desbloqueada
    ) {
        // Columna centrada horizontalmente dentro de la tarjeta
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(), // Padding interno de 16dp, ocupa todo el ancho
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Superficie circular que contiene el ícono de la insignia
            Surface(
                modifier = Modifier.size(64.dp), // Tamaño fijo de 64dp
                shape = CircleShape, // Forma circular
                color = if (badge.isUnlocked) MaterialTheme.colorScheme.primaryContainer else Color(0xFFEEEEEE) // Color primario si desbloqueada, gris claro si no
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = null,
                    tint = if (badge.isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray, // Color primario si desbloqueada, gris si no
                    modifier = Modifier.padding(16.dp) // Padding interno de 16dp dentro del círculo
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp)) // Espacio entre ícono y título
            
            // Título de la insignia
            Text(
                text = badge.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (badge.isUnlocked) Color.Black else Color.Gray // Negro si desbloqueada, gris si no
            )
            
            // Descripción de la insignia (altura fija para mantener consistencia)
            Text(
                text = badge.description,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = if (badge.isUnlocked) Color.Gray else Color.LightGray, // Gris normal si desbloqueada, gris claro si no
                lineHeight = 14.sp, // Altura de línea fija
                modifier = Modifier.height(28.dp) // Altura fija de 28dp para mantener uniformidad
            )
            
            // Si la insignia NO está desbloqueada, muestra una barra de progreso
            if (!badge.isUnlocked) {
                Spacer(modifier = Modifier.height(8.dp)) // Espacio antes de la barra
                LinearProgressIndicator(
                    progress = { badge.progress }, // Progreso actual (valor entre 0f y 1f)
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape), // Ocupa todo el ancho, altura 4dp, bordes circulares
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Color primario semitransparente
                    trackColor = Color(0xFFEEEEEE) // Color de fondo de la barra (gris claro)
                )
            }
        }
    }
}
