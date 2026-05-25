package com.turistgo.app.features.post

import android.location.Geocoder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    initialLat: Double? = null,
    initialLng: Double? = null,
    onLocationSelected: (Double, Double) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Ubicación por defecto (Medellín, Colombia)
    val defaultLocation = LatLng(6.2442, -75.5812)
    val initialLocation = remember(initialLat, initialLng) {
        if (initialLat != null && initialLng != null) {
            LatLng(initialLat, initialLng)
        } else {
            defaultLocation
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }

    // Estados para la búsqueda y reversa de geocodificación
    var searchQuery by remember { mutableStateOf("") }
    var addressText by remember { mutableStateOf("Cargando dirección...") }
    var isGeocoding by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }

    val cameraTarget = cameraPositionState.position.target

    // Geocodificación inversa cuando la cámara se detiene
    LaunchedEffect(cameraPositionState.isMoving, cameraTarget) {
        if (!cameraPositionState.isMoving) {
            isGeocoding = true
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val results = geocoder.getFromLocation(cameraTarget.latitude, cameraTarget.longitude, 1)
                    val address = results?.firstOrNull()?.getAddressLine(0)
                        ?: "Coordenadas: ${"%.5f".format(cameraTarget.latitude)}, ${"%.5f".format(cameraTarget.longitude)}"
                    withContext(Dispatchers.Main) {
                        addressText = address
                        isGeocoding = false
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        addressText = "Coordenadas: ${"%.5f".format(cameraTarget.latitude)}, ${"%.5f".format(cameraTarget.longitude)}"
                        isGeocoding = false
                    }
                }
            }
        }
    }

    // Función para buscar direcciones
    val performSearch = { query: String ->
        if (query.isNotBlank()) {
            keyboardController?.hide()
            isGeocoding = true
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val results = geocoder.getFromLocationName(query + ", Colombia", 1)
                    if (!results.isNullOrEmpty()) {
                        val loc = LatLng(results[0].latitude, results[0].longitude)
                        withContext(Dispatchers.Main) {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(loc, 16f),
                                1000
                            )
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val fallback = geocoder.getFromLocationName(query, 1)
                        if (!fallback.isNullOrEmpty()) {
                            val loc = LatLng(fallback[0].latitude, fallback[0].longitude)
                            withContext(Dispatchers.Main) {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(loc, 16f),
                                    1000
                                )
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Ignorar error
                } finally {
                    withContext(Dispatchers.Main) {
                        isGeocoding = false
                    }
                }
            }
        }
    }

    // Animación de flotación para el pin central
    val pinOffset by animateDpAsState(
        targetValue = if (cameraPositionState.isMoving) (-18).dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "PinOffsetAnimation"
    )

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
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false
            ),
            onMapLoaded = { isMapLoaded = true }
        )

        // ── PIN CENTRAL ESTÁTICO (Efecto Uber) ─────────────────────────────────
        if (isMapLoaded) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = pinOffset - 20.dp), // Ajuste por la base del pin
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin de ubicación",
                        tint = Color(0xFFC62828),
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(if (cameraPositionState.isMoving) 12.dp else 4.dp, CircleShape)
                    )
                    // Sombra flotante del pin en el suelo del mapa
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 12.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = if (cameraPositionState.isMoving) 0.15f else 0.35f))
                    )
                }
            }
        }

        // ── CAPA DE CARGA INICIAL ──────────────────────────────────────────────
        AnimatedVisibility(
            visible = !isMapLoaded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFEF7F3)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFFC62828), strokeWidth = 3.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Cargando mapa interactivo...",
                        color = Color(0xFF555555),
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // ── BARRA SUPERIOR CON BUSCADOR Y VOLVER ATRÁS ──────────────────────────
        if (isMapLoaded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color(0xFF333333)
                            )
                        }

                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar lugar o dirección...", color = Color.Gray, fontSize = 14.sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { performSearch(searchQuery) }),
                            modifier = Modifier.weight(1f)
                        )

                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = Color.Gray
                                )
                            }
                        }

                        IconButton(onClick = { performSearch(searchQuery) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color(0xFFC62828)
                            )
                        }
                    }
                }
            }
        }

        // ── BOTONES DE ACCIÓN FLOTANTES LATERALES ─────────────────────────────
        if (isMapLoaded) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Botón: Volver a Ciudad del Perfil
                if (initialLat != null && initialLng != null) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(initialLocation, 15f),
                                    1000
                                )
                            }
                        },
                        containerColor = Color.White,
                        contentColor = Color(0xFF1E88E5),
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = "Mi Ciudad",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Botón: Recetrar en el Pin
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(cameraTarget, 16f),
                                800
                            )
                        }
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFFC62828),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Recetrar",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ── TARJETA INFERIOR PREMIUM DETALLES Y CONFIRMACIÓN ─────────────────────
        if (isMapLoaded) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Indicador de arrastre sutil en la tarjeta
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF2F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ubicación seleccionada",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            if (isGeocoding) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .padding(top = 4.dp),
                                    color = Color(0xFFC62828),
                                    trackColor = Color(0xFFFEF2F2)
                                )
                            } else {
                                Text(
                                    text = addressText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF222222),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón confirmar con gradiente elegante
                    Button(
                        onClick = {
                            onLocationSelected(cameraTarget.latitude, cameraTarget.longitude)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFD32F2F), Color(0xFFC62828))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Confirmar ubicación",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
