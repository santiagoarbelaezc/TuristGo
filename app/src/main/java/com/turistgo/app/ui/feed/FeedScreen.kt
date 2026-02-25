package com.turistgo.app.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.turistgo.app.ui.components.Destination
import com.turistgo.app.ui.components.DestinationCard
import com.turistgo.app.ui.feed.components.FeedSearchBar
import com.turistgo.app.ui.feed.components.SearchContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(onNavigateToDetail: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("Todos", "Montaña", "Playa", "Gastronomía", "Cultura", "Aventura")
    var selectedCategory by remember { mutableStateOf("Todos") }

    val destinations = listOf(
        Destination("Santuario de Las Lajas", "Ipiales, Nariño", "4.9", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/celebre-la-semana-santa-en-estos-cuatro-lugares-turisticos-de-colombia-1229852_ckbgrw.jpg"),
        Destination("San Andrés Islas", "San Andrés, Colombia", "4.8", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/destinos-naturales-en-colombia-sin-turismo-masivo_ei0akp.jpg"),
        Destination("Piedra del Peñol", "Guatapé, Antioquia", "4.7", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/SL3RJGIFWRCQDGAMA2XYX4QYRQ_dtneeb.jpg"),
        Destination("Nevado del Ruiz", "Manizales, Caldas", "4.6", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036016/Nevado_del_Ruiz_by_Edgar_mi099q.png"),
        Destination("Parque Tayrona", "Magdalena, Colombia", "4.9", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771996096/visitar-parque-tayrona-13_bwybj6.webp"),
        Destination("Cartagena de Indias", "Bolívar, Colombia", "4.8", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771996090/iStock-1165965255_copia_tvooih.jpg"),
        Destination("Valle del Cocora", "Quindío, Colombia", "4.7", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771996090/2022110906163046516_pgqroh.webp")
    )

    var isSearchActive by remember { mutableStateOf(false) }

    var isMapView by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TopBar content
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hola, Santiago",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Explora el mundo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    // Toggle Lista/Mapa
                    Surface(
                        onClick = { isMapView = !isMapView },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(45.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isMapView) Icons.Default.List else Icons.Default.Map,
                                contentDescription = "Cambiar vista",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                // Barra de búsqueda
                FeedSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onFocusChange = { isSearchActive = it || searchQuery.isNotEmpty() }
                )

                // Categorías
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                }
            }
        }

        // Main content
        Box(modifier = Modifier.weight(1f)) {
            if (isSearchActive) {
                SearchContent()
            } else if (isMapView) {
                // Placeholder para Mapa
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Vista de Mapa seleccionada", fontWeight = FontWeight.SemiBold)
                        Text("Aquí se mostrarían las publicaciones cercanas", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Destinos Populares",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(destinations) { destination ->
                        DestinationCard(destination) {
                            // En una app real usaríamos el ID real
                            onNavigateToDetail("post_${destination.name}")
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
