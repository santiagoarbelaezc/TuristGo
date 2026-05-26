package com.turistgo.app.features.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.User
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionsViewModel @Inject constructor(
    private val repository: AppDataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val targetUserId: String = checkNotNull(savedStateHandle["userId"])

    private val _targetUser = MutableStateFlow<User?>(null)
    val targetUser: StateFlow<User?> = _targetUser.asStateFlow()

    private val _followers = MutableStateFlow<List<User>>(emptyList())
    val followers: StateFlow<List<User>> = _followers.asStateFlow()

    private val _following = MutableStateFlow<List<User>>(emptyList())
    val following: StateFlow<List<User>> = _following.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Cargar el usuario objetivo
            val user = repository.getUserById(targetUserId)
            _targetUser.value = user
            
            if (user != null) {
                // Observar todos los usuarios para mantener actualizada la lista
                repository.getUsers().collectLatest { allUsers ->
                    // Seguidores: usuarios cuyo ID está en la lista followerIds del target
                    _followers.value = allUsers.filter { user.followerIds.contains(it.id) }
                    // Seguidos: usuarios cuyo ID está en la lista followingIds del target
                    _following.value = allUsers.filter { user.followingIds.contains(it.id) }
                }
            }
        }
    }
}
