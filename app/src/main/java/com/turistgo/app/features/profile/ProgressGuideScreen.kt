// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.profile

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource // Para obtener strings desde resources
import com.turistgo.app.R

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de guía de progreso
@Composable
fun ProgressGuideScreen(onBack: () -> Unit) {
    // Color de fondo cálido para toda la pantalla
    val warmBg = Color(0xFFFBFAF5)
    // Estado para la pestaña seleccionada (0: Niveles, 1: Reputación, 2: Insignias)
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Scaffold proporciona la estructura base con top bar y contenido
    Scaffold(
        topBar = {
            // Barra superior centrada con título y botón de retroceso
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.progress_guide_title), fontWeight = FontWeight.Bold) }, // "Guía de Progreso"
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
        // Columna principal que ocupa todo el espacio disponible
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Barra de pestañas (tabs) para navegar entre las tres secciones
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = warmBg, // Fondo del TabRow
                contentColor = MaterialTheme.colorScheme.primary, // Color del contenido de las pestañas
                indicator = { tabPositions -> // Indicador visual de la pestaña activa
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]), // Posiciona el indicador sobre la pestaña seleccionada
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                // Pestaña 0: Niveles
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.tab_levels), fontWeight = FontWeight.Bold) } // "Niveles"
                )
                // Pestaña 1: Reputación
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.tab_reputation), fontWeight = FontWeight.Bold) } // "Reputación"
                )
                // Pestaña 2: Insignias
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(stringResource(R.string.tab_badges), fontWeight = FontWeight.Bold) } // "Insignias"
                )
            }

            // Contenedor que ocupa el resto del espacio y muestra el contenido según la pestaña seleccionada
            Box(modifier = Modifier.weight(1f).padding(24.dp)) {
                when (selectedTab) {
                    0 -> LevelsGuide()      // Muestra la guía de niveles
                    1 -> ReputationGuide()  // Muestra la guía de reputación
                    2 -> BadgesGuide()      // Muestra la guía de insignias
                }
            }
        }
    }
}

// Componente que muestra la guía de niveles (Explorador, Viajero, Guía, Leyenda)
@Composable
fun LevelsGuide() {
    // Columna con scroll vertical para poder ver todo el contenido
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // Título de la sección
        Text(stringResource(R.string.level_guide_title), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        // Descripción introductoria
        Text(stringResource(R.string.level_guide_desc), fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp)) // Espaciado
        
        // Cada nivel se muestra con su ícono, requisitos y descripción
        LevelStep(
            level = stringResource(R.string.level_0_name),      // "Explorador"
            requirement = stringResource(R.string.level_0_req), // "0 puntos"
            description = stringResource(R.string.level_0_desc),// "¡Bienvenido! Comienza tu aventura."
            icon = Icons.Default.PersonOutline,
            color = Color.Gray
        )
        LevelStep(
            level = stringResource(R.string.level_1_name),      // "Viajero"
            requirement = stringResource(R.string.level_1_req), // "100 puntos"
            description = stringResource(R.string.level_1_desc),// "Comparte tu primer destino."
            icon = Icons.Default.Explore,
            color = Color(0xFF4CAF50) // Verde
        )
        LevelStep(
            level = stringResource(R.string.level_2_name),      // "Guía"
            requirement = stringResource(R.string.level_2_req), // "300 puntos"
            description = stringResource(R.string.level_2_desc),// "Inspira a otros viajeros."
            icon = Icons.Default.AirplanemodeActive,
            color = Color(0xFF2196F3) // Azul
        )
        LevelStep(
            level = stringResource(R.string.level_3_name),      // "Leyenda"
            requirement = stringResource(R.string.level_3_req), // "500 puntos"
            description = stringResource(R.string.level_3_desc),// "Eres un referente en la comunidad."
            icon = Icons.Default.MilitaryTech,
            color = Color(0xFFFF9800) // Naranja
        )
    }
}

