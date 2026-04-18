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
import kotlinx.coroutines.flow.first
import com.turistgo.app.BuildConfig

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val groqService: GroqService
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
                // 1. Obtener los lugares disponibles (posts)
                val availablePosts = repository.getPosts().first()
                
                // 2. Construir el prompt del sistema
                val placesContext = availablePosts.joinToString("\n") { 
                    "- ID: ${it.id}, Nombre: ${it.name}, Ubicación: ${it.location}, Descripción: ${it.description}"
                }
                
                val systemPrompt = """
                    Eres un asistente de viajes experto y apasionado para la app TuristGo. 
                    Tu objetivo es analizar los lugares disponibles y generar un itinerario detallado, natural y emocionante.
                    
                    ESTILO DE RESPUESTA:
                    - Usa muchos emojis relevantes para que la respuesta sea visual y divertida.
                    - Estructura la respuesta con un título como "🗓️ Itinerario sugerido día a día".
                    - Separa por días usando etiquetas claras como "Día 1", "Día 2", etc.
                    - Incluye horarios sugeridos para las actividades (ej: 08:00, 10:00, 14:00, 19:00).
                    - Sé muy amable, entusiasta y usa un tono de experto local.
                    
                    LUGARES DISPONIBLES EN LA APP (Usa estos nombres y datos):
                    ${if (availablePosts.isEmpty()) "No hay lugares registrados aún." else placesContext}
                    
                    INSTRUCCIONES CRÍTICAS:
                    1. Al FINAL de tu respuesta, después del itinerario, debes incluir OBLIGATORIAMENTE la sección: 
                       SUGGESTED_IDS: [id1, id2, ...] 
                       donde los IDs correspondan a los lugares de la lista anterior que mencionaste en el itinerario. 
                    2. Si el usuario pide algo que no está disponible, sugiérele los lugares de la lista como la mejor opción actual en TuristGo.
                """.trimIndent()

                // 3. Llamar a Groq API
                val request = GroqRequest(
                    messages = listOf(
                        GroqMessage(role = "system", content = systemPrompt),
                        GroqMessage(role = "user", content = userMessage)
                    )
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
