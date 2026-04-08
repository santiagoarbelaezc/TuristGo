package com.turistgo.app.features.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.turistgo.app.core.components.Destination
import com.turistgo.app.core.components.DestinationCard
import com.turistgo.app.features.feed.components.FeedSearchBar

import com.turistgo.app.features.feed.components.SearchContent

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
                MapPlaceholder(destinations)
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
@Composable
fun MapPlaceholder(destinations: List<Destination>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        // Grid pattern
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 80.dp.toPx()
            for (i in 0..size.width.toInt() step step.toInt()) {
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = androidx.compose.ui.geometry.Offset(i.toFloat(), 0f),
                    end = androidx.compose.ui.geometry.Offset(i.toFloat(), size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            for (j in 0..size.height.toInt() step step.toInt()) {
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = androidx.compose.ui.geometry.Offset(0f, j.toFloat()),
                    end = androidx.compose.ui.geometry.Offset(size.width, j.toFloat()),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Mock Markers covering some destinations
        val markers = listOf(
            Pair(0.2f, 0.3f) to destinations[0],
            Pair(0.5f, 0.2f) to destinations[1],
            Pair(0.3f, 0.6f) to destinations[2],
            Pair(0.7f, 0.5f) to destinations[3]
        )

        markers.forEach { (pos, dest) ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = (pos.first * 300).dp, top = (pos.second * 500).dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = dest.name,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Overlay FABs
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { /* Recenter */ },
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
        
        // Status Badge
        Surface(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            shadowElevation = 2.dp
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Memory, null, Modifier.size(14.dp), MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cargando entorno interactivo...", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
