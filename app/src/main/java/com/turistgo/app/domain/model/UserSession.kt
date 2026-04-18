package com.turistgo.app.domain.model

data class UserSession(
    val userId: String?,
    val name: String?,
    val email: String?,
    val photoUrl: String? = null,
    val isLoggedIn: Boolean
)
