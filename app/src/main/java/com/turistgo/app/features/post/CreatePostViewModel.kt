package com.turistgo.app.features.post

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {
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
}
