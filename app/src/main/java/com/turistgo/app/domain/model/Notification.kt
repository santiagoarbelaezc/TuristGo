package com.turistgo.app.domain.model

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val postId: String? = null,
    val senderId: String? = null,
    val senderName: String? = null
)

enum class NotificationType { 
    NEW_POST, 
    VERIFICATION, 
    REPUTATION, 
    SYSTEM,
    COMMENT,
    POST_APPROVED,
    POST_REJECTED,
    FOLLOW_REQUEST,
    FOLLOW_ACCEPTED
}
