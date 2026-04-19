package com.turistgo.app.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorPhotoUrl: String? = null,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
