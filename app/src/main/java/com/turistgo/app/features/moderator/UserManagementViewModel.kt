package com.turistgo.app.features.moderator

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.turistgo.app.domain.repository.AppDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModeratorUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String = "Usuario",
    val isVerified: Boolean = false
)

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val repository: AppDataRepository
) : ViewModel() {
    
    val users: StateFlow<List<ModeratorUser>> = repository.getUsers()
        .map { domainUsers ->
            domainUsers.map { user ->
                ModeratorUser(
                    id = user.id,
                    name = "${user.name} ${user.lastName}",
                    email = user.email,
                    role = user.role,
                    isVerified = user.isVerified
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun verifyUser(userId: String) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                repository.updateUser(user.copy(isVerified = true))
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }
}
