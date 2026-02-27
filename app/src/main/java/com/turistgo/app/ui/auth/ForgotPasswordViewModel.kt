package com.turistgo.app.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {
    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _message = mutableStateOf<String?>(null)
    val message: State<String?> = _message

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun sendPasswordReset() {
        if (_email.value.isBlank()) {
            _snackbarMessage.value = "Por favor, ingresa tu correo electrónico"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            
            // Simulación de envío de correo
            delay(2000)
            
            _isLoading.value = false
            _isSuccess.value = true
            _message.value = "Se ha enviado un enlace de recuperación a ${_email.value}"
            _snackbarMessage.value = "Revisa tu correo para restablecer tu contraseña"
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}
