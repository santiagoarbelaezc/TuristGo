package com.turistgo.app.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.PostStatus
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.data.datastore.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val repository: AppDataRepository
) : ViewModel() {
    
    val userSession = sessionManager.userSession
    
    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchCategory = MutableStateFlow("All")
    val searchCategory: StateFlow<String> = _searchCategory.asStateFlow()
    
    // Original Posts
    private val allPosts = repository.getPosts(PostStatus.APPROVED)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentUser = userSession.flatMapLatest { session ->
        val userId = session.userId
        if (userId != null) {
            repository.getUsers().map { users -> users.find { it.id == userId } }
        } else flowOf(null)
    }

    val savedPostIds = currentUser.map { it?.savedPostIds ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedPostIds = currentUser.map { it?.likedPostIds ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Reactive filtered results
    val filteredPosts: StateFlow<List<Post>> = combine(
        allPosts,
        _searchQuery,
        _searchCategory
    ) { posts, query, category ->
        var list = posts
        
        // Filter by category
        val isAllCategory = category == "All" || category == "Todo" || category == "Todos"
        if (!isAllCategory) {
            list = list.filter { it.categories.contains(category) }
        }
        
        // Filter by query
        if (query.isNotEmpty()) {
            list = list.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.location.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        
        // Sort by date descending (Newest first)
        list.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSearchCategory(category: String) {
        _searchCategory.value = category
    }

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
}

// Helpers for Flow
suspend fun <T> Flow<T>.firstOrNull(): T? = try { first() } catch (e: Exception) { null }
