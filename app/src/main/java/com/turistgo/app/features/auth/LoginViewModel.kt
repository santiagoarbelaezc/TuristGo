// Paquete donde se encuentra este ViewModel de autenticación
package com.turistgo.app.features.auth

// Importaciones de estados de Compose
import android.content.Context
import androidx.compose.runtime.State
          // Interfaz de solo lectura para estados observables
import androidx.compose.runtime.mutableStateOf // Crea un estado mutable que puede ser observado

// Importaciones de Android Architecture Components
import androidx.lifecycle.ViewModel            // Clase base para ViewModels (sobrevive cambios de configuración)
import androidx.lifecycle.viewModelScope      // CoroutineScope vinculado al ciclo de vida del ViewModel

// Importaciones de Kotlin Coroutines y Flows
import kotlinx.coroutines.flow.MutableStateFlow   // Flow mutable para emitir valores
import kotlinx.coroutines.flow.StateFlow         // Flow de solo lectura para estados
import kotlinx.coroutines.flow.asStateFlow       // Convierte MutableStateFlow a StateFlow (solo lectura)
import kotlinx.coroutines.launch                // Lanza una coroutine en un scope específico
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.core.auth.GoogleAuthHelper
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.domain.model.User
import com.turistgo.app.core.models.AlertState
import com.turistgo.app.core.models.AlertType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * LoginViewModel - Maneja la lógica de negocio y el estado de la pantalla de inicio de sesión
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val googleAuthHelper: GoogleAuthHelper,
    private val repository: AppDataRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    
    private val _email = mutableStateOf("")
    val email: State<String> = _email
    
    private val _password = mutableStateOf("")
    val password: State<String> = _password
    
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    private val _isPrivacyAccepted = mutableStateOf(false)
    val isPrivacyAccepted: State<Boolean> = _isPrivacyAccepted

    private val _alertState = MutableStateFlow(AlertState())
    val alertState: StateFlow<AlertState> = _alertState.asStateFlow()
    
    fun onPrivacyAcceptanceChange(newValue: Boolean) {
        _isPrivacyAccepted.value = newValue
    }
    
    fun onEmailChange(newValue: String) { 
        _email.value = newValue.trim() 
    }
    
    fun onPasswordChange(newValue: String) { 
        _password.value = newValue 
    }
    
    fun login(onSuccess: (Boolean) -> Unit) {
        if (!_isPrivacyAccepted.value) {
            _alertState.value = AlertState(
                title = "Políticas de Privacidad",
                message = "Por favor, acepta los términos y condiciones para continuar.",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        if (_email.value.isEmpty() || _password.value.isEmpty()) {
            _alertState.value = AlertState(
                title = "Campos Incompletos",
                message = "Asegúrate de ingresar tu correo y contraseña para poder entrar.",
                type = AlertType.WARNING,
                isVisible = true
            )
            return
        }

        if (!isValidIdentity(_email.value)) {
            _alertState.value = AlertState(
                title = "Identificación Inválida",
                message = "El formato ingresado no parece ser un correo válido ni un nombre de usuario permitido (sin espacios).",
                type = AlertType.ERROR,
                isVisible = true
            )
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Login por username → buscar email en Firestore primero
                val emailToUse = if (!_email.value.contains("@")) {
                    repository.getUserByUsername(_email.value)?.email ?: _email.value
                } else {
                    _email.value
                }

                // Autenticar con Firebase Authentication
                val result = firebaseAuth.signInWithEmailAndPassword(emailToUse, _password.value).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // Obtener datos completos del usuario desde Firestore
                    val user = repository.getUserById(firebaseUser.uid)

                    if (user != null) {
                        val isAdmin = user.role == "ADMIN"
                        sessionManager.saveSession(
                            userId = user.id,
                            name = "${user.name} ${user.lastName}",
                            email = user.email,
                            photoUrl = user.profilePhotoUrl,
                            role = user.role
                        )
                        onSuccess(isAdmin)
                    } else {
                        _alertState.value = AlertState(
                            title = "Usuario no encontrado",
                            message = "Tu cuenta existe pero no tiene perfil. Contacta soporte.",
                            type = AlertType.ERROR,
                            isVisible = true
                        )
                    }
                }
            } catch (e: Exception) {
                _alertState.value = AlertState(
                    title = "Credenciales Incorrectas",
                    message = "El correo/usuario o la contraseña no coinciden. Inténtalo de nuevo.",
                    type = AlertType.ERROR,
                    isVisible = true
                )
            }
            
            _isLoading.value = false
        }
    }

    private fun isValidIdentity(identity: String): Boolean {
        return if (identity.contains("@")) {
            android.util.Patterns.EMAIL_ADDRESS.matcher(identity).matches()
        } else {
            identity.length >= 3 && !identity.contains(" ")
        }
    }

    fun dismissAlert() {
        _alertState.value = _alertState.value.copy(isVisible = false)
    }

    fun loginWithSocial(context: Context, provider: String, onSuccess: (Boolean) -> Unit) {
        if (!_isPrivacyAccepted.value) {
            _snackbarMessage.value = "Debes aceptar el uso de datos personales para iniciar sesión"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            
            if (provider == "Google") {
                _snackbarMessage.value = "Conectando con Google..."
                val result = googleAuthHelper.getGoogleCredential(context)
                
                result.fold(
                    onSuccess = { googleUser ->
                        if (googleUser != null) {
                            sessionManager.saveSession(
                                userId = googleUser.id,
                                name = googleUser.name,
                                email = googleUser.email,
                                photoUrl = googleUser.photoUrl,
                                role = "USER"
                            )
                            
                            val nameParts = googleUser.name.split(" ", limit = 2)
                            val firstName = nameParts.getOrNull(0) ?: googleUser.name
                            val lastName = nameParts.getOrNull(1) ?: ""

                            repository.saveUser(
                                User(
                                    id = googleUser.id,
                                    name = firstName,
                                    lastName = lastName,
                                    age = "",
                                    country = "",
                                    city = "",
                                    phone = "",
                                    email = googleUser.email,
                                    profilePhotoUrl = googleUser.photoUrl,
                                    consentAccepted = true,
                                    consentTimestamp = System.currentTimeMillis(),
                                    locale = googleUser.locale
                                )
                            )

                            _snackbarMessage.value = "¡Bienvenido, ${googleUser.name}!"
                            onSuccess(false)
                        } else {
                            _snackbarMessage.value = "Error al iniciar sesión con Google: No se recibieron datos"
                        }
                    },
                    onFailure = { error ->
                        _snackbarMessage.value = "Error: ${error.localizedMessage ?: "Fallo al conectar con Google"}"
                    }
                )
            } else {
                _snackbarMessage.value = "Conectando con $provider..."
                kotlinx.coroutines.delay(2000)
                
                sessionManager.saveSession(
                    userId = "social_${System.currentTimeMillis()}",
                    name = "$provider User",
                    email = "${provider.lowercase()}@example.com",
                    role = "USER"
                )
                
                repository.saveUser(
                    User(
                        id = "social_${System.currentTimeMillis()}",
                        name = "$provider",
                        lastName = "User",
                        age = "",
                        country = "",
                        city = "",
                        phone = "",
                        email = "${provider.lowercase()}@example.com"
                    )
                )

                _snackbarMessage.value = "¡Bienvenido vía $provider!"
                onSuccess(false)
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearSnackbarMessage() { 
        _snackbarMessage.value = null 
    }
}
