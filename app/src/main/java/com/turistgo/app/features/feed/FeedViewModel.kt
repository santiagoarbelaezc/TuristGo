package com.turistgo.app.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.PostStatus
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.data.datastore.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    
    // Reactive filtered results
    val filteredPosts: StateFlow<List<Post>> = combine(
        allPosts,
        _searchQuery,
        _searchCategory
    ) { posts, query, category ->
        var list = posts
        
        // Filter by category if not "All"
        if (category != "All" && category != "Todo") {
            list = list.filter { it.categories.contains(category) }
        }
        
        // Filter by query (title/name or location)
        if (query.isNotEmpty()) {
            list = list.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.location.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSearchCategory(category: String) {
        _searchCategory.value = category
    }
}