// Componente que muestra la guía de reputación (cómo ganar puntos)
@Composable
fun ReputationGuide() {
    // Columna con scroll vertical
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // Título de la sección
        Text("Sistema de Reputación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        // Descripción introductoria
        Text("Gana puntos por cada acción positiva que realices.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tarjetas que muestran cada acción y los puntos que otorga
        ReputationCard(
            action = stringResource(R.string.action_publish_dest), // "Publicar destino"
            points = "+100 pts",
            icon = Icons.Default.AddPhotoAlternate
        )
        ReputationCard(
            action = stringResource(R.string.action_save_place), // "Guardar lugar"
            points = "+10 pts",
            icon = Icons.Default.Bookmark
        )
        ReputationCard(
            action = stringResource(R.string.action_give_like), // "Dar me gusta"
            points = "+10 pts",
            icon = Icons.Default.Favorite
        )
        ReputationCard(
            action = "Recibir verificación", // Acción futura (próximamente)
            points = "+50 pts",
            icon = Icons.Default.CheckCircle,
            isComingSoon = true // Marca que esta función aún no está disponible
        )
    }
}

// Componente que muestra la guía de insignias (logros especiales)
@Composable
fun BadgesGuide() {
    // Columna con scroll vertical
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // Título de la sección
        Text("Manual de Insignias", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        // Descripción introductoria
        Text("Logros especiales por hitos específicos.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Fila para cada insignia con su nombre y requisito
        BadgeRowGuide(
            name = stringResource(R.string.badge_first_step),      // "Primer paso"
            requirement = stringResource(R.string.badge_first_step_req), // "Publica tu primer destino"
            icon = Icons.Default.Public
        )
        BadgeRowGuide(
            name = stringResource(R.string.badge_curator),         // "Curador"
            requirement = stringResource(R.string.badge_curator_req),    // "Guarda 5 lugares"
            icon = Icons.Default.Bookmark
        )
        BadgeRowGuide(
            name = stringResource(R.string.badge_enthusiast),      // "Entusiasta"
            requirement = stringResource(R.string.badge_enthusiast_req), // "Da 10 me gusta"
            icon = Icons.Default.Favorite
        )
    }
}

// Componente que muestra un paso de nivel individual (ícono circular + información)
@Composable
fun LevelStep(level: String, requirement: String, description: String, icon: ImageVector, color: Color) {
    // Fila con ícono a la izquierda y texto a la derecha
    Row(modifier = Modifier.padding(bottom = 24.dp)) {
        // Superficie circular que contiene el ícono
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.1f) // Color del nivel con 10% de opacidad
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(12.dp))
        }
        Spacer(modifier = Modifier.width(16.dp)) // Espacio entre ícono y texto
        // Columna con la información del nivel
        Column {
            Text(level, fontWeight = FontWeight.Bold, fontSize = 16.sp)           // Nombre del nivel
            Text(requirement, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold) // Requisito
            Text(description, fontSize = 13.sp, color = Color.DarkGray)          // Descripción
        }
    }
}

// Componente que muestra una tarjeta de acción de reputación
@Composable
fun ReputationCard(action: String, points: String, icon: ImageVector, isComingSoon: Boolean = false) {
    // Tarjeta blanca con bordes redondeados
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        // Fila con ícono, texto y puntos
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de la acción
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            // Columna con el nombre de la acción y etiqueta "Próximamente" si aplica
            Column(modifier = Modifier.weight(1f)) {
                Text(action, fontWeight = FontWeight.Bold)
                if (isComingSoon) Text(stringResource(R.string.coming_soon), fontSize = 10.sp, color = Color.Gray)
            }
            // Puntos que otorga la acción
            Text(points, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

// Componente que muestra una fila de insignia en la guía
@Composable
fun BadgeRowGuide(name: String, requirement: String, icon: ImageVector) {
    // Fila con ícono circular a la izquierda y texto a la derecha
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Superficie circular con el ícono de la insignia
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) // Color primario con 40% opacidad
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        // Columna con nombre y requisito de la insignia
        Column {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(requirement, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
