// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.profile

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource // Para obtener strings desde resources
import com.turistgo.app.R
// Importaciones de componentes reutilizables del módulo de moderador
import com.turistgo.app.features.moderator.AnalyticsCard
import com.turistgo.app.features.moderator.ComparisonBar
import com.turistgo.app.features.moderator.LineChart
import com.turistgo.app.features.moderator.StatsGridCard

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de estadísticas del usuario
@Composable
fun UserStatsScreen(
    onBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val stats by viewModel.profileStats.collectAsState()
    val scrollState = rememberScrollState()
    val warmBg = Color(0xFFFBFAF5)

    // Scaffold proporciona la estructura base con top bar y contenido
    Scaffold(
        topBar = {
            // Barra superior centrada con título y botón de retroceso
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.stats_title), fontWeight = FontWeight.Bold) }, // "Estadísticas"
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
        // Columna principal que ocupa toda la pantalla y permite scroll vertical
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Aplica el padding del Scaffold
                .verticalScroll(scrollState) // Habilita scroll vertical
                .padding(24.dp) // Padding interno de 24dp en todos los lados
        ) {
            // Título principal de la sección
            Text(
                text = "Tu Impacto en TuristGo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A) // Color gris muy oscuro casi negro
            )
            // Subtítulo descriptivo
            Text(
                text = "Resumen de tus contribuciones y nivel",
                fontSize = 14.sp,
                color = Color(0xFF666666) // Color gris medio
            )

            Spacer(modifier = Modifier.height(24.dp)) // Espaciado

            // Fila con dos tarjetas de estadísticas específicas del usuario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio horizontal de 16dp entre tarjetas
            ) {
                StatsGridCard(
                    label = "Lugares Publicados",
                    value = stats.postsCount.toString(),
                    delta = "+0",
                    icon = Icons.Default.Place,
                    color = Color(0xFFE8EAF6),
                    modifier = Modifier.weight(1f)
                )
                StatsGridCard(
                    label = "Lugares Guardados",
                    value = stats.savedCount.toString(),
                    delta = "+0",
                    icon = Icons.Default.Star,
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Espaciado

            // Tarjeta de análisis: Evolución de Puntos (gráfico de líneas)
            AnalyticsCard(title = "Evolución de Puntos") {
                // Gráfico de líneas mostrando la progresión de puntos a lo largo del tiempo
                LineChart(
                    data = listOf(0.1f, 0.2f, 0.3f, 0.5f, 0.7f, 0.8f, stats.levelProgress.coerceAtLeast(0.1f)),
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Espaciado

            // Tarjeta de análisis: Progreso de Nivel
            AnalyticsCard(title = "Progreso de Nivel") {
                // Columna con espaciado vertical de 12dp entre elementos
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Barra de comparación que muestra el progreso hacia el siguiente nivel
                    ComparisonBar(
                        label = "${stats.levelName} (Nivel ${stats.levelNumber})",
                        percentage = stats.levelProgress,
                        color = Color(0xFF5C6BC0),
                        count = "${stats.points} / ${stats.nextLevelPoints} pts"
                    )
                    // Texto informativo sobre puntos necesarios para el siguiente nivel
                    Text(
                        stringResource(R.string.points_to_next), // "Puntos para el siguiente nivel"
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp) // Pequeño padding superior
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Espaciado final
        }
    }
}
