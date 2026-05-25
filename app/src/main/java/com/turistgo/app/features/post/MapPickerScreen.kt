package com.turistgo.app.features.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    initialLat: Double? = null,
    initialLng: Double? = null,
    onLocationSelected: (Double, Double) -> Unit,
    onNavigateBack: () -> Unit
) {
    val defaultLocation = LatLng(6.2442, -75.5812) // Medellín, Colombia
    val initialLocation = if (initialLat != null && initialLng != null) {
        LatLng(initialLat, initialLng)
    } else {
        defaultLocation
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 14f)
    }

    var markerPosition by remember { mutableStateOf(initialLocation) }
    var isMapLoaded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Formatear coordenadas legibles
    val latText = "%.5f".format(markerPosition.latitude)
    val lngText = "%.5f".format(markerPosition.longitude)

    Box(modifier = Modifier.fillMaxSize()) {

        // ── MAPA ─────────────────────────────────────────────────────────────
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = true
            ),
            onMapLoaded = { isMapLoaded = true },
            onMapClick = { latLng ->
                markerPosition = latLng
            }
        ) {
            Marker(
                state = MarkerState(position = markerPosition),
                title = "Ubicación seleccionada",
                draggable = true,
                onClick = { false }
            )
        }

        // ── LOADING OVERLAY ───────────────────────────────────────────────────
        AnimatedVisibility(
            visible = !isMapLoaded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFFC62828))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Cargando mapa...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // ── TOP BAR flotante ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón atrás
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color(0xFF1A1A1A)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Título y coords
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Seleccionar ubicación",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            "📍 $latText, $lngText",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón confirmar
                    Button(
                        onClick = {
                            onLocationSelected(
                                markerPosition.latitude,
                                markerPosition.longitude
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC62828)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Confirmar",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // ── HINT: Instrucción ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = isMapLoaded,
            enter = fadeIn(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 80.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Color.Black.copy(alpha = 0.65f),
                modifier = Modifier.padding(top = 180.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Toca el mapa para mover el marcador",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // ── FAB: Centrar en ubicación seleccionada ─────────────────────────
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(markerPosition, 16f)
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 32.dp),
            containerColor = Color.White,
            contentColor = Color(0xFFC62828),
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.MyLocation,
                contentDescription = "Centrar marcador",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
