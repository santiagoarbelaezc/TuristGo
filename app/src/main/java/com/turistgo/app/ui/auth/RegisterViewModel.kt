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

    private val _lastName = mutableStateOf("")
    val lastName: State<String> = _lastName

    private val _age = mutableStateOf("")
    val age: State<String> = _age

    private val _country = mutableStateOf("")
    val country: State<String> = _country

    private val _city = mutableStateOf("")
    val city: State<String> = _city

    private val _phone = mutableStateOf("")
    val phone: State<String> = _phone

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onNameChange(v: String)            { _name.value = v }
    fun onLastNameChange(v: String)        { _lastName.value = v }
    fun onAgeChange(v: String)             { _age.value = v }
    fun onCountryChange(v: String)         { _country.value = v }
    fun onCityChange(v: String)            { _city.value = v }
    fun onPhoneChange(v: String)           { _phone.value = v }
    fun onEmailChange(v: String)           { _email.value = v }
    fun onPasswordChange(v: String)        { _password.value = v }
    fun onConfirmPasswordChange(v: String) { _confirmPassword.value = v }

    fun register(onSuccess: () -> Unit) {
        val fields = listOf(_name, _lastName, _age, _country, _city, _phone, _email, _password, _confirmPassword)
        if (fields.any { it.value.isEmpty() }) {
            _snackbarMessage.value = "Todos los campos son obligatorios"
            return
        }
        if (_password.value != _confirmPassword.value) {
            _snackbarMessage.value = "Las contraseñas no coinciden"
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
