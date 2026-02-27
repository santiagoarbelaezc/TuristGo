package com.turistgo.app.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _city = mutableStateOf("")
    val city: State<String> = _city

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onNameChange(newValue: String) { _name.value = newValue }
    fun onEmailChange(newValue: String) { _email.value = newValue }
    fun onCityChange(newValue: String) { _city.value = newValue }
    fun onPasswordChange(newValue: String) { _password.value = newValue }

    fun register(onSuccess: () -> Unit) {
        if (_name.value.isEmpty() || _email.value.isEmpty() || _password.value.isEmpty()) {
            _snackbarMessage.value = "Todos los campos son obligatorios"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(1500)
            
            _snackbarMessage.value = "¡Bienvenido Explorador! Has ganado 10 puntos"
            onSuccess()
            
            _isLoading.value = false
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}
