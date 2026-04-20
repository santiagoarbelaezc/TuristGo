package com.turistgo.app.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.User
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.features.profile.ProfileStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val repository: AppDataRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)

    private val userSession = sessionManager.userSession

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentUser = userSession.flatMapLatest { session ->
        val userId = session.userId
        if (userId != null) {
            repository.getUsers().map { users -> users.find { it.id == userId } }
        } else flowOf(null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile: StateFlow<User?> = _userId.flatMapLatest { id ->
        if (id != null) {
            repository.getUsers().map { users -> users.find { it.id == id } }
        } else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isFollowing: StateFlow<Boolean> = combine(currentUser, userProfile) { current, target ->
        current != null && target != null && current.followingIds.contains(target.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isMe: StateFlow<Boolean> = combine(currentUser, userProfile) { current, target ->
        current != null && target != null && current.id == target.id
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val userPosts: StateFlow<List<Post>> = _userId.flatMapLatest { id ->
        if (id != null) repository.getPostsByAuthor(id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profileStats: StateFlow<ProfileStats> = combine(userPosts, userProfile) { posts, user ->
        calculateStats(posts, user)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileStats())

    fun loadUser(userId: String) {
        _userId.value = userId
    }

    fun requestFollow() {
        val targetId = _userId.value ?: return
        viewModelScope.launch {
            val session = sessionManager.userSession.first()
            val currentUserId = session.userId ?: return@launch
            val currentUserName = session.name ?: "Usuario"
            repository.sendFollowRequest(currentUserId, currentUserName, targetId)
        }
    }

    private fun calculateStats(posts: List<Post>, user: User?): ProfileStats {
        val postsSize = posts.size
        val followersList = user?.followerIds ?: emptyList()
        val followingList = user?.followingIds ?: emptyList()
        
        val (levelName, levelNum, _) = when {
            postsSize >= 10 -> Triple("Guía Local", 3, 5000)
            postsSize >= 5 -> Triple("Viajero", 2, 600)
            postsSize >= 1 -> Triple("Explorador", 1, 300)
            else -> Triple("Novato", 0, 100)
        }

        return ProfileStats(
            levelName = levelName,
            levelNumber = levelNum,
            postsCount = postsSize,
            savedCount = user?.savedPostIds?.size ?: 0,
            likedCount = user?.likedPostIds?.size ?: 0,
            followersCount = followersList.size,
            followingCount = followingList.size,
            points = postsSize * 50 + followersList.size * 20,
            levelProgress = 0.5f,
            badgesCount = if (postsSize >= 1) 1 else 0
        )
    }
}
