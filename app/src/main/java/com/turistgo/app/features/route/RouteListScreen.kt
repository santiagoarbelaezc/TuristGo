// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.route

// Importaciones de animaciones y UI
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Modelos de datos del mockup ───────────────────────────────────────────

// Modelo que representa un mensaje en el chat
data class ChatMessage(
    val id: Int, // Identificador único del mensaje
    val content: String, // Contenido del mensaje (texto)
    val isUser: Boolean, // Indica si el mensaje es del usuario (true) o de la IA (false)
    val aiResponse: AiTravelPlan? = null // Plan de viaje generado por IA (solo para mensajes de IA)
)

// Modelo que representa un plan de viaje generado por IA
data class AiTravelPlan(
    val title: String, // Título del plan (ej: "Plan romántico para dos 💑")
    val summary: String, // Resumen del plan
    val days: List<DayPlan>, // Lista de días del itinerario
    val places: List<RecommendedPlace> // Lista de lugares recomendados
)

// Modelo que representa un día del itinerario
data class DayPlan(
    val day: Int, // Número del día (1, 2, 3...)
    val activities: List<String> // Lista de actividades para ese día
)

// Modelo que representa un lugar recomendado
data class RecommendedPlace(
    val name: String, // Nombre del lugar
    val type: String, // Tipo de lugar (ej: "Atractivo Natural")
    val rating: Float, // Calificación (0-5)
    val imageUrl: String, // URL de la imagen
    val description: String // Descripción breve
)

// ─── Datos de ejemplo del plan ──────────────────────────────────────────────

// Plan de viaje de ejemplo (mockup) - En producción vendría de la API
private val mockPlan = AiTravelPlan(
    title = "Plan romántico para dos 💑",
    summary = "¡He preparado un viaje perfecto para ti y tu novia! Combiné naturaleza, gastronomía y cultura en una ruta de 3 días por los destinos más hermosos de Colombia.",
    days = listOf(
        DayPlan(
            day = 1,
            activities = listOf(
                "🌅 08:00 – Llegada a Guatapé, desayuno en el malecón",
                "🪨 10:00 – Subida a la Piedra del Peñol (740 escalones, ¡vale la pena!)",
                "📸 12:00 – Fotos panorámicas del embalse",
                "🚤 14:00 – Paseo en lancha por las islas del embalse",
                "🍷 19:00 – Cena romántica en restaurante con vista al agua"
            )
        ),
        DayPlan(
            day = 2,
            activities = listOf(
                "🌿 09:00 – Traslado a Jardín, Antioquia",
                "☕ 10:30 – Café de especialidad en el Parque Principal",
                "🚡 12:00 – Teleférico hasta el Cerro del Salvador",
                "🦋 15:00 – Visita a la cueva de Las Pintadas",
                "🎶 20:00 – Noche de música en vivo en la plaza"
            )
        ),
        DayPlan(
            day = 3,
            activities = listOf(
                "🌄 08:00 – Amanecer en el Puente de Occidente (siglo XIX)",
                "🛶 10:00 – Tour en chiva por el pueblo de Santa Fe de Antioquia",
                "🏛️ 13:00 – Recorrido por la arquitectura colonial",
                "🌮 15:00 – Almuerzo tradicional: sancocho antioqueño",
                "✈️ 17:00 – Regreso con recuerdos para toda la vida ❤️"
            )
        )
    ),
    places = listOf(
        RecommendedPlace(
            name = "Piedra del Peñol",
            type = "Atractivo Natural",
            rating = 4.9f,
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/pe%C3%B1ol_jlujxo.jpg",
            description = "Monolito de 200 m de altura con vistas épicas del embalse."
        ),
        RecommendedPlace(
            name = "Salento, Quindío",
            type = "Pueblo Patrimonio",
            rating = 4.8f,
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/salento_i4sh8q.jpg",
            description = "Pueblo cafetero de colores y naturaleza exuberante."
        ),
        RecommendedPlace(
            name = "Cartagena de Indias",
            type = "Ciudad Colonial",
            rating = 4.7f,
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/indias_ym97lb.jpg",
            description = "La antigua capital colonial llena de historia y cultura."
        )
    )
)

