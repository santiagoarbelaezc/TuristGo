package com.turistgo.app.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ForgotPasswordViewModel : ViewModel() {
    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun sendPasswordReset(onNavigate: () -> Unit) {
        if (_email.value.isBlank()) {
            _snackbarMessage.value = "Por favor, ingresa tu correo electrónico"
            return
        }
        // Sin lógica de backend — navega directamente
        onNavigate()
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}
