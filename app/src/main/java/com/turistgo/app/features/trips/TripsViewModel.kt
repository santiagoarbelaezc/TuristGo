package com.turistgo.app.features.trips

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.ChatMessage
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.PostStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.data.remote.GroqService
import com.turistgo.app.data.remote.model.GroqMessage
import com.turistgo.app.data.remote.model.GroqRequest
import com.turistgo.app.data.datastore.UserSessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import com.turistgo.app.BuildConfig
import com.turistgo.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val groqService: GroqService,
    private val sessionManager: UserSessionManager,
    private val budgetService: com.turistgo.app.data.AssistantBudgetService,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Mapa en memoria: messageId -> List<Post>
    // Las tarjetas NO se guardan en Firestore (problemas de serialización con enums anidados)
    // Se reconstruyen en sesión desde este mapa.
    private val destinationsMap = mutableMapOf<String, List<Post>>()

    init {
        viewModelScope.launch {
            chatRepository.getMessages().collectLatest { savedMessages ->
                // No sobreescribir mientras la IA está respondiendo (evita el parpadeo de tarjetas)
                if (_isLoading.value) return@collectLatest

                if (savedMessages.isEmpty()) {
                    val initialMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = "¿Qué viaje quieres planear?",
                        isFromUser = false
                    )
                    _messages.clear()
                    _messages.add(initialMessage)
                    chatRepository.saveMessages(listOf(initialMessage))
                } else {
                    // Reconstruir mensajes con destinos del mapa en memoria
                    val rebuilt = savedMessages.map { msg ->
                        msg.copy(
                            suggestedDestinations = destinationsMap[msg.id] ?: emptyList(),
                            isPlanResponse = destinationsMap.containsKey(msg.id)
                        )
                    }
                    _messages.clear()
                    _messages.addAll(rebuilt)
                }
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            isFromUser = true
        )

        _messages.add(userMessage)
        viewModelScope.launch {
            chatRepository.saveMessages(_messages.toList())
        }

        generateAiResponse(content)
    }

    fun onQuickPlanSelected(planType: String) {
        sendMessage(planType)
    }

    fun clearChat() {
        viewModelScope.launch {
            chatRepository.clearMessages()
            _messages.clear()
            val welcomeMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "¿Qué viaje quieres planear?",
                isFromUser = false
            )
            _messages.add(welcomeMessage)
            chatRepository.saveMessages(listOf(welcomeMessage))
        }
    }

    private fun generateAiResponse(userMessage: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // 1. Datos del usuario para personalización
                val session = sessionManager.userSession.firstOrNull()
                val userProfile = session?.userId?.let { repository.getUserById(it) }

                val userName = userProfile?.name ?: session?.name ?: "Viajero"
                val userCity = userProfile?.city ?: "No especificada"
                val userCountry = userProfile?.country ?: "Colombia"
                val userInterests = userProfile?.interests?.joinToString(", ") ?: "Viajes, aventura, cultura"

                val userContext = """
                    DATOS DEL USUARIO:
                    - Nombre: $userName
                    - Ciudad actual: $userCity, $userCountry
                    - Intereses: $userInterests
                """.trimIndent()

                // 2. Cargar SOLO posts APROBADOS de Firestore
                val allApprovedPosts = repository.getPosts(PostStatus.APPROVED).first()

                // 3. Lógica de presupuesto
                val extractedBudget = budgetService.extractBudget(userMessage)
                val isBudgetInsufficient = extractedBudget != null && extractedBudget < 50_000

                val isLowBudget = isBudgetInsufficient ||
                        budgetService.isLowBudget(userMessage) ||
                        _messages.takeLast(4).any { it.isFromUser && budgetService.isLowBudget(it.content) }

                // 4. Filtrar posts según presupuesto
                val filteredPosts: List<Post> = when {
                    isBudgetInsufficient -> budgetService.getClosestCheapestPlaces(
                        allApprovedPosts, userProfile?.city, userProfile?.department
                    )
                    isLowBudget -> budgetService.filterPlacesForLowBudget(
                        allApprovedPosts, userProfile?.city, userProfile?.department
                    ).ifEmpty { allApprovedPosts }
                    else -> allApprovedPosts
                }

                // 5. Construir catálogo de lugares para el prompt
                val catalogContext = if (filteredPosts.isEmpty()) {
                    "No hay destinos registrados aún en el catálogo."
                } else {
                    filteredPosts.take(12).joinToString("\n") { post ->
                        buildString {
                            append("• ID: ${post.id}")
                            append(" | Nombre: ${post.name}")
                            append(" | Tipo: ${post.categories.joinToString(", ").ifEmpty { "Turístico" }}")
                            append(" | Ubicación: ${post.location}")
                            if (!post.city.isNullOrBlank()) append(", ${post.city}")
                            if (!post.department.isNullOrBlank()) append(", ${post.department}")
                            if (post.latitude != null && post.longitude != null)
                                append(" | Coords: (${post.latitude}, ${post.longitude})")
                            append(" | Precio: ${post.priceRange.ifBlank { "No disponible" }}")
                            append(" | Horario: ${post.schedule.ifBlank { "No disponible" }}")
                            append(" | Descripción: ${post.description.take(120).ifBlank { "Sin descripción" }}")
                        }
                    }
                }

                // 6. System prompt principal
                var systemPrompt = """
Eres TuristGo AI, un asistente experto en turismo colombiano. Eres empático, creativo, cercano y usas tuteo siempre.

━━━━━━━━━━━━━━━━━━━━━━━━
👤 PERFIL DEL USUARIO
━━━━━━━━━━━━━━━━━━━━━━━━
$userContext

━━━━━━━━━━━━━━━━━━━━━━━━
📍 CATÁLOGO DE DESTINOS REGISTRADOS EN LA APP (solo APROBADOS)
━━━━━━━━━━━━━━━━━━━━━━━━
$catalogContext

━━━━━━━━━━━━━━━━━━━━━━━━
📌 INSTRUCCIONES CRÍTICAS
━━━━━━━━━━━━━━━━━━━━━━━━

1. **USA EL CATÁLOGO SIEMPRE**: Cuando diseñes itinerarios o recomendaciones, prioriza los lugares del catálogo anterior. No inventes destinos que no estén registrados, a menos que el catálogo esté vacío.

2. **IDs OBLIGATORIOS**: Cuando menciones uno o más destinos del catálogo, SIEMPRE incluye al final de tu respuesta:
   SUGGESTED_IDS: [id1, id2, id3]
   Esto permite que el usuario pueda navegar directamente al lugar desde la app.

3. **ITINERARIO CREATIVO**: Cuando el usuario pida un plan, sé muy creativo. Usa este formato EXACTO:
   🗓️ Itinerario – [nombre del viaje creativo]
   
   📅 Día 1 – [título temático del día]
   🕗 07:00 – Actividad con detalle emotivo
   🍽️ 12:00 – Almuerzo en [lugar del catálogo si existe]
   🌆 15:00 – Actividad vespertina con descripción
   🌙 19:00 – Actividad nocturna o cierre del día
   
   📅 Día 2 – [título temático]
   ...

4. **INFORMACIÓN FALTANTE**: Si el usuario no ha dado suficiente info (días, acompañantes, intereses), haz MÁXIMO 2 preguntas directas y concisas. Sin introducciones largas.
   Ejemplo: "¿Cuántos días tienes disponibles? ¿Viajas solo o en grupo?"

5. **MENSAJES ABSURDOS O SIN SENTIDO**: Si el usuario escribe algo sin coherencia, no relacionado con viajes, o claramente una prueba (ej: "asdfjkl", "sdfsdf", "quiero volar a marte"), responde de forma amigable y redirige la conversación:
   - Responde con humor suave o empatía.
   - Recuérdales que eres un asistente de viajes turísticos.
   - Invita a que cuenten su próximo destino soñado.
   Ejemplo: "¡Parece que tu mensaje se perdió en el camino viajero! 😄 Soy TuristGo AI y me especializo en ayudarte a planear aventuras increíbles por Colombia. ¿A dónde te gustaría escaparte pronto?"

6. **ESTILO**: Usa emojis con moderación (no en cada línea). Sé breve y directo. Responde en español siempre.

7. **NUNCA inventes IDs** que no estén en el catálogo de arriba.
                """.trimIndent()

                // 7. Regla adicional si presupuesto insuficiente
                if (isBudgetInsufficient) {
                    systemPrompt += """

━━━━━━━━━━━━━━━━━━━━━━━━
⚠️ ALERTA DE PRESUPUESTO BAJO (< 50.000 COP)
━━━━━━━━━━━━━━━━━━━━━━━━
El usuario indicó un presupuesto de ${extractedBudget} COP, que es menor al mínimo recomendado de 50.000 COP para un viaje completo.

- Comienza tu respuesta explicando esto de forma muy empática: "Para armar un plan completo se recomienda al menos 50.000 COP. Pero no te preocupes, te armo algo especial con lo que tienes 💪"
- Diseña un itinerario de UN SOLO DÍA.
- Usa únicamente destinos del catálogo gratuitos o de bajo costo, ordenados por cercanía al usuario.
- Incluye actividades en parques, caminatas, miradores o plazas públicas.
                    """.trimIndent()
                } else if (isLowBudget) {
                    systemPrompt += "\n\n" + budgetService.getLowBudgetContext(userProfile?.city)
                }

                // 8. Historial de conversación (últimos 12 mensajes)
                val conversationHistory = _messages.takeLast(12).map { msg ->
                    GroqMessage(
                        role = if (msg.isFromUser) "user" else "assistant",
                        content = msg.content
                    )
                }

                // 9. Llamada a Groq API
                val request = GroqRequest(
                    messages = listOf(GroqMessage(role = "system", content = systemPrompt)) + conversationHistory
                )

                val apiKey = "Bearer ${BuildConfig.GROQ_API_KEY}"
                val response = groqService.getChatCompletion(apiKey = apiKey, request = request)

                val aiContent = response.choices.firstOrNull()?.message?.content
                    ?: "Lo siento, no pude generar una respuesta en este momento."

                // 10. Limpiar la respuesta de tags internos
                val cleanContent = aiContent
                    .replace(Regex("SUGGESTED_IDS:\\s*\\[.*?\\]", RegexOption.DOT_MATCHES_ALL), "")
                    .trim()

                // 11. SIEMPRE mostrar tarjetas - sistema de 4 niveles de prioridad:
                val suggestedPosts = extractRelevantPosts(
                    aiContent = aiContent,
                    cleanContent = cleanContent,
                    userMessage = userMessage,
                    allPosts = allApprovedPosts
                )

                val aiMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = cleanContent,
                    isFromUser = false,
                    isPlanResponse = true, // SIEMPRE mostrar la sección de destinos
                    suggestedDestinations = suggestedPosts
                )
                // Guardar destinos en el mapa en memoria ANTES de actualizar _messages
                destinationsMap[aiMessage.id] = suggestedPosts

                _messages.add(aiMessage)
                chatRepository.saveMessages(_messages.toList())

            } catch (e: Exception) {
                _messages.add(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = "Ups, tuve un problema al conectarme. Revisa tu conexión e inténtalo de nuevo 🌐",
                        isFromUser = false
                    )
                )
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun extractIds(content: String): List<String> {
        val regex = Regex("SUGGESTED_IDS:\\s*\\[(.*?)\\]", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(content)
        return if (match != null) {
            match.groupValues[1]
                .split(",")
                .map { it.trim().removeSurrounding("\"").removeSurrounding("'") }
                .filter { it.isNotEmpty() }
        } else {
            emptyList()
        }
    }

    /**
     * Sistema de 4 niveles para SIEMPRE retornar tarjetas de destino relevantes:
     * 1. IDs exactos indicados por la IA en SUGGESTED_IDS
     * 2. Posts cuyos nombres aparecen en la respuesta de la IA
     * 3. Posts que coinciden con palabras clave del mensaje del usuario (ciudad, categoría)
     * 4. Top 4 posts más relevantes por defecto (si ningún match)
     */
    private fun extractRelevantPosts(
        aiContent: String,
        cleanContent: String,
        userMessage: String,
        allPosts: List<Post>
    ): List<Post> {
        val maxCards = 4

        // NIVEL 1: IDs exactos del tag SUGGESTED_IDS
        val suggestedIds = extractIds(aiContent)
        if (suggestedIds.isNotEmpty()) {
            val byId = allPosts.filter { it.id in suggestedIds }
            if (byId.isNotEmpty()) return byId.take(maxCards)
        }

        // NIVEL 2: Nombres de posts mencionados en la respuesta de la IA
        val responseNormalized = cleanContent.lowercase()
        val byName = allPosts.filter { post ->
            val words = post.name.lowercase().split(" ").filter { it.length > 3 }
            words.isNotEmpty() && words.count { word -> responseNormalized.contains(word) } >= 1
        }
        if (byName.isNotEmpty()) return byName.take(maxCards)

        // NIVEL 3: Palabras clave del mensaje del usuario vs location/categorías/descripción
        val userNormalized = userMessage.lowercase()
        val byKeyword = allPosts.filter { post ->
            val searchTarget = buildString {
                append(post.location.lowercase())
                append(" ")
                append(post.city?.lowercase() ?: "")
                append(" ")
                append(post.department?.lowercase() ?: "")
                append(" ")
                append(post.categories.joinToString(" ").lowercase())
                append(" ")
                append(post.description.lowercase().take(100))
            }
            val userWords = userNormalized.split(" ").filter { it.length > 3 }
            userWords.any { word -> searchTarget.contains(word) }
        }
        if (byKeyword.isNotEmpty()) return byKeyword.take(maxCards)

        // NIVEL 4: Fallback — top 4 posts (los primeros del catálogo aprobado)
        return allPosts.take(maxCards)
    }
}
