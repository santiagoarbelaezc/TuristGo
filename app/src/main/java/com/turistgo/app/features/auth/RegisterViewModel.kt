package com.turistgo.app.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.domain.model.User
import com.turistgo.app.data.datastore.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {
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

    private val _phoneExtension = mutableStateOf("+57")
    val phoneExtension: State<String> = _phoneExtension

    private val _phone = mutableStateOf("")
    val phone: State<String> = _phone

    // Listas de datos
    val countries = listOf("Colombia", "México", "Argentina", "España", "Chile")
    
    private val countryCities = mapOf(
        "Colombia" to listOf("Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena"),
        "México" to listOf("CDMX", "Guadalajara", "Monterrey", "Puebla", "Cancún"),
        "Argentina" to listOf("Buenos Aires", "Córdoba", "Rosario", "Mendoza", "La Plata"),
        "España" to listOf("Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza"),
        "Chile" to listOf("Santiago", "Valparaíso", "Concepción", "La Serena", "Antofagasta")
    )

    private val _availableCities = mutableStateOf<List<String>>(emptyList())
    val availableCities: State<List<String>> = _availableCities

    val phoneExtensions = listOf("+57", "+52", "+54", "+34", "+56", "+1", "+33", "+49")

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
    
    fun onCountryChange(v: String) { 
        _country.value = v
        _city.value = "" 
        _availableCities.value = countryCities[v] ?: emptyList()
    }
    
    fun onCityChange(v: String)            { _city.value = v }
    fun onPhoneExtensionChange(v: String)  { _phoneExtension.value = v }
    fun onPhoneChange(v: String)           { _phone.value = v }
    fun onEmailChange(v: String)           { _email.value = v }
    fun onPasswordChange(v: String)        { _password.value = v }
    fun onConfirmPasswordChange(v: String) { _confirmPassword.value = v }

    fun register(onSuccess: (String) -> Unit) {
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
            
            val userId = UUID.randomUUID().toString()
            val newUser = User(
                id = userId,
                name = _name.value,
                lastName = _lastName.value,
                age = _age.value,
                country = _country.value,
                city = _city.value,
                phone = "${_phoneExtension.value} ${_phone.value}",
                email = _email.value,
                password = _password.value
            )
            
            repository.saveUser(newUser)
            sessionManager.saveSession(userId, newUser.name, newUser.email)

            kotlinx.coroutines.delay(3000)
            _snackbarMessage.value = "¡Bienvenido ${_name.value}! Registro casi completo"
            onSuccess(userId)

            _isLoading.value = false
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}

