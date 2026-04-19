package com.turistgo.app.features.trips

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.ChatMessage
import com.turistgo.app.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val groqService: GroqService,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        // Mensaje de bienvenida inicial
        _messages.add(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "¿Qué viaje quieres planear?",
                isFromUser = false
            )
        )
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        // Agregar mensaje del usuario
        _messages.add(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                content = content,
                isFromUser = true
            )
        )

        // Llamar a Groq API con el mensaje del usuario
        generateAiResponse(content)
    }

    fun onQuickPlanSelected(planType: String) {
        sendMessage(planType)
    }

    private fun generateAiResponse(userMessage: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // 1. Obtener datos del usuario para personalización
                val session = sessionManager.userSession.firstOrNull()
                val userProfile = session?.userId?.let { repository.getUserById(it) }
                
                val userContext = """
                    DATOS DEL USUARIO:
                    - Nombre: ${userProfile?.name ?: session?.name ?: "Viajero"}
                    - Ubicación Actual: ${userProfile?.city ?: "No especificada"}, ${userProfile?.country ?: "No especificada"}
                    - Intereses: ${userProfile?.interests?.joinToString(", ") ?: "Viajes, aventura, relax"}
                """.trimIndent()

                // 2. Obtener los lugares disponibles (posts)
                val availablePosts = repository.getPosts().first()
                
                val placesContext = availablePosts.joinToString("\n") { 
                    "- ID: ${it.id}, Nombre: ${it.name}, Categorías: ${it.categories.joinToString(", ")}, Ubicación: ${it.location}, Descripción: ${it.description}"
                }
                
                val systemPrompt = """
                    Eres un asistente de viajes experto, local y consultivo para la aplicación TuristGo. 
                    Tu misión es guiar al usuario para crear el viaje perfecto, combinando sus intereses con los destinos disponibles.
                    
                    USUARIO ACTUAL:
                    $userContext
                    
                    ESTILO DE NARRACIÓN:
                    - ¡Escribe de forma natural, cercana y apasionada! Saluda al usuario por su nombre.
                    - REGLA DE TONO: Debes TUTEAR al usuario siempre. Usa "tú", "te", "tuyo", etc. NUNCA uses "usted". Somos amigos y expertos locales.
                    - Sé CONSULTIVO: Tu objetivo no es solo dar un plan, sino asegurarte de que sea el adecuado.
                    
                    COMPORTAMIENTO CONSULTIVO (REGLA DE ORO):
                    - Si la petición del usuario es vaga (ej: "Quiero viajar"), NO generes un plan final. En su lugar, salúdalo con entusiasmo y hazle preguntas clave como:
                      * ¿Cuánto tiempo tienes para este plan? (ej: 1 día, un fin de semana).
                      * ¿Prefieres algo cercano a tu ubicación actual (${userProfile?.city ?: "tu ciudad"}) o quieres ir más lejos?
                      * ¿Hay algún interés específico hoy?
                    - Si ya tienes información suficiente (duración, distancia, intereses), genera un itinerario inolvidable.
                    
                    ESTRUCTURA DEL ITINERARIO:
                    - Usa títulos vibrantes como "🌟 Tu Gran Aventura Personalizada".
                    - Planifica por días con horarios sugeridos y emojis creativos.
                    - Justifica cada parada basándote en los intereses del usuario (ej: "Como te gusta la cultura, he incluido...").
                    
                    CATÁLOGO DE LUGARES DISPONIBLES (Prioriza estos):
                    ${if (availablePosts.isEmpty()) "Sugerencias genéricas de Colombia." else placesContext}
                    
                    REGLAS CRÍTICAS:
                    1. NO menciones IDs técnicos en el texto narrativo.
                    2. Al FINAL absoluto, si mencionaste lugares del catálogo, añade: SUGGESTED_IDS: [id1, id2, ...]
                    3. Si vas a preguntar al usuario (flujo consultivo), NO incluyas el bloque SUGGESTED_IDS.
                """.trimIndent()

                // 3. Preparar historial (últimos 10 mensajes)
                val conversationHistory = _messages.takeLast(10).map { msg ->
                    GroqMessage(
                        role = if (msg.isFromUser) "user" else "assistant",
                        content = msg.content
                    )
                }

                // 4. Llamar a Groq API
                val request = GroqRequest(
                    messages = listOf(GroqMessage(role = "system", content = systemPrompt)) + conversationHistory
                )
                
                // API Key from .env (via BuildConfig) - Se agrega el prefijo Bearer programáticamente
                val apiKey = "Bearer ${com.turistgo.app.BuildConfig.GROQ_API_KEY}"
                
                val response = groqService.getChatCompletion(
                    apiKey = apiKey,
                    request = request
                )
                
                val aiContent = response.choices.firstOrNull()?.message?.content ?: "Lo siento, no pude generar una respuesta."
                
                // 4. Procesar IDs sugeridos
                val suggestedIds = extractIds(aiContent)
                val cleanContent = aiContent.replace(Regex("SUGGESTED_IDS: \\[.*?\\]"), "").trim()
                
                val suggestedPosts = availablePosts.filter { it.id in suggestedIds }
                
                // 5. Agregar mensaje a la lista
                _messages.add(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = cleanContent,
                        isFromUser = false,
                        isPlanResponse = suggestedPosts.isNotEmpty(),
                        suggestedDestinations = suggestedPosts
                    )
                )
                
            } catch (e: Exception) {
                _messages.add(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = "Lo siento, tuve un problema al conectar con mi cerebro viajero. Por favor, verifica tu conexión e inténtalo de nuevo.",
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
        val regex = Regex("SUGGESTED_IDS: \\[(.*?)\\]")
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
}
