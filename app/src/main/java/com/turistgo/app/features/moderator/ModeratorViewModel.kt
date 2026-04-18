package com.turistgo.app.features.moderator

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.PostStatus
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModeratorViewModel @Inject constructor(
    private val repository: AppDataRepository
) : ViewModel() {

    val posts: StateFlow<List<Post>> = repository.getPosts(PostStatus.PENDING)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun approvePost(postId: String) {
        viewModelScope.launch {
            repository.updatePostStatus(postId, PostStatus.APPROVED)
        }
    }

    fun rejectPost(postId: String, reason: String) {
        viewModelScope.launch {
            // Actualmente el modelo Post no guarda la razón, pero el repositorio lo actualiza a REJECTED
            repository.updatePostStatus(postId, PostStatus.REJECTED)
        }
    }

    fun resolvePost(postId: String) {
        viewModelScope.launch {
            // Asumimos 'RESOLVED' es similar a APPROVED o una fase final de verificado
            repository.updatePostStatus(postId, PostStatus.APPROVED)
        }
    }

    suspend fun getPostById(postId: String): Post? {
        return repository.getPostById(postId)
    }
}
