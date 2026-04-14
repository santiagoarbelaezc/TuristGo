package com.turistgo.app.features.profile

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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _lastName = mutableStateOf("")
    val lastName: State<String> = _lastName

    private val _phone = mutableStateOf("")
    val phone: State<String> = _phone

    private val _country = mutableStateOf("")
    val country: State<String> = _country

    private val _city = mutableStateOf("")
    val city: State<String> = _city

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var currentUser: User? = null

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val session = sessionManager.userSession.firstOrNull()
            if (session?.userId != null) {
                currentUser = repository.getUserById(session.userId)
                currentUser?.let { user ->
                    _name.value = user.name
                    _lastName.value = user.lastName
                    _phone.value = user.phone
                    _country.value = user.country
                    _city.value = user.city
                }
            }
        }
    }

    fun onNameChange(v: String) { _name.value = v }
    fun onLastNameChange(v: String) { _lastName.value = v }
    fun onPhoneChange(v: String) { _phone.value = v }
    fun onCountryChange(v: String) { _country.value = v }
    fun onCityChange(v: String) { _city.value = v }

    fun saveChanges(onSuccess: () -> Unit) {
        val user = currentUser ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            
            val updatedUser = user.copy(
                name = _name.value,
                lastName = _lastName.value,
                phone = _phone.value,
                country = _country.value,
                city = _city.value
            )
            
            repository.updateUser(updatedUser)
            sessionManager.saveSession(updatedUser.id, updatedUser.name, updatedUser.email)
            
            _snackbarMessage.value = "Perfil actualizado correctamente"
            kotlinx.coroutines.delay(500)
            onSuccess()
            
            _isLoading.value = false
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}
