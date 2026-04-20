// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.profile

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Flecha hacia atrás con soporte para RTL (Right-to-Left)
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de estadísticas
@Composable
fun StatsScreen(onBack: () -> Unit) {
    // Scaffold proporciona la estructura base con top bar y contenido
    Scaffold(
        topBar = {
            // Barra superior con título y botón de retroceso
            TopAppBar(
                title = { Text("Mis Estadísticas", fontWeight = FontWeight.Bold) }, // Título de la pantalla
                navigationIcon = {
                    IconButton(onClick = onBack) { // Botón de flecha hacia atrás
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding -> // padding interno para evitar la top bar
        // LazyColumn para scroll vertical del contenido
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Aplica el padding del Scaffold
                .background(MaterialTheme.colorScheme.background), // Fondo del tema actual
            contentPadding = PaddingValues(20.dp), // Padding interno de 20dp en todos los lados
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio vertical de 16dp entre items
        ) {
            // Item: Título de la sección "Resumen de Actividad"
            item {
                Text(
                    text = "Resumen de Actividad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Item: Fila con dos tarjetas métricas (Activas y Pendientes)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio horizontal de 12dp entre tarjetas
                ) {
                    MetricCard(
                        title = "Activas",
                        value = "5", // Número de publicaciones activas (ejemplo estático)
                        color = MaterialTheme.colorScheme.primaryContainer, // Color del contenedor primario del tema
                        icon = Icons.Default.Public, // Ícono de mundo/global
                        modifier = Modifier.weight(1f) // Ocupa la mitad del ancho disponible
                    )
                    MetricCard(
                        title = "Pendientes",
                        value = "2", // Número de publicaciones pendientes de revisión (ejemplo estático)
                        color = Color(0xFFFFF3E0), // Color amarillo muy claro
                        icon = Icons.Default.History, // Ícono de reloj/historial
                        modifier = Modifier.weight(1f) // Ocupa la otra mitad
                    )
                }
            }

            // Item: Tarjeta de publicaciones verificadas con éxito (ocupa todo el ancho)
            item {
                MetricCard(
                    title = "Verificadas con éxito",
                    value = "8", // Número de publicaciones verificadas (ejemplo estático)
                    color = Color(0xFFE8F5E9), // Color verde muy claro
                    icon = Icons.Default.Verified, // Ícono de verificación (check con escudo)
                    modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
                )
            }

            // Item: Espaciador y título de la sección "Impacto en la Comunidad"
            item {
                Spacer(modifier = Modifier.height(8.dp)) // Espacio vertical
                Text(
                    text = "Impacto en la Comunidad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Item: Lista de métricas de impacto (votos, comentarios, guardados)
            item {
                ImpactList() // Componente que muestra la lista de impacto
            }
            
            // Item: Tarjeta con "Dato curioso" (información motivacional)
            item {
                Spacer(modifier = Modifier.height(24.dp)) // Espacio antes de la tarjeta
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) // Fondo semitransparente
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Fila con ícono de información y título
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dato curioso", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Texto motivacional con datos de ejemplo
                        Text(
                            "Tus publicaciones han ayudado a más de 150 turistas a descubrir joyas ocultas este mes.",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// Componente que muestra una tarjeta métrica (título, valor, ícono y color de fondo)
@Composable
fun MetricCard(title: String, value: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, // Modificador personalizable (ancho, etc.)
        shape = RoundedCornerShape(16.dp), // Bordes redondeados de 16dp
        colors = CardDefaults.cardColors(containerColor = color) // Color de fondo personalizado
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Padding interno de 16dp
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) // Ícono con color secundario
            Spacer(modifier = Modifier.height(12.dp)) // Espacio entre ícono y valor
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) // Valor grande y negrita
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) // Título pequeño
        }
    }
}

// Componente que muestra la lista de métricas de impacto (votos, comentarios, guardados)
@Composable
fun ImpactList() {
    // Columna con espaciado vertical de 12dp entre cada elemento
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ImpactItem("Votos recibidos", "42", Icons.Default.ThumbUp) // Votos positivos recibidos
        ImpactItem("Comentarios en tus posts", "12", Icons.Default.Chat) // Cantidad de comentarios
        ImpactItem("Veces guardado", "18", Icons.Default.Bookmark) // Veces que otros guardaron sus publicaciones
    }
}

// Componente que muestra un item individual de impacto (ícono circular, etiqueta y valor)
@Composable
fun ImpactItem(label: String, value: String, icon: ImageVector) {
    // Fila que ocupa todo el ancho disponible
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Superficie circular que contiene el ícono
        Surface(
            modifier = Modifier.size(40.dp), // Tamaño fijo de 40dp
            shape = CircleShape, // Forma circular
            color = MaterialTheme.colorScheme.surfaceVariant // Color de superficie variante
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary) // Ícono con padding interno
        }
        Spacer(modifier = Modifier.width(16.dp)) // Espacio entre ícono y texto
        Text(text = label, modifier = Modifier.weight(1f), fontSize = 14.sp) // Etiqueta que ocupa espacio restante
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp) // Valor en negrita
    }
}
