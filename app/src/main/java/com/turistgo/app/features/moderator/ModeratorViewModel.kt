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
        val post = posts.value.find { it.id == postId } ?: return
        viewModelScope.launch {
            repository.updatePostStatus(postId, PostStatus.APPROVED)
            
            repository.addNotification(
                com.turistgo.app.domain.model.Notification(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = post.authorId,
                    title = "Publicación Aprobada",
                    message = "¡Tu publicación '${post.name}' ha sido aprobada!",
                    type = com.turistgo.app.domain.model.NotificationType.POST_APPROVED
                )
            )
        }
    }

    fun rejectPost(postId: String, reason: String) {
        val post = posts.value.find { it.id == postId } ?: return
        viewModelScope.launch {
            repository.updatePostStatus(postId, PostStatus.REJECTED)
            
            repository.addNotification(
                com.turistgo.app.domain.model.Notification(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = post.authorId,
                    title = "Publicación Rechazada",
                    message = "Tu publicación '${post.name}' ha sido rechazada. Motivo: $reason",
                    type = com.turistgo.app.domain.model.NotificationType.POST_REJECTED
                )
            )
        }
    }

    fun resolvePost(postId: String) {
        val post = posts.value.find { it.id == postId } ?: return
        viewModelScope.launch {
            repository.updatePostStatus(postId, PostStatus.APPROVED)
            
            repository.addNotification(
                com.turistgo.app.domain.model.Notification(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = post.authorId,
                    title = "Publicación Verificada",
                    message = "Tu publicación '${post.name}' ha sido verificada con éxito.",
                    type = com.turistgo.app.domain.model.NotificationType.POST_APPROVED
                )
            )
        }
    }

    suspend fun getPostById(postId: String): Post? {
        return repository.getPostById(postId)
    }
}
