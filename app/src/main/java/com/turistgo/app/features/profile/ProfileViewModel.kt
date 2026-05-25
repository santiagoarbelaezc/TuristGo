package com.turistgo.app.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val repository: AppDataRepository
) : ViewModel() {
    val userSession = sessionManager.userSession

    private val _userId = userSession.map { it.userId }

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile: StateFlow<User?> = _userId.flatMapLatest { id ->
        if (id != null) {
            flow { emit(repository.getUserById(id)) }
        } else {
            flowOf<User?>(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val myPosts = userSession.flatMapLatest { session ->
        val userId = session.userId
        if (userId != null) {
            repository.getPostsByAuthor(userId)
        } else {
            flowOf(emptyList())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val savedPosts = userSession.flatMapLatest { session ->
        val userId = session.userId
        if (userId != null) {
            repository.getSavedPosts(userId)
        } else {
            flowOf(emptyList())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val likedPosts = userSession.flatMapLatest { session ->
        val userId = session.userId
        if (userId != null) {
            repository.getLikedPosts(userId)
        } else {
            flowOf(emptyList())
        }
    }

    // --- Dynamic Gamification Logic ---

    val profileStats: StateFlow<ProfileStats> = combine(
        myPosts, savedPosts, likedPosts, userProfile
    ) { posts, saved, liked, user ->
        val postsSize = posts.size
        val savedSize = saved.size
        val likedSize = liked.size
        val followers = user?.followerIds?.size ?: 0
        val following = user?.followingIds?.size ?: 0
        
        val totalPoints = user?.points ?: 0
        
        val (levelName, levelNum, nextPts) = when {
            totalPoints <= 100 -> Triple("Novato", 1, 100)
            totalPoints <= 300 -> Triple("Explorador", 2, 300)
            else -> Triple("Guía Experto", 3, 300)
        }

        val progress = when {
            totalPoints <= 100 -> (totalPoints.toFloat() / 100f).coerceIn(0f, 1f)
            totalPoints <= 300 -> ((totalPoints - 100).toFloat() / 200f).coerceIn(0f, 1f)
            else -> 1f
        }
        
        // Simple badge logic for UI counters
        var badges = 0
        if (postsSize >= 1) {
            badges++ // First Step
            if (savedSize >= 5) badges++ // Curator
            if (likedSize >= 10) badges++ // Enthusiast
        }

        ProfileStats(
            levelName = levelName,
            levelNumber = levelNum,
            points = totalPoints,
            nextLevelPoints = nextPts,
            levelProgress = progress,
            badgesCount = badges,
            postsCount = postsSize,
            savedCount = savedSize,
            likedCount = likedSize,
            followersCount = followers,
            followingCount = following
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileStats())

    fun toggleSave(postId: String) {
        viewModelScope.launch {
            val userId = sessionManager.userSession.first().userId ?: return@launch
            repository.toggleSavedPost(userId, postId)
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val userId = sessionManager.userSession.first().userId ?: return@launch
            repository.toggleLikedPost(userId, postId)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }
}
