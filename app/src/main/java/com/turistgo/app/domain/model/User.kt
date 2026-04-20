package com.turistgo.app.domain.model

data class User(
    val id: String,
    val name: String,
    val lastName: String,
    val age: String,
    val country: String,
    val department: String? = null,
    val city: String,
    val address: String? = null,
    val phone: String,
    val email: String,
    val password: String? = null,
    val username: String? = null,
    val profilePhotoUrl: String? = null,
    val interests: List<String>? = emptyList(),
    val consentAccepted: Boolean = false,
    val consentTimestamp: Long? = null,
    val locale: String? = null,
    val notificationsEnabled: Boolean = true,
    val isVerified: Boolean = false,
    val role: String = "USER",
    val savedPostIds: List<String> = emptyList(),
    val likedPostIds: List<String> = emptyList(),
    val followingIds: List<String> = emptyList(),
    val followerIds: List<String> = emptyList()
)
