// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.post

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
// Importaciones de Google Maps Compose
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de selección de ubicación en mapa
@Composable
fun MapPickerScreen(
    initialLat: Double? = null, // Latitud inicial (opcional, puede ser null)
    initialLng: Double? = null, // Longitud inicial (opcional, puede ser null)
    onLocationSelected: (Double, Double) -> Unit, // Callback que devuelve las coordenadas seleccionadas (lat, lng)
    onNavigateBack: () -> Unit // Callback para volver a la pantalla anterior
) {
    // Ubicación por defecto: Bogotá, Colombia (centro de la ciudad)
    val defaultLocation = LatLng(4.6097, -74.0817)
    
    // Determina la ubicación inicial: usa la proporcionada o la de Bogotá por defecto
    val initialLocation = if (initialLat != null && initialLng != null) {
        LatLng(initialLat, initialLng) // Usa las coordenadas recibidas
    } else {
        defaultLocation // Usa Bogotá como fallback
    }

    // Estado de la cámara del mapa (posición, zoom, inclinación, etc.)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f) // Zoom nivel 15 (aproximadamente a nivel de calle)
    }

    // Estado que almacena la posición actual del marcador (se actualiza al tocar el mapa)
    var markerPosition by remember { mutableStateOf(initialLocation) }

    // Scaffold proporciona la estructura base con top bar y FAB (Floating Action Button)
    Scaffold(
        topBar = {
            // Barra superior con título, botón de retroceso y botón de confirmación
            TopAppBar(
                title = { Text("Seleccionar Ubicación") }, // Título de la pantalla
                navigationIcon = {
                    // Botón de flecha hacia atrás
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    // Botón de confirmación en la barra superior (texto)
                    TextButton(onClick = { 
                        onLocationSelected(markerPosition.latitude, markerPosition.longitude) // Envía las coordenadas actuales
                    }) {
                        Text("Confirmar", color = MaterialTheme.colorScheme.primary) // Texto en color primario
                    }
                }
            )
        },
        floatingActionButton = {
            // Botón flotante de acción (FAB) como alternativa de confirmación
            FloatingActionButton(
                onClick = { 
                    onLocationSelected(markerPosition.latitude, markerPosition.longitude) // Envía las coordenadas actuales
                },
                containerColor = MaterialTheme.colorScheme.primary // Color de fondo primario
            ) {
                Icon(Icons.Default.Check, contentDescription = "Confirmar", tint = Color.White) // Ícono de check blanco
            }
        }
    ) { padding -> // padding interno para evitar la top bar y FAB
        // Contenedor Box que ocupa todo el espacio disponible
        Box(modifier = Modifier.padding(padding)) {
            // Componente de Google Maps
            GoogleMap(
                modifier = Modifier.fillMaxSize(), // Ocupa toda la pantalla
                cameraPositionState = cameraPositionState, // Estado de la cámara para controlar zoom y posición
                onMapClick = { latLng -> // Callback cuando el usuario toca el mapa
                    markerPosition = latLng // Actualiza la posición del marcador a donde se tocó
                }
            ) {
                // Marcador que muestra la ubicación seleccionada
                Marker(
                    state = MarkerState(position = markerPosition), // Posición actual del marcador
                    title = "Ubicación seleccionada", // Título que aparece al tocar el marcador
                    draggable = true // Permite arrastrar el marcador a otra posición
                )
            }

            // Overlay (superposición) que muestra una pista/hint al usuario
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter) // Alineado en la parte superior centrada
                    .padding(16.dp), // Padding de 16dp alrededor
                color = Color.Black.copy(alpha = 0.6f), // Fondo negro con 60% de opacidad (semitransparente)
                shape = MaterialTheme.shapes.medium // Forma redondeada estándar de Material 3
            ) {
                // Texto de ayuda
                Text(
                    text = "Toca el mapa para mover el marcador", // Instrucción para el usuario
                    color = Color.White, // Texto blanco para contraste
                    modifier = Modifier.padding(8.dp), // Padding interno de 8dp
                    style = MaterialTheme.typography.bodySmall // Estilo de texto pequeño
                )
            }
        }
    }
}
