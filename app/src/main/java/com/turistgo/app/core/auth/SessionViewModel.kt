package com.turistgo.app.core.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.turistgo.app.data.datastore.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    val authState: StateFlow<AuthState> = sessionManager.userSession
        .map { session ->
            if (session.isLoggedIn) {
                AuthState.Authenticated(session.role ?: "USER")
            } else {
                AuthState.NotAuthenticated
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )

    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            sessionManager.clearSession()
        }
    }
}