// ─── Pantalla principal ─────────────────────────────────────────────────────

// Declara la función composable principal de la pantalla de planificación de rutas con IA
@Composable
fun RouteListScreen() {
    // URL del logo de TuristGo
    val logoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"

    // Estados locales de la UI
    var promptText by remember { mutableStateOf("") } // Texto ingresado por el usuario
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) } // Lista de mensajes del chat
    var isTyping by remember { mutableStateOf(false) } // Indica si la IA está "escribiendo"
    val listState = rememberLazyListState() // Estado para controlar scroll de la LazyColumn
    val scope = rememberCoroutineScope() // CoroutineScope para operaciones asíncronas
    val keyboardController = LocalSoftwareKeyboardController.current // Para ocultar el teclado

    // Función para enviar un mensaje
    fun sendMessage() {
        val text = promptText.trim()
        if (text.isEmpty()) return // No enviar mensajes vacíos
        promptText = "" // Limpia el campo de texto
        keyboardController?.hide() // Oculta el teclado

        // Agrega el mensaje del usuario a la lista
        val userMsg = ChatMessage(id = messages.size, content = text, isUser = true)
        messages = messages + userMsg
        isTyping = true // Muestra el indicador de "escribiendo..."

        // Hace scroll automático hasta el último mensaje
        scope.launch {
            delay(200)
            listState.animateScrollToItem(messages.size - 1)
        }

        // Simula la respuesta de la IA (delay de 2.2 segundos)
        scope.launch {
            delay(2200) // Simula que la IA "piensa"
            isTyping = false
            // Agrega la respuesta de la IA con el plan de viaje
            val aiMsg = ChatMessage(
                id = messages.size + 1,
                content = mockPlan.summary,
                isUser = false,
                aiResponse = mockPlan
            )
            messages = messages + aiMsg
            delay(150)
            listState.animateScrollToItem(messages.size - 1) // Scroll al nuevo mensaje
        }
    }

    // Columna principal que ocupa toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fondo del tema
    ) {
        // ── Header (cabecera con logo y título) ───────────────────────────────────
        Surface(
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 4.dp // Sombra para efecto de elevación
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Logo de la aplicación
                AsyncImage(
                    model = logoUrl,
                    contentDescription = "TuristGo Logo",
                    modifier = Modifier.size(42.dp),
                    contentScale = ContentScale.Fit
                )
                Column {
                    Text(
                        text = "TuristGo AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Tu asistente de viajes inteligente",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // Empuja el ícono a la derecha
                // Ícono de IA
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp).size(18.dp)
                    )
                }
            }
        }

        // ── Área del chat (LazyColumn con mensajes) ───────────────────────────────
        LazyColumn(
            state = listState, // Controla el scroll
            modifier = Modifier
                .weight(1f) // Ocupa el espacio restante
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre mensajes
        ) {
            // Mensaje de bienvenida inicial (solo si no hay mensajes)
            if (messages.isEmpty()) {
                item {
                    WelcomeBanner(logoUrl = logoUrl)
                }
            }

            // Itera sobre los mensajes del chat
            items(messages, key = { it.id }) { message -> // key = id para optimización
                // Animación de entrada para cada mensaje
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(300)
                    )
                ) {
                    if (message.isUser) {
                        UserBubble(text = message.content) // Burbuja del usuario
                    } else {
                        AiBubble(message = message) // Burbuja de la IA
                    }
                }
            }

            // Indicador "escribiendo..." (animación de tres puntos)
            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }
        }

        // ── Barra de entrada de texto ────────────────────────────────────────────
        Surface(
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp // Sombra más pronunciada
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Campo de texto para escribir el mensaje
                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    placeholder = { Text("¿Qué viaje quieres planear?", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp), // Bordes redondeados (forma de píldora)
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send), // Botón "Enviar" en teclado
                    keyboardActions = KeyboardActions(onSend = { sendMessage() }), // Acción al presionar Enviar
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                // Botón flotante de envío
                FloatingActionButton(
                    onClick = { sendMessage() },
                    modifier = Modifier.size(52.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// ─── Banner de bienvenida ────────────────────────────────────────────────────

// Componente que muestra el banner de bienvenida al iniciar el chat
@Composable
private fun WelcomeBanner(logoUrl: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Logo
        AsyncImage(
            model = logoUrl,
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Fit
        )
        // Mensaje de bienvenida
        Text(
            text = "¡Hola! Soy tu asistente de viajes 🌍",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Cuéntame qué tipo de viaje sueñas y lo planeamos juntos.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Sugerencias rápidas (chips)
        val suggestions = listOf(
            "Plan con mi novia 💑",
            "Aventura en familia 🏕️",
            "Viaje solo de descanso 🧘"
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(suggestions) { suggestion ->
                SuggestionChip(
                    onClick = { }, // Por ahora sin acción (podría llenar el prompt)
                    label = { Text(suggestion, fontSize = 13.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}

// ─── Burbuja del usuario ─────────────────────────────────────────────────────

// Componente que muestra un mensaje del usuario (alineado a la derecha)
@Composable
private fun UserBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End // Alinea a la derecha
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp), // Esquina superior izquierda recta
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.widthIn(max = 280.dp) // Ancho máximo
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

// ─── Burbuja de la IA ────────────────────────────────────────────────────────

// Componente que muestra un mensaje de la IA (alineado a la izquierda)
@Composable
private fun AiBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cabecera con ícono y nombre de la IA
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(6.dp).size(14.dp)
                )
            }
            Text("TuristGo AI", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        }

        // Texto resumen del mensaje
        Surface(
            shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp), // Esquina superior derecha recta
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message.content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Si hay un plan de viaje asociado, lo muestra
        message.aiResponse?.let { plan ->
            AiPlanCard(plan = plan)
        }
    }
}

// ─── Card con el plan de viaje ───────────────────────────────────────────────

// Componente que muestra la tarjeta completa del plan de viaje
@Composable
private fun AiPlanCard(plan: AiTravelPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Título del plan
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.TravelExplore, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text(plan.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) // Línea divisoria

            // Sección: Lugares recomendados
            Text("📍 Lugares recomendados", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(plan.places) { place ->
                    PlaceRecommendationCard(place = place)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Sección: Itinerario día a día
            Text("🗓️ Itinerario día a día", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            plan.days.forEach { day ->
                DayPlanSection(dayPlan = day)
            }
        }
    }
}

// ─── Card de lugar recomendado ───────────────────────────────────────────────

// Componente que muestra una tarjeta de lugar recomendado
@Composable
private fun PlaceRecommendationCard(place: RecommendedPlace) {
    Card(
        modifier = Modifier.width(180.dp), // Ancho fijo para scroll horizontal
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Imagen del lugar
            AsyncImage(
                model = place.imageUrl,
                contentDescription = place.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            // Información del lugar
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(place.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                Text(place.type, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                // Rating con estrellas
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(13.dp))
                    Text(place.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
                Text(place.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, lineHeight = 16.sp)
            }
        }
    }
}

// ─── Sección de día del itinerario ──────────────────────────────────────────

// Componente que muestra un día del itinerario
@Composable
private fun DayPlanSection(dayPlan: DayPlan) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Badge del día
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = "Día ${dayPlan.day}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        // Lista de actividades del día
        dayPlan.activities.forEach { activity ->
            Text(
                text = activity,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// ─── Indicador de "escribiendo…" ────────────────────────────────────────────

// Componente que muestra una animación de tres puntos saltando (IA escribiendo)
@Composable
private fun TypingIndicator() {
    // Animación infinita para los tres puntos
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Ícono de IA
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(6.dp).size(14.dp)
            )
        }
        // Burbuja con los tres puntos animados
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    // Animación de cada punto (sube y baja)
                    val offsetY by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = -6f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(index * 130) // Desfase entre puntos
                        ),
                        label = "dot$index"
                    )
                    Surface(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(y = offsetY.dp), // Movimiento vertical
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    ) {}
                }
            }
        }
    }
}
