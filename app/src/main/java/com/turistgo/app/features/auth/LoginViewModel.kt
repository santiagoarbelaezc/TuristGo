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

/**
 * LoginViewModel - Maneja la lógica de negocio y el estado de la pantalla de inicio de sesión
 * 
 * Responsabilidades:
 * 1. Almacenar y gestionar los estados de email y contraseña
 * 2. Manejar el estado de carga durante la autenticación
 * 3. Gestionar mensajes de notificación (Snackbar)
 * 4. Validar credenciales y simular el proceso de login
 * 5. Diferenciar entre usuarios normales y administradores
 * 
 * Patrones utilizados:
 * - Unidirectional Data Flow (UDF): Los eventos fluyen del UI al ViewModel
 * - Observable State: El UI observa los estados del ViewModel
 * - StateFlow y State: Para estados reactivos
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val googleAuthHelper: GoogleAuthHelper,
    private val repository: AppDataRepository
) : ViewModel() {
    
    // ==================== ESTADOS PRIVADOS MUTABLES ====================
    
    /**
     * Estado interno mutable del email
     * mutableStateOf: Crea un observable que recompondrá la UI cuando cambie
     * valor inicial: String vacío ""
     */
    private val _email = mutableStateOf("")
    
    /**
     * Estado público de solo lectura del email
     * State<String>: Interfaz de solo lectura que expone el valor actual
     * El UI puede LEER pero no MODIFICAR directamente
     */
    val email: State<String> = _email
    
    /**
     * Estado interno mutable de la contraseña
     * Privado para encapsulamiento - solo el ViewModel puede modificarlo
     */
    private val _password = mutableStateOf("")
    
    /**
     * Estado público de solo lectura de la contraseña
     * El UI observa este estado para mostrar el valor actual
     */
    val password: State<String> = _password
    
    /**
     * Estado interno mutable de carga (loading)
     * true: Mostrar indicador de progreso, deshabilitar botones
     * false: UI normal, interacciones habilitadas
     */
    private val _isLoading = mutableStateOf(false)
    
    /**
     * Estado público de solo lectura del estado de carga
     * El UI usa esto para mostrar/ocultar loading y habilitar/deshabilitar interacciones
     */
    val isLoading: State<Boolean> = _isLoading
    
    /**
     * Flow mutable para mensajes de notificación (Snackbar)
     * MutableStateFlow: Similar a mutableStateOf pero para Flows
     * - Soporta operaciones de coroutine
     * - Ideal para eventos únicos como mostrar un mensaje
     * valor inicial: null (sin mensaje)
     */
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    
    /**
     * Flow público de solo lectura para mensajes del Snackbar
     * StateFlow: Emite valores a los collectors (el UI)
     * asStateFlow(): Convierte a tipo inmutable (solo lectura)
     * El UI colecta estos cambios para mostrar notificaciones
     */
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    /**
     * Estado de aceptación de términos y privacidad
     */
    private val _isPrivacyAccepted = mutableStateOf(false)
    val isPrivacyAccepted: State<Boolean> = _isPrivacyAccepted
    
    // ==================== MÉTODOS PÚBLICOS (EVENTOS DEL UI) ====================

    fun onPrivacyAcceptanceChange(newValue: Boolean) {
        _isPrivacyAccepted.value = newValue
    }
    
    /**
     * Actualiza el valor del email cuando el usuario escribe en el campo
     * Este método es llamado desde el UI (OutlinedTextField)
     * 
     * @param newValue El nuevo texto ingresado por el usuario
     * 
     * Flujo: UI -> ViewModel -> Actualiza estado -> UI se recomponer
     */
    fun onEmailChange(newValue: String) { 
        _email.value = newValue 
    }
    
    /**
     * Actualiza el valor de la contraseña cuando el usuario escribe
     * 
     * @param newValue El nuevo texto de contraseña ingresado
     */
    fun onPasswordChange(newValue: String) { 
        _password.value = newValue 
    }
    
    /**
     * Método principal de autenticación - Valida credenciales y realiza login
     * 
     * @param onSuccess Callback que devuelve true si es administrador, false si es usuario normal
     */
    fun login(onSuccess: (Boolean) -> Unit) {
        if (!_isPrivacyAccepted.value) {
            _snackbarMessage.value = "Debes aceptar el uso de datos personales para continuar"
            return
        }

        if (_email.value.isEmpty() || _password.value.isEmpty()) {
            _snackbarMessage.value = "Por favor, completa todos los campos"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(1000)
            
            val isAdmin = _email.value == "admin" && _password.value == "admin"
            
            if (isAdmin) {
                sessionManager.saveSession(
                    userId = "admin_001",
                    name = "Administrador",
                    email = _email.value
                )
                onSuccess(true)
            } else {
                // Buscar usuario en el repositorio por email o username
                val user = repository.getUserByEmail(_email.value) 
                        ?: repository.getUserByUsername(_email.value)
                
                if (user != null && user.password == _password.value) {
                    sessionManager.saveSession(
                        userId = user.id,
                        name = "${user.name} ${user.lastName}",
                        email = user.email,
                        photoUrl = user.profilePhotoUrl
                    )
                    onSuccess(false)
                } else {
                    _snackbarMessage.value = "Credenciales incorrectas"
                }
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Realiza login con proveedor social (Google, Facebook, LinkedIn)
     * 
     * @param provider El nombre del proveedor
     * @param onSuccess Callback de éxito
     */
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
                                photoUrl = googleUser.photoUrl
                            )
                            
                            // PERSISTIR EN EL REPOSITORIO
                            // Intentamos separar el nombre en partes
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
                // Simular latencia para otros proveedores (Facebook, LinkedIn)
                kotlinx.coroutines.delay(2000)
                
                sessionManager.saveSession(
                    userId = "social_${System.currentTimeMillis()}",
                    name = "$provider User",
                    email = "${provider.lowercase()}@example.com"
                )
                
                // También persistir en el repositorio para otros proveedores
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

// ==================== NOTAS ADICIONALES SOBRE BUENAS PRÁCTICAS ====================

/**
 * 1. ENCAPSULAMIENTO:
 *    - Propiedades mutables (_email, _password, etc) son PRIVADAS
 *    - Propiedades públicas (email, password, etc) son de solo lectura
 *    - Solo el ViewModel puede modificar el estado interno
 *
 * 2. REACTIVIDAD:
 *    - Uso de mutableStateOf para estados que causan recomposición
 *    - Uso de StateFlow para eventos únicos (Snackbar)
 *    - El UI observa cambios automáticamente
 *
 * 3. CICLO DE VIDA:
 *    - ViewModel sobrevive a cambios de configuración (rotación, cambio de idioma)
 *    - viewModelScope se cancela automáticamente cuando el ViewModel ya no es necesario
 *
 * 4. SEGURIDAD DE HILOS:
 *    - viewModelScope.launch ejecuta en el hilo principal (Dispatchers.Main por defecto)
 *    - Para operaciones pesadas, se usaría withContext(Dispatchers.IO)
 *
 * 5. SEPARACIÓN DE RESPONSABILIDADES:
 *    - ViewModel: Lógica de negocio, estado, validación
 *    - UI: Solo muestra datos y envía eventos
 *
 * 6. MEJORAS PARA PRODUCCIÓN:
 *    - Inyección de dependencias (repositorio de autenticación)
 *    - Manejo de errores más robusto (try-catch)
 *    - Validación más estricta (expresiones regulares para email)
 *    - Encriptación de credenciales
 *    - Autenticación biométrica
 *    - Manejo de tokens JWT
 *    - Persistencia de sesión
 */
