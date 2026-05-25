package com.turistgo.app.domain.model

data class ActivityLog(
    val id: String = "",
    val type: String = "", // "LOGIN", "REGISTER", "PUBLISH_POST"
    val userId: String = "",
    val userName: String = "",
    val details: String = "",
    val timestamp: Long = 0L
)
