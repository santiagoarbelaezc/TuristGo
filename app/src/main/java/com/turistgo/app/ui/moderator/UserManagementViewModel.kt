package com.turistgo.app.ui.moderator

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class ModeratorUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String = "Usuario",
    val isVerified: Boolean = false
)

class UserManagementViewModel : ViewModel() {
    private val _users = mutableStateListOf(
        ModeratorUser("1", "Santiago Arbelaez", "santiago@example.com", isVerified = true),
        ModeratorUser("2", "Maria Lopez", "maria@example.com"),
        ModeratorUser("3", "Carlos Ruiz", "carlos@example.com"),
        ModeratorUser("4", "Ana Gomez", "ana@example.com")
    )
    val users: List<ModeratorUser> = _users

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun verifyUser(userId: String) {
        val index = _users.indexOfFirst { it.id == userId }
        if (index != -1) {
            _users[index] = _users[index].copy(isVerified = true)
        }
    }

    fun deleteUser(userId: String) {
        _users.removeIf { it.id == userId }
    }
}
