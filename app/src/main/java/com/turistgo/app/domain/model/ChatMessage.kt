package com.turistgo.app.domain.model

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isPlanResponse: Boolean = false,
    val suggestedDestinations: List<Post> = emptyList()
)
