package com.turistgo.app.features.moderator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.PostStatus
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class PlatformStats(
    val totalUsers: Int = 0,
    val totalPosts: Int = 0,
    val approvedPosts: Int = 0,
    val pendingPosts: Int = 0,
    val rejectedPosts: Int = 0,
    val approvedPercentage: Float = 0f,
    val pendingPercentage: Float = 0f,
    val rejectedPercentage: Float = 0f
)

@HiltViewModel
class ModeratorStatsViewModel @Inject constructor(
    private val repository: AppDataRepository
) : ViewModel() {

    val stats: StateFlow<PlatformStats> = combine(
        repository.getUsers(),
        repository.getPosts()
    ) { users, posts ->
        val totalUsers = users.size
        val totalPosts = posts.size
        val approved = posts.count { it.status == PostStatus.APPROVED }
        val pending = posts.count { it.status == PostStatus.PENDING }
        val rejected = posts.count { it.status == PostStatus.REJECTED }
        
        PlatformStats(
            totalUsers = totalUsers,
            totalPosts = totalPosts,
            approvedPosts = approved,
            pendingPosts = pending,
            rejectedPosts = rejected,
            approvedPercentage = if (totalPosts > 0) approved.toFloat() / totalPosts else 0f,
            pendingPercentage = if (totalPosts > 0) pending.toFloat() / totalPosts else 0f,
            rejectedPercentage = if (totalPosts > 0) rejected.toFloat() / totalPosts else 0f
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlatformStats()
    )
}
