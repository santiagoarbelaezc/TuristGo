package com.turistgo.app.features.profile

data class ProfileStats(
    val levelName: String = "Novato",
    val levelNumber: Int = 0,
    val points: Int = 0,
    val nextLevelPoints: Int = 500,
    val levelProgress: Float = 0f,
    val badgesCount: Int = 0,
    val postsCount: Int = 0,
    val savedCount: Int = 0,
    val likedCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0
)
