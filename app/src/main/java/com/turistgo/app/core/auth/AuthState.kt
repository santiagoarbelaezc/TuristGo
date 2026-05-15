package com.turistgo.app.core.auth

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val role: String) : AuthState()
    object NotAuthenticated : AuthState()
}
