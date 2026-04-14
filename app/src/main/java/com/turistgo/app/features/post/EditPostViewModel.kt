package com.turistgo.app.features.post

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class PostEditState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val category: String = "General",
    val imageUrl: String = ""
)

class EditPostViewModel : ViewModel() {
    private val _uiState = mutableStateOf(PostEditState())
    val uiState: State<PostEditState> = _uiState

    fun loadPost(postId: String?) {
        // Simulación de carga de datos basada en el ID
        // En una app real esto vendría de un repositorio/API
        val simulatedPosts = mapOf(
            "0" to PostEditState("Avistamiento de aves", "Increíble experiencia viendo aves rapaces.", "Manizales, Caldas", "Turismo", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/nevadoruiz_rc301x.jpg"),
            "1" to PostEditState("Camping en el Ruiz", "Frío pero vale la pena la vista.", "Nevado del Ruiz", "Aventura", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/nevadoruiz_rc301x.jpg"),
            "2" to PostEditState("Café en Salento", "El mejor café de la región.", "Salento, Quindío", "Gastronomía", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/salento_i4sh8q.jpg")
        )

        simulatedPosts[postId]?.let {
            _uiState.value = it
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle)
    }

    fun updateDescription(newDescription: String) {
        _uiState.value = _uiState.value.copy(description = newDescription)
    }

    fun updateLocation(newLocation: String) {
        _uiState.value = _uiState.value.copy(location = newLocation)
    }

    fun updateCategory(newCategory: String) {
        _uiState.value = _uiState.value.copy(category = newCategory)
    }
}
