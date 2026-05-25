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
    val rejectedPercentage: Float = 0f,
    val loginHistory: List<Float> = emptyList(),
    val registerHistory: List<Float> = emptyList(),
    val postHistory: List<Float> = emptyList(),
    val recentLogs: List<com.turistgo.app.domain.model.ActivityLog> = emptyList()
)

@HiltViewModel
class ModeratorStatsViewModel @Inject constructor(
    private val repository: AppDataRepository
) : ViewModel() {

    val stats: StateFlow<PlatformStats> = combine(
        repository.getUsers(),
        repository.getPosts(),
        repository.getActivityLogs()
    ) { users, posts, logs ->
        val totalUsers = users.size
        val totalPosts = posts.size
        val approved = posts.count { it.status == PostStatus.APPROVED }
        val pending = posts.count { it.status == PostStatus.PENDING }
        val rejected = posts.count { it.status == PostStatus.REJECTED }
        
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()

        fun countEventsByDay(type: String): List<Float> {
            val dailyCounts = MutableList(7) { 0 }
            logs.filter { it.type == type }.forEach { log ->
                val diffDays = ((now - log.timestamp) / oneDayMillis).toInt()
                if (diffDays in 0..6) {
                    dailyCounts[6 - diffDays]++
                }
            }
            val maxVal = dailyCounts.maxOrNull() ?: 0
            if (maxVal == 0) return listOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f)
            return dailyCounts.map {
                if (maxVal > 0) (it.toFloat() / maxVal).coerceIn(0.1f, 1f) else 0.1f
            }
        }

        val loginHist = countEventsByDay("LOGIN")
        val registerHist = countEventsByDay("REGISTER")
        val postHist = countEventsByDay("PUBLISH_POST")
        
        PlatformStats(
            totalUsers = totalUsers,
            totalPosts = totalPosts,
            approvedPosts = approved,
            pendingPosts = pending,
            rejectedPosts = rejected,
            approvedPercentage = if (totalPosts > 0) approved.toFloat() / totalPosts else 0f,
            pendingPercentage = if (totalPosts > 0) pending.toFloat() / totalPosts else 0f,
            rejectedPercentage = if (totalPosts > 0) rejected.toFloat() / totalPosts else 0f,
            loginHistory = loginHist,
            registerHistory = registerHist,
            postHistory = postHist,
            recentLogs = logs.take(20)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlatformStats()
    )
}
