package com.turistgo.app.features.auth
 
import com.turistgo.app.core.utils.ColombiaGeography

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
import com.turistgo.app.core.models.AlertState
import com.turistgo.app.core.models.AlertType
import java.util.UUID
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val sessionManager: UserSessionManager,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _lastName = mutableStateOf("")
    val lastName: State<String> = _lastName

    private val _age = mutableStateOf("")
    val age: State<String> = _age

    private val _country = mutableStateOf("")
    val country: State<String> = _country
    
    private val _department = mutableStateOf("")
    val department: State<String> = _department
    
    private val _address = mutableStateOf("")
    val address: State<String> = _address

    private val _city = mutableStateOf("")
    val city: State<String> = _city
    
    private val _phoneExtension = mutableStateOf("+57")
    val phoneExtension: State<String> = _phoneExtension
    
    private val _phone = mutableStateOf("")
    val phone: State<String> = _phone
    
    val countries = listOf("Colombia", "Argentina", "Brasil")
 
    private val argentinaCities = listOf("Buenos Aires", "Córdoba", "Rosario", "Mendoza", "La Plata", "Mar del Plata", "San Miguel de Tucumán", "Salta", "Santa Fe", "Corrientes")
    private val brasilCities = listOf("São Paulo", "Rio de Janeiro", "Brasília", "Salvador", "Fortaleza", "Belo Horizonte", "Manaus", "Curitiba", "Recife", "Porto Alegre")

    private val _availableDepartments = mutableStateOf<List<String>>(emptyList())
    val availableDepartments: State<List<String>> = _availableDepartments

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

    private val _alertState = MutableStateFlow(AlertState())
    val alertState: StateFlow<AlertState> = _alertState.asStateFlow()

    fun onNameChange(v: String)            { _name.value = v }
    fun onLastNameChange(v: String)        { _lastName.value = v }
    fun onAgeChange(v: String)             { if (v.length <= 3 && v.all { it.isDigit() }) _age.value = v }
    
    fun onCountryChange(v: String) { 
        _country.value = v
        _department.value = "" 
        _city.value = ""
        
        when (v) {
            "Colombia" -> {
                _availableDepartments.value = ColombiaGeography.getDepartments()
                _availableCities.value = emptyList()
            }
            "Argentina" -> {
                _availableDepartments.value = emptyList()
                _availableCities.value = argentinaCities.sorted()
            }
            "Brasil" -> {
                _availableDepartments.value = emptyList()
                _availableCities.value = brasilCities.sorted()
            }
            else -> {
                _availableDepartments.value = emptyList()
                _availableCities.value = emptyList()
            }
        }
    }

    fun onDepartmentChange(v: String) {
        _department.value = v
        _city.value = ""
        if (_country.value == "Colombia") {
            _availableCities.value = ColombiaGeography.getCities(v)
        }
    }
    
    fun onCityChange(v: String)            { _city.value = v }
    fun onAddressChange(v: String)         { _address.value = v }
    fun onPhoneExtensionChange(v: String)  { _phoneExtension.value = v }
    fun onPhoneChange(v: String)           { if (v.length <= 10 && v.all { it.isDigit() }) _phone.value = v }
    fun onEmailChange(v: String)           { _email.value = v }
    fun onPasswordChange(v: String)        { _password.value = v }
    fun onConfirmPasswordChange(v: String) { _confirmPassword.value = v }

    fun register(onSuccess: (String) -> Unit) {
        val trimmedName = _name.value.trim()
        val trimmedLastName = _lastName.value.trim()
        val trimmedAge = _age.value.trim()
        val trimmedCountry = _country.value.trim()
        val trimmedCity = _city.value.trim()
        val trimmedPhone = _phone.value.trim()
        val trimmedEmail = _email.value.trim()
        val trimmedPassword = _password.value.trim()
        val trimmedConfirmPassword = _confirmPassword.value.trim()

        val fields = listOf(
            trimmedName to "Nombre",
            trimmedLastName to "Apellido",
            trimmedAge to "Edad",
            trimmedCountry to "País",
            trimmedCity to "Ciudad",
            trimmedPhone to "Teléfono",
            trimmedEmail to "Correo",
            trimmedPassword to "Contraseña",
            trimmedConfirmPassword to "Confirmación"
        )

        val emptyField = fields.find { it.first.isEmpty() }
        if (emptyField != null) {
            _alertState.value = AlertState(
                title = "Campo Requerido",
                message = "El campo '${emptyField.second}' es obligatorio. Por favor complétalo.",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        if (trimmedCountry == "Colombia" && _department.value.trim().isEmpty()) {
            _alertState.value = AlertState(
                title = "Geografía Incompleta",
                message = "Por favor selecciona un departamento para continuar con tu registro en Colombia.",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        val nameRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$".toRegex()
        if (!nameRegex.matches(trimmedName) || trimmedName.length < 2) {
            _alertState.value = AlertState(
                title = "Nombre Inválido",
                message = "El nombre debe contener solo letras y tener al menos 2 caracteres.",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        if (!nameRegex.matches(trimmedLastName) || trimmedLastName.length < 2) {
            _alertState.value = AlertState(
                title = "Apellido Inválido",
                message = "El apellido debe contener solo letras y tener al menos 2 caracteres.",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        val ageInt = trimmedAge.toIntOrNull()
        if (ageInt == null || ageInt < 18 || ageInt > 120) {
            _alertState.value = AlertState(
                title = "Restricción de Edad",
                message = "Debes ser mayor de 18 años para registrarte en TuristGo.",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        val phoneRegex = "^[0-9]{10}$".toRegex()
        if (!phoneRegex.matches(trimmedPhone)) {
            _alertState.value = AlertState(
                title = "Teléfono Inválido",
                message = "El número telefónico debe tener exactamente 10 dígitos numéricos.",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _alertState.value = AlertState(
                title = "Email Inválido",
                message = "La dirección de correo electrónico no tiene un formato válido.",
                type = AlertType.ERROR,
                isVisible = true
            )
            return
        }

        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-*?&()]).{8,}$".toRegex()
        if (!passwordPattern.matches(trimmedPassword)) {
             _alertState.value = AlertState(
                title = "Contraseña Débil",
                message = "La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial (ej. @, #, $, %, etc.).",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        if (trimmedPassword != trimmedConfirmPassword) {
            _alertState.value = AlertState(
                title = "Contraseñas Diferentes",
                message = "Las contraseñas no coinciden. Asegúrate de escribirlas idénticas en ambos campos.",
                type = AlertType.ERROR,
                isVisible = true
            )
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Crear usuario en Firebase Authentication
                val result = firebaseAuth.createUserWithEmailAndPassword(
                    trimmedEmail,
                    trimmedPassword
                ).await()

                val firebaseUser = result.user
                    ?: throw Exception("Firebase no retornó usuario")

                val uid = firebaseUser.uid
                val generatedUsername = trimmedEmail
                    .substringBefore("@")
                    .lowercase()
                    .filter { it.isLetterOrDigit() }

                val newUser = User(
                    id = uid,  // usar el UID de Firebase
                    name = trimmedName,
                    lastName = trimmedLastName,
                    age = trimmedAge,
                    country = trimmedCountry,
                    department = _department.value.trim().takeIf { it.isNotEmpty() },
                    city = trimmedCity,
                    address = _address.value.trim().takeIf { it.isNotEmpty() },
                    phone = "${_phoneExtension.value} ${trimmedPhone}",
                    email = trimmedEmail,
                    password = null,  // nunca guardar contraseña en Firestore
                    username = generatedUsername
                )

                // Guardar perfil en Firestore
                repository.saveUser(newUser)
                sessionManager.saveSession(uid, newUser.name, newUser.email, role = "USER")

                _snackbarMessage.value = "¡Bienvenido ${trimmedName}! Registro completado"
                onSuccess(uid)

            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("email address is already in use") == true ->
                        "Este correo ya está registrado. Intenta iniciar sesión."
                    e.message?.contains("badly formatted") == true ->
                        "El formato del correo no es válido."
                    else -> "Error al registrar: ${e.message}"
                }
                _alertState.value = AlertState(
                    title = "Error de Registro",
                    message = errorMessage,
                    type = AlertType.ERROR,
                    isVisible = true
                )
            }

            _isLoading.value = false
        }
    }

    fun registerWithSocial(provider: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _snackbarMessage.value = "Regístrate con $provider..."
            
            val userId = "social_${System.currentTimeMillis()}"
            val userName = "$provider User"
            val userEmail = "${provider.lowercase()}@example.com"
            
            val newUser = User(
                id = userId,
                name = userName,
                lastName = "Social",
                age = "0",
                country = "",
                city = "",
                phone = "",
                email = userEmail
            )
            repository.saveUser(newUser)
            
            sessionManager.saveSession(userId, userName, userEmail, role = "USER")
            
            kotlinx.coroutines.delay(2000)
            
            _snackbarMessage.value = "¡Bienvenido via $provider!"
            onSuccess(userId)
            
            _isLoading.value = false
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
    
    fun dismissAlert() {
        _alertState.value = _alertState.value.copy(isVisible = false)
    }
}
