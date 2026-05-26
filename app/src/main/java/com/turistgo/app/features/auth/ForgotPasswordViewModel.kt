package com.turistgo.app.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * ViewModel para la pantalla de recuperación de contraseña.
 *
 * Llama a Firebase para enviar el enlace de reseteo.
 * Devuelve el resultado al Composable mediante callbacks (onSuccess / snackbar).
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail.trim()
    }

    /**
     * Envía el correo de recuperación.
     * - [onSuccess] se invoca cuando Firebase confirma el envío.
     *   La UI usa este callback para cambiar al "Paso 2" (pantalla de confirmación).
     */
    fun sendPasswordReset(onSuccess: () -> Unit) {
        if (_email.value.isBlank()) {
            _snackbarMessage.value = "Por favor, ingresa tu correo electrónico"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _snackbarMessage.value = "Por favor, ingresa un correo electrónico válido"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                firebaseAuth.sendPasswordResetEmail(_email.value).await()
                onSuccess()
            } catch (e: Exception) {
                _snackbarMessage.value = "Error: ${e.localizedMessage ?: "Fallo al enviar correo"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
