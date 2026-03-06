package com.turistgo.app.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para la pantalla de recuperación de contraseña.
 * 
 * Este ViewModel maneja el estado y la lógica de negocio para solicitar
 * un restablecimiento de contraseña mediante correo electrónico.
 * 
 * Responsabilidades:
 * - Mantener el estado del email ingresado
 * - Validar que el email no esté vacío
 * - Manejar mensajes de feedback (Snackbar)
 * - Coordinar la navegación post-éxito
 * 
 * Buenas prácticas implementadas:
 * - Estado inmutable expuesto hacia la UI
 * - Validaciones centralizadas en el ViewModel
 * - Separación clara de responsabilidades
 * - Uso apropiado de State vs StateFlow
 * 
 * @see ForgotPasswordScreen Pantalla que consume este ViewModel
 */
class ForgotPasswordViewModel : ViewModel() {
    
    // ==================== ESTADOS DE LA UI ====================
    // Usamos mutableStateOf para el email porque es un estado simple
    // que solo necesita ser observado por Compose para recomposiciones
    
    /**
     * Almacena el email ingresado por el usuario.
     * Privado mutable para modificaciones internas, expuesto como inmutable.
     */
    private val _email = mutableStateOf("")
    
    /**
     * Email del usuario expuesto como State inmutable.
     * La UI puede leerlo pero no modificarlo directamente.
     */
    val email: State<String> = _email

    // ==================== EVENTOS DE UI (UNA SOLA VEZ) ====================
    // Usamos StateFlow para el mensaje del Snackbar porque:
    // 1. Representa eventos que deben consumirse una sola vez
    // 2. StateFlow soporta valores null para "sin mensaje"
    // 3. Es compatible con collectAsState() en Compose
    
    /**
     * Mensaje para mostrar en el Snackbar.
     * Cuando no hay mensaje, el valor es null.
     * La UI debe consumir este mensaje y llamar a clearSnackbarMessage()
     */
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    
    /**
     * Flujo de mensajes del Snackbar expuesto como inmutable.
     * La UI debe recolectar este flujo para mostrar mensajes.
     */
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // ==================== MÉTODOS PÚBLICOS ====================
    // Estos métodos son la interfaz entre la UI y la lógica de negocio

    /**
     * Actualiza el email cuando el usuario escribe en el campo de texto.
     * 
     * @param newEmail Nuevo valor del email (puede ser parcial mientras escribe)
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    /**
     * Procesa la solicitud de restablecimiento de contraseña.
     * 
     * Flujo:
     * 1. Valida que el email no esté vacío
     * 2. Si es válido, navega a la pantalla de restablecimiento
     * 3. Si no es válido, muestra mensaje de error
     * 
     * NOTA: Actualmente es una simulación sin backend real.
     * En una implementación real, aquí se haría la llamada a la API.
     * 
     * @param onNavigate Callback a ejecutar cuando la validación es exitosa
     *                   (generalmente navega a ResetPasswordScreen)
     */
    fun sendPasswordReset(onNavigate: () -> Unit) {
        // ==================== VALIDACIÓN ====================
        // Validamos que el email no esté vacío o solo contenga espacios
        if (_email.value.isBlank()) {
            // Mostramos mensaje de error amigable
            _snackbarMessage.value = "Por favor, ingresa tu correo electrónico"
            return // Detenemos la ejecución si hay error
        }
        
        // ==================== VALIDACIONES ADICIONALES RECOMENDADAS ====================
        // En una versión más completa, también validarías:
        /*
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _snackbarMessage.value = "Por favor, ingresa un correo electrónico válido"
            return
        }
        */
        
        // ==================== ACCIÓN EXITOSA ====================
        // Por ahora, simplemente navegamos sin llamada a backend
        // En el futuro, aquí iría algo como:
        // viewModelScope.launch {
        //     _isLoading.value = true
        //     try {
        //         authRepository.sendPasswordResetEmail(_email.value)
        //         onNavigate()
        //     } catch (e: Exception) {
        //         _snackbarMessage.value = "Error: ${e.message}"
        //     } finally {
        //         _isLoading.value = false
        //     }
        // }
        
        // Nota: Como no hay operación asíncrona, no necesitamos
        // viewModelScope ni estado de loading por ahora
        onNavigate()
    }

    /**
     * Limpia el mensaje del Snackbar después de ser mostrado.
     * 
     * Este método debe ser llamado desde la UI después de consumir el mensaje
     * para evitar que se muestre repetidamente.
     * 
     * Generalmente se llama en un LaunchedEffect después de showSnackbar()
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
    
    /**
     * Método de limpieza del ciclo de vida del ViewModel.
     * Se llama cuando el ViewModel es destruido (ej. al cerrar la pantalla).
     */
    override fun onCleared() {
        super.onCleared()
        // Aquí podrías cancelar operaciones pendientes si las hubiera
        // Por ahora no hay operaciones que cancelar
    }
}
