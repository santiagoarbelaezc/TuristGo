package com.turistgo.app.domain.model

data class User(
    val id: String,
    val name: String,
    val lastName: String,
    val age: String,
    val country: String,
    val city: String,
    val phone: String,
    val email: String,
    val password: String? = null,
    val username: String? = null,
    val profilePhotoUrl: String? = null,
    val interests: List<String>? = emptyList()
)
