package com.turistgo.app.domain.model

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType { 
    NEW_POST, 
    VERIFICATION, 
    REPUTATION, 
    SYSTEM,
    COMMENT // New type for social interaction
}
