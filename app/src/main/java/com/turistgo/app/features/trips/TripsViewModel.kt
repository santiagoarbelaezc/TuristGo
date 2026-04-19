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

import com.turistgo.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val groqService: GroqService,
    private val sessionManager: UserSessionManager,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            chatRepository.getMessages().collectLatest { savedMessages ->
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
                    _messages.clear()
                    _messages.addAll(savedMessages)
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
                    Eres un asistente de viajes experto y local para TuristGo.
                    
                    USUARIO:
                    $userContext
                    
                    CATÁLOGO DE LUGARES:
                    ${if (availablePosts.isEmpty()) "Sugerencias genéricas." else placesContext}
                    
                    REGLAS DE RESPUESTA:
                    1. CONSULTA PUNTUAL: Si falta información (duración, acompañantes, etc.), responde ÚNICAMENTE con las preguntas de forma directa. Sin introducciones ni saludos largos.
                       Ejemplo: "¿Para cuántas personas es el viaje? ¿Cuántos días tienes disponibles?"
                    2. ITINERARIO (ESTRICTO): Si generas un plan, usa este formato:
                       🗓️ Itinerario día a día
                       
                       Día 1
                       [Emoji] HH:mm – Actividad
                       [Emoji] HH:mm – Actividad
                       
                       Día 2
                       ...
                    3. ESTILO: Tuteo siempre. Sé breve y usa emojis.
                    4. IDs: Al final añade SUGGESTED_IDS: [id1, id2, ...] solo si incluyes lugares del catálogo.
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
                val aiMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = cleanContent,
                    isFromUser = false,
                    isPlanResponse = suggestedPosts.isNotEmpty(),
                    suggestedDestinations = suggestedPosts
                )
                _messages.add(aiMessage)
                chatRepository.saveMessages(_messages.toList())
                
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
