package com.turistgo.app.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de restablecimiento de contraseña.
 * 
 * Responsabilidades:
 * - Mantener el estado de la UI (campos de texto, loading, mensajes)
 * - Validar los datos ingresados por el usuario
 * - Ejecutar la lógica de negocio (reset de contraseña)
 * - Manejar la comunicación con la UI mediante State y StateFlow
 * 
 * Buenas prácticas implementadas:
 * - Inmutabilidad de estados expuestos (usando State/StateFlow)
 * - Separación entre estado interno (_variable) y público (variable)
 * - Manejo de corrutinas con viewModelScope
 * - Validaciones en el ViewModel (no en la UI)
 * - Nombres descriptivos para métodos y variables
 */
class ResetPasswordViewModel : ViewModel() {
    
    // ==================== ESTADOS DE LA UI ====================
    // Usamos mutableStateOf para estados simples que solo necesita Compose
    // Los exponemos como State<String> para garantizar inmutabilidad desde fuera
    
    /**
     * Código de verificación enviado al email del usuario
     */
    private val _code = mutableStateOf("")
    val code: State<String> = _code

    /**
     * Nueva contraseña ingresada por el usuario
     */
    private val _newPassword = mutableStateOf("")
    val newPassword: State<String> = _newPassword

    /**
     * Confirmación de la nueva contraseña
     */
    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    /**
     * Indicador de carga para operaciones asíncronas
     */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // ==================== FLUJOS PARA EVENTOS ====================
    // Usamos StateFlow para mensajes que deben ser consumidos una sola vez
    // StateFlow es ideal para eventos como Snackbars o Navigations
    
    /**
     * Mensaje para mostrar en Snackbar.
     * Se usa StateFlow porque es un evento que debe ser consumido
     * y limpiado después de mostrarse.
     */
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // ==================== MÉTODOS PÚBLICOS ====================
    // Estos métodos son llamados desde la UI para actualizar el estado

    /**
     * Actualiza el código de verificación cuando el usuario escribe
     * @param v Nuevo valor del código
     */
    fun onCodeChange(v: String) {
        _code.value = v
    }

    /**
     * Actualiza la nueva contraseña cuando el usuario escribe
     * @param v Nuevo valor de la contraseña
     */
    fun onNewPasswordChange(v: String) {
        _newPassword.value = v
    }

    /**
     * Actualiza la confirmación de contraseña cuando el usuario escribe
     * @param v Nuevo valor de la confirmación
     */
    fun onConfirmPasswordChange(v: String) {
        _confirmPassword.value = v
    }

    /**
     * Ejecuta el proceso de restablecimiento de contraseña.
     * 
     * Validaciones realizadas:
     * 1. Código no vacío
     * 2. Campos de contraseña completos
     * 3. Coincidencia entre nueva contraseña y confirmación
     * 
     * @param onSuccess Callback a ejecutar cuando el reset es exitoso
     *                  (generalmente para navegar a Login)
     */
    fun resetPassword(onSuccess: () -> Unit) {
        // ==================== VALIDACIONES ====================
        // Las validaciones se hacen en el ViewModel, no en la UI
        // Esto mantiene la lógica de negocio centralizada y testeable
        
        // Validación 1: Código de verificación
        if (_code.value.isBlank()) {  // Usamos isBlank() en lugar de isEmpty() para considerar espacios
            _snackbarMessage.value = "Ingresa el código de verificación"
            return
        }
        
        // Validación 2: Campos completos
        if (_newPassword.value.isBlank() || _confirmPassword.value.isBlank()) {
            _snackbarMessage.value = "Por favor, completa todos los campos"
            return
        }
        
        // Validación 3: Contraseñas coincidentes
        if (_newPassword.value != _confirmPassword.value) {
            _snackbarMessage.value = "Las contraseñas no coinciden"
            return
        }
        
        // Validación adicional recomendada: Fortaleza de contraseña
        if (_newPassword.value.length < 6) {
            _snackbarMessage.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        // ==================== EJECUCIÓN ASINCRÓNICA ====================
        // Lanzamos una corrutina en el scope del ViewModel
        // Esto asegura que la operación se cancele si el ViewModel es destruido
        viewModelScope.launch {
            try {
                // Mostramos indicador de carga
                _isLoading.value = true
                
                // SIMULACIÓN: Llamada a API o repositorio
                // En una implementación real, aquí iría algo como:
                // authRepository.resetPassword(_code.value, _newPassword.value)
                delay(2000) // Simulamos operación de red
                
                // Éxito: Mostramos mensaje y ejecutamos callback
                _snackbarMessage.value = "¡Contraseña actualizada correctamente!"
                onSuccess()
                
            } catch (e: Exception) {
                // Manejo de errores de red/API
                _snackbarMessage.value = "Error al actualizar: ${e.message}"
            } finally {
                // Aseguramos que el loading se desactive incluso si hay error
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia el mensaje del Snackbar después de ser mostrado.
     * Este método debe ser llamado desde la UI después de consumir el mensaje.
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
    
    /**
     * Método de limpieza opcional que se llama cuando el ViewModel es destruido.
     * Útil para cancelar operaciones pendientes o liberar recursos.
     */
    override fun onCleared() {
        super.onCleared()
        // Aquí podrías cancelar operaciones específicas si es necesario
        // viewModelScope.cancel() // No necesario, se cancela automáticamente
    }
}
