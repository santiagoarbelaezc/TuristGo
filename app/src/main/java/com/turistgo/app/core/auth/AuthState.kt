package com.turistgo.app.core.auth

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object NotAuthenticated : AuthState()
}
