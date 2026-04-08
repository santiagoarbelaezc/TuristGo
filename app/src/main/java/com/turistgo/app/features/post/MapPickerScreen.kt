package com.turistgo.app.features.post

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    onLocationSelected: (lat: Double, lng: Double) -> Unit,
    onBack: () -> Unit
) {
    // Simulated center coordinates (Colombia - Bogotá area)
    val baseLat = 4.7110
    val baseLng = -74.0721

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Calculate lat/lng from offset
    val currentLat = baseLat - (offsetY / 10000.0)
    val currentLng = baseLng + (offsetX / 10000.0)

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("Seleccionar Ubicación", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Simulated Map Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFECE6D9))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) {
                // Grid
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 60.dp.toPx()
                    for (i in 0..size.width.toInt() step step.toInt()) {
                        drawLine(
                            Color(0xFFD4CFC5),
                            Offset(i.toFloat(), 0f),
                            Offset(i.toFloat(), size.height),
                            strokeWidth = 0.5.dp.toPx()
                        )
                    }
                    for (j in 0..size.height.toInt() step step.toInt()) {
                        drawLine(
                            Color(0xFFD4CFC5),
                            Offset(0f, j.toFloat()),
                            Offset(size.width, j.toFloat()),
                            strokeWidth = 0.5.dp.toPx()
                        )
                    }
                    // Some "roads"
                    drawLine(Color(0xFFC8C0B0), Offset(0f, size.height * 0.4f), Offset(size.width, size.height * 0.4f), 6.dp.toPx())
                    drawLine(Color(0xFFC8C0B0), Offset(size.width * 0.3f, 0f), Offset(size.width * 0.3f, size.height), 6.dp.toPx())
                    drawLine(Color(0xFFC8C0B0), Offset(size.width * 0.65f, 0f), Offset(size.width * 0.65f, size.height), 4.dp.toPx())
                    // "Park" area
                    drawCircle(Color(0xFFA8D5A2).copy(alpha = 0.3f), radius = 80.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.6f))
                }
            }

            // Center Pin (fixed)
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                // Shadow dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                )
            }

            // Coordinates Display
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.GpsFixed, null, Modifier.size(16.dp), MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lat: ${"%.4f".format(currentLat)}  Lng: ${"%.4f".format(currentLng)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Recenter button
            FloatingActionButton(
                onClick = { offsetX = 0f; offsetY = 0f },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp),
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Recentrar")
            }
        }

        // Confirm button
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = { onLocationSelected(currentLat, currentLng) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Check, null, Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar Ubicación", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
