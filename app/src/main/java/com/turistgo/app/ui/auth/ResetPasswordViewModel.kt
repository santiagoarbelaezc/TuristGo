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

class ResetPasswordViewModel : ViewModel() {
    private val _code = mutableStateOf("")
    val code: State<String> = _code

    private val _newPassword = mutableStateOf("")
    val newPassword: State<String> = _newPassword

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onCodeChange(v: String)            { _code.value = v }
    fun onNewPasswordChange(v: String)     { _newPassword.value = v }
    fun onConfirmPasswordChange(v: String) { _confirmPassword.value = v }

    fun resetPassword(onSuccess: () -> Unit) {
        if (_code.value.isEmpty()) {
            _snackbarMessage.value = "Ingresa el código de verificación"
            return
        }
        if (_newPassword.value.isEmpty() || _confirmPassword.value.isEmpty()) {
            _snackbarMessage.value = "Por favor, completa todos los campos"
            return
        }
        if (_newPassword.value != _confirmPassword.value) {
            _snackbarMessage.value = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            delay(2000)
            _snackbarMessage.value = "¡Contraseña actualizada correctamente!"
            onSuccess()
            _isLoading.value = false
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}
