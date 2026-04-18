package com.turistgo.app.features.post

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import android.net.Uri
import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.data.remote.MediaRepository
import com.turistgo.app.domain.model.PostStatus
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val mediaRepository: MediaRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {
    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _description = mutableStateOf("")
    val description: State<String> = _description

    private val _location = mutableStateOf("")
    val location: State<String> = _location

    private val _schedule = mutableStateOf("")
    val schedule: State<String> = _schedule

    private val _priceRange = mutableStateOf("")
    val priceRange: State<String> = _priceRange

    private val _selectedImageUri = mutableStateOf<Uri?>(null)
    val selectedImageUri: State<Uri?> = _selectedImageUri

    private val _isUploading = mutableStateOf(false)
    val isUploading: State<Boolean> = _isUploading

    val categories = listOf("Gastronomía", "Cultura", "Naturaleza", "Entretenimiento", "Historia")

    private val _selectedCategory = mutableStateOf("Naturaleza")
    val selectedCategory: State<String> = _selectedCategory

    private val _suggestedCategory = mutableStateOf<String?>(null)
    val suggestedCategory: State<String?> = _suggestedCategory

    private val _isAnalyzing = mutableStateOf(false)
    val isAnalyzing: State<Boolean> = _isAnalyzing

    fun onTitleChange(v: String) { 
        _title.value = v
        triggerAiSuggestion()
    }
    
    fun onDescriptionChange(v: String) { 
        _description.value = v
        triggerAiSuggestion()
    }
    
    fun onLocationChange(v: String) { _location.value = v }
    fun onScheduleChange(v: String) { _schedule.value = v }
    fun onPriceRangeChange(v: String) { _priceRange.value = v }
    fun onCategoryChange(v: String) { _selectedCategory.value = v }
    fun onImageSelected(uri: Uri?) { _selectedImageUri.value = uri }

    fun acceptAiSuggestion() {
        _suggestedCategory.value?.let {
            _selectedCategory.value = it
            _suggestedCategory.value = null
        }
    }

    private fun triggerAiSuggestion() {
        if (_title.value.length > 5 || _description.value.length > 10) {
            viewModelScope.launch {
                _isAnalyzing.value = true
                delay(1000) // Simulación de procesamiento de IA
                
                val combined = (_title.value + " " + _description.value).lowercase()
                _suggestedCategory.value = when {
                    combined.contains("comida") || combined.contains("restaurante") || combined.contains("café") -> "Gastronomía"
                    combined.contains("museo") || combined.contains("arte") || combined.contains("monumento") -> "Cultura"
                    combined.contains("parque") || combined.contains("sendero") || combined.contains("mirador") -> "Naturaleza"
                    combined.contains("bar") || combined.contains("discoteca") || combined.contains("fiesta") -> "Entretenimiento"
                    combined.contains("historia") || combined.contains("antiguo") || combined.contains("arquitectura") -> "Historia"
                    else -> null
                }
                _isAnalyzing.value = false
            }
        }
    }

    fun savePost(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                val session = sessionManager.userSession.firstOrNull()
                
                var finalImageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/destinos-naturales-en-colombia-sin-turismo-masivo_ei0akp.jpg"
                
                _selectedImageUri.value?.let { uri ->
                    finalImageUrl = mediaRepository.uploadImage(uri)
                }

                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    name = _title.value,
                    location = _location.value,
                    rating = "0.0",
                    imageUrl = finalImageUrl,
                    description = _description.value,
                    schedule = _schedule.value.ifEmpty { "No disponible" },
                    priceRange = _priceRange.value.ifEmpty { "No disponible" },
                    status = PostStatus.PENDING,
                    authorId = session?.userId ?: "unknown",
                    authorName = session?.name ?: "Usuario"
                )
                repository.savePost(newPost)
                _isUploading.value = false
                onSuccess()
            } catch (e: Exception) {
                _isUploading.value = false
                // Proximamente: Manejo de errores en UI
            }
        }
    }
}
