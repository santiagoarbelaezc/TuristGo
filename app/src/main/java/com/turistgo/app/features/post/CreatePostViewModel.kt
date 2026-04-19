package com.turistgo.app.features.post
 
import com.turistgo.app.core.utils.ColombiaGeography

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
 
    private val _department = mutableStateOf("")
    val department: State<String> = _department
 
    private val _city = mutableStateOf("")
    val city: State<String> = _city
 
    private val _availableDepartments = mutableStateOf(ColombiaGeography.getDepartments())
    val availableDepartments: State<List<String>> = _availableDepartments
 
    private val _availableCities = mutableStateOf<List<String>>(emptyList())
    val availableCities: State<List<String>> = _availableCities

    private val _schedule = mutableStateOf("")
    val schedule: State<String> = _schedule

    private val _priceRange = mutableStateOf("")
    val priceRange: State<String> = _priceRange

    private val _selectedImageUri = mutableStateOf<Uri?>(null)
    val selectedImageUri: State<Uri?> = _selectedImageUri

    private val _isUploading = mutableStateOf(false)
    val isUploading: State<Boolean> = _isUploading

    val categories = listOf("Turismo", "Evento", "Concierto", "Gastronomía", "Cultura", "Naturaleza", "Historia", "Otros")

    private val _selectedCategories = mutableStateOf<Set<String>>(emptySet())
    val selectedCategories: State<Set<String>> = _selectedCategories

    private val _latitude = mutableStateOf<Double?>(null)
    val latitude: State<Double?> = _latitude

    private val _longitude = mutableStateOf<Double?>(null)
    val longitude: State<Double?> = _longitude

    private val _startTime = mutableStateOf("")
    val startTime: State<String> = _startTime

    private val _endTime = mutableStateOf("")
    val endTime: State<String> = _endTime

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
    
    fun onDepartmentChange(v: String) {
        _department.value = v
        _city.value = ""
        _availableCities.value = ColombiaGeography.getCities(v)
    }
    
    fun onCityChange(v: String) { _city.value = v }
 
    fun onStartTimeChange(v: String) { _startTime.value = v }
    fun onEndTimeChange(v: String) { _endTime.value = v }
    fun onPriceRangeChange(v: String) { _priceRange.value = v }
    fun onCategoryToggle(category: String) {
        val current = _selectedCategories.value
        _selectedCategories.value = if (current.contains(category)) {
            current - category
        } else {
            current + category
        }
    }
    fun onImageSelected(uri: Uri?) { _selectedImageUri.value = uri }
    fun onCoordinatesSelected(lat: Double, lng: Double) {
        _latitude.value = lat
        _longitude.value = lng
    }

    fun acceptAiSuggestion() {
        _suggestedCategory.value?.let {
            if (!_selectedCategories.value.contains(it)) {
                _selectedCategories.value = _selectedCategories.value + it
            }
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
                // Simulación de optimización de imagen (compresión/redimensión)
                if (_selectedImageUri.value != null) {
                    delay(800) // Simular tiempo de procesamiento
                }

                val session = sessionManager.userSession.firstOrNull()
                
                var finalImageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/destinos-naturales-en-colombia-sin-turismo-masivo_ei0akp.jpg"
                
                _selectedImageUri.value?.let { uri ->
                    finalImageUrl = mediaRepository.uploadImage(uri)
                }

                val finalSchedule = if (_startTime.value.isNotEmpty() && _endTime.value.isNotEmpty()) {
                    "${_startTime.value} - ${_endTime.value}"
                } else if (_startTime.value.isNotEmpty()) {
                    _startTime.value
                } else {
                    "No disponible"
                }

                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    name = _title.value,
                    categories = _selectedCategories.value.toList(),
                    location = _location.value,
                    department = _department.value.ifEmpty { null },
                    city = _city.value.ifEmpty { null },
                    latitude = _latitude.value,
                    longitude = _longitude.value,
                    rating = "0.0",
                    imageUrl = finalImageUrl,
                    description = _description.value,
                    schedule = finalSchedule,
                    priceRange = _priceRange.value.ifEmpty { "No disponible" },
                    status = PostStatus.PENDING,
                    authorId = session?.userId ?: "unknown",
                    authorName = session?.name ?: "Usuario",
                    createdAt = System.currentTimeMillis()
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
