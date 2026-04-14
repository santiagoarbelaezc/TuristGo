package com.turistgo.app.domain.model

data class Post(
    val id: String,
    val name: String,
    val location: String,
    val rating: String,
    val imageUrl: String,
    val description: String = ""
)
