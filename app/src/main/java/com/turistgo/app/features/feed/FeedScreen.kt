// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.feed

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List // Ícono de lista con soporte RTL
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importaciones de componentes personalizados
import com.turistgo.app.core.components.Destination
import com.turistgo.app.core.components.DestinationCard
import com.turistgo.app.features.feed.components.FeedSearchBar
import com.turistgo.app.features.feed.components.SearchContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de feed
@Composable
fun FeedScreen(
    innerPadding: PaddingValues, // Padding de navegación (status bar, etc.)
    onNavigateToDetail: (String) -> Unit, // Callback para navegar al detalle de un post (recibe ID)
    onNavigateToUserProfile: (String) -> Unit, // Callback para navegar al perfil de usuario (recibe ID)
    viewModel: FeedViewModel = hiltViewModel() // ViewModel inyectado por Hilt
) {
    // Estados observados del ViewModel
    val userSession by viewModel.userSession.collectAsState(initial = null) // Sesión del usuario actual
    val suggestedUsers by viewModel.suggestedUsers.collectAsState() // Usuarios sugeridos para seguir
    
    // Estados de búsqueda y filtrado
    val searchQuery by viewModel.searchQuery.collectAsState() // Texto de búsqueda
    val selectedCategory by viewModel.searchCategory.collectAsState() // Categoría/filtro seleccionado
    val filteredPosts by viewModel.filteredPosts.collectAsState() // Lista de posts filtrados

    // Estados locales de UI
    var isSearchActive by remember { mutableStateOf(false) } // Indica si la barra de búsqueda está activa
    var isMapView by remember { mutableStateOf(false) } // Indica si se muestra vista de mapa o lista
    
    // Categorías para el feed principal (cuando NO hay búsqueda activa)
    val feedCategories = listOf(
        stringResource(R.string.cat_all),      // "Todos"
        stringResource(R.string.cat_mountain), // "Montaña"
        stringResource(R.string.cat_beach),    // "Playa"
        stringResource(R.string.cat_gastronomy), // "Gastronomía"
        stringResource(R.string.cat_culture),  // "Cultura"
        stringResource(R.string.cat_adventure) // "Aventura"
    )
    
    // Filtros para el modo de búsqueda (cuando hay búsqueda activa)
    val searchFilters = listOf(
        stringResource(R.string.filter_all),      // "Todo"
        stringResource(R.string.filter_events),   // "Eventos"
        stringResource(R.string.filter_places),   // "Lugares"
        stringResource(R.string.filter_concerts), // "Conciertos"
        stringResource(R.string.filter_sports)    // "Deportes"
    )

    // Las categorías mostradas dependen del modo (búsqueda activa o no)
    val displayedCategories = if (isSearchActive) searchFilters else feedCategories

    // Columna principal que ocupa toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding()) // Solo padding inferior
            .background(MaterialTheme.colorScheme.background) // Fondo del tema
            .statusBarsPadding() // Padding para la barra de estado
    ) {
        // --- Barra superior (TopAppBar) ---
        TopAppBar(
            title = {
                Column {
                    // Fila con indicador visual (punto) y mensaje de bienvenida
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(6.dp), // Punto pequeño de 6dp
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.welcome_msg, userSession?.name ?: stringResource(R.string.default_user)), // "¡Bienvenido, {nombre}!"
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    // Título principal
                    Text(
                        text = stringResource(R.string.explore_world), // "Explora el mundo"
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp // Espaciado negativo para estilo moderno
                    )
                }
            },
            actions = {
                // Botón para alternar entre vista de mapa y lista
                Surface(
                    onClick = { isMapView = !isMapView }, // Alterna el estado
                    shape = RoundedCornerShape(16.dp),
                    color = if (isMapView) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.height(40.dp).padding(end = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Ícono cambia según la vista actual
                        Icon(
                            imageVector = if (isMapView) Icons.AutoMirrored.Filled.List else Icons.Default.Map,
                            contentDescription = stringResource(R.string.change_view),
                            tint = if (isMapView) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMapView) "Lista" else "Mapa",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMapView) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        // --- Barra de búsqueda ---
        val context = LocalContext.current
        FeedSearchBar(
            query = searchQuery,
            onQueryChange = { 
                viewModel.updateSearchQuery(it) // Actualiza el texto de búsqueda en el ViewModel
                if (it.isEmpty() && !isSearchActive) {
                    // Si está vacío y no activo, no hacer nada
                } else {
                    isSearchActive = true // Activa el modo de búsqueda
                }
            },
            onFocusChange = { active ->
                isSearchActive = active || searchQuery.isNotEmpty() // Activo si tiene foco o texto
                if (!isSearchActive) {
                    viewModel.updateSearchCategory(context.getString(R.string.cat_all)) // Resetea categoría al salir
                }
            }
        )

        // --- Chips de filtro (categorías) ---
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayedCategories) { category ->
                FilterChip(
                    selected = selectedCategory == category, // Marca como seleccionado si coincide
                    onClick = { viewModel.updateSearchCategory(category) }, // Actualiza la categoría en el ViewModel
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // --- Contenido principal (cambia según estado: búsqueda, mapa o lista) ---
        Box(modifier = Modifier.weight(1f)) { // Ocupa el espacio restante
            if (isSearchActive) {
                // Modo: Búsqueda activa - muestra resultados de búsqueda y usuarios sugeridos
                SearchContent(
                    results = filteredPosts,
                    suggestedUsers = suggestedUsers,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToProfile = onNavigateToUserProfile
                )
            } else if (isMapView) {
                // Modo: Vista de mapa - muestra ubicaciones en un mapa
                val destinations = filteredPosts.map { 
                    Destination(it.id, it.name, it.location, it.rating, it.imageUrl)
                }
                MapPlaceholder(destinations) // Placeholder del mapa (implementación simplificada)
            } else {
                // Modo: Vista de lista (feed principal)
                val savedPostIds by viewModel.savedPostIds.collectAsState() // IDs de posts guardados por el usuario
                val likedPostIds by viewModel.likedPostIds.collectAsState() // IDs de posts que le gustaron al usuario
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Item: Título de la sección
                    item {
                        Text(
                            text = stringResource(R.string.popular_destinations), // "Destinos populares"
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    // Items: Lista de posts (cada post es una tarjeta)
                    items(filteredPosts) { post ->
                        val destination = Destination(
                            id = post.id, 
                            name = post.name, 
                            location = post.location, 
                            rating = post.rating, 
                            imageUrl = post.imageUrl,
                            commentCount = post.commentCount,
                            createdAt = post.createdAt
                        )
                        DestinationCard(
                            destination = destination,
                            isSaved = savedPostIds.contains(post.id), // Indica si el post está guardado
                            isLiked = likedPostIds.contains(post.id), // Indica si al usuario le gusta el post
                            onSaveToggle = { viewModel.toggleSave(post.id) }, // Alterna guardado
                            onLikeToggle = { viewModel.toggleLike(post.id) }, // Alterna like
                            onClick = { onNavigateToDetail(post.id) } // Navega al detalle
                        )
                    }
                    // Item: Espaciador final
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

// Placeholder del mapa (implementación simplificada)
@Composable
fun MapPlaceholder(destinations: List<com.turistgo.app.core.components.Destination>) {
    // Contenedor que simula un mapa
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)), // Fondo gris claro
        contentAlignment = Alignment.Center
    ) {
        Text("Mapa Interactivo cargado con ${destinations.size} destinos") // Texto informativo
    }
}
