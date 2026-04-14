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

@HiltViewModel
class TripsViewModel @Inject constructor() : ViewModel() {

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

        // Simular respuesta de IA
        generateAiResponse()
    }

    fun onQuickPlanSelected(planType: String) {
        sendMessage(planType)
    }

    private fun generateAiResponse() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000) // Simular pensamiento de la IA

            // Por ahora, siempre devolvemos el plan romántico como solicitó el usuario
            val romanticPlan = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "¡He preparado un viaje perfecto para ti y tu novia! Combiné naturaleza, gastronomía y cultura en una ruta de 3 días por los destinos más hermosos de Colombia.",
                isFromUser = false,
                isPlanResponse = true,
                suggestedDestinations = listOf(
                    Post(
                        id = "1",
                        name = "Piedra del Peñol",
                        location = "Guatapé, Antioquia",
                        rating = "4.8",
                        imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/celebre-la-semana-santa-en-estos-cuatro-lugares-turisticos-de-colombia-1229852_ckbgrw.jpg",
                        description = "Un lugar icónico con vista panorámica."
                    ),
                    Post(
                        id = "2",
                        name = "Parque Tayrona",
                        location = "Santa Marta, Colombia",
                        rating = "4.9",
                        imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771996096/visitar-parque-tayrona-13_bwybj6.webp",
                        description = "Playas de cristal y selva virgen."
                    )
                )
            )

            _messages.add(romanticPlan)
            _isLoading.value = false
        }
    }
}
