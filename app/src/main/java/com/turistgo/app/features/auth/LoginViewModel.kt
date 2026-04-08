package com.turistgo.app.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onEmailChange(newValue: String) { _email.value = newValue }
    fun onPasswordChange(newValue: String) { _password.value = newValue }

    fun login(onSuccess: (Boolean) -> Unit) {
        if (_email.value.isEmpty() || _password.value.isEmpty()) {
            _snackbarMessage.value = "Por favor, completa todos los campos"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            // Simulación de delay de red
            kotlinx.coroutines.delay(1000)
            
            val isAdmin = _email.value == "admin" && _password.value == "admin"
            onSuccess(isAdmin)
            
            _isLoading.value = false
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}
