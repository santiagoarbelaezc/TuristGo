package com.turistgo.app.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import com.turistgo.app.core.network.CloudinaryManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val repository: AppDataRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _photoUri = mutableStateOf<String?>(null)
    val photoUri: State<String?> = _photoUri

    private val _interests = mutableStateOf<List<String>>(emptyList())
    val interests: State<List<String>> = _interests

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onUsernameChange(v: String) { _username.value = v }
    fun onPhotoUriChange(v: String?) { _photoUri.value = v }
    
    fun toggleInterest(interest: String) {
        val current = _interests.value.toMutableList()
        if (current.contains(interest)) {
            current.remove(interest)
        } else {
            current.add(interest)
        }
        _interests.value = current
    }

    fun saveProfile(userId: String, onSuccess: () -> Unit) {
        if (_username.value.isEmpty()) {
            _snackbarMessage.value = "Por favor ingresa un nombre de usuario"
            return
        }
        if (_interests.value.isEmpty()) {
            _snackbarMessage.value = "Selecciona al menos un interés"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            
            var finalPhotoUrl = _photoUri.value
            
            if (finalPhotoUrl != null && finalPhotoUrl.startsWith("content://")) {
                val uri = Uri.parse(finalPhotoUrl)
                val uploadedUrl = CloudinaryManager.uploadImage(context, uri)
                if (uploadedUrl != null) {
                    finalPhotoUrl = uploadedUrl
                }
            }

            val user = repository.getUserById(userId)
            if (user != null) {
                val updatedUser = user.copy(
                    username = _username.value,
                    profilePhotoUrl = finalPhotoUrl,
                    interests = _interests.value
                )
                repository.updateUser(updatedUser)
                onSuccess()
            } else {
                _snackbarMessage.value = "Error: Usuario no encontrado"
            }
            
            _isLoading.value = false
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}
