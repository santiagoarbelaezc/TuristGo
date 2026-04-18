package com.turistgo.app.domain.model

data class Post(
    val id: String,
    val name: String,
    val categories: List<String> = emptyList(),
    val location: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val rating: String,
    val imageUrl: String,
    val description: String = "",
    val schedule: String = "No disponible",
    val priceRange: String = "No disponible",
    val status: PostStatus = PostStatus.APPROVED,
    val authorId: String = "admin",
    val authorName: String = "TuristGo",
    val createdAt: Long = System.currentTimeMillis()
)
