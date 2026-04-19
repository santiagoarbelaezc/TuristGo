package com.turistgo.app.features.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.Comment
import com.turistgo.app.domain.model.Notification
import com.turistgo.app.domain.model.NotificationType
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.turistgo.app.data.datastore.UserSessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val repository: AppDataRepository
) : ViewModel() {
    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val userSession = sessionManager.userSession

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentUser = userSession.flatMapLatest { session ->
        val userId = session.userId
        if (userId != null) {
            repository.getUsers().map { users -> users.find { it.id == userId } }
        } else flowOf(null)
    }

    val isSaved = combine(_post, currentUser) { post, user ->
        post != null && user?.savedPostIds?.contains(post.id) == true
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isLiked = combine(_post, currentUser) { post, user ->
        post != null && user?.likedPostIds?.contains(post.id) == true
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val comments = _post.flatMapLatest { post ->
        if (post != null) repository.getComments(post.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _post.value = repository.getPostById(postId)
        }
    }

    fun toggleSave() {
        val postId = _post.value?.id ?: return
        viewModelScope.launch {
            val userId = sessionManager.userSession.first().userId ?: return@launch
            repository.toggleSavedPost(userId, postId)
        }
    }

    fun toggleLike() {
        val postId = _post.value?.id ?: return
        viewModelScope.launch {
            val userId = sessionManager.userSession.first().userId ?: return@launch
            repository.toggleLikedPost(userId, postId)
        }
    }

    fun addComment(content: String) {
        val post = _post.value ?: return
        viewModelScope.launch {
            val session = sessionManager.userSession.firstOrNull() ?: return@launch
            val userId = session.userId ?: return@launch
            val userName = session.name ?: "Usuario"

            val newComment = Comment(
                id = java.util.UUID.randomUUID().toString(),
                postId = post.id,
                authorId = userId,
                authorName = userName,
                authorPhotoUrl = session.photoUrl,
                content = content
            )
            repository.addComment(newComment)

            // Trigger Notification for author (if not same person)
            if (post.authorId != userId) {
                repository.addNotification(
                    Notification(
                        id = java.util.UUID.randomUUID().toString(),
                        userId = post.authorId,
                        title = "Nuevo comentario",
                        message = "$userName ha comentado en tu publicación: ${post.name}",
                        type = NotificationType.COMMENT
                    )
                )
            }

            // Trigger Confirmation Notification for commenter (as requested by user)
            repository.addNotification(
                Notification(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    title = "Comentario enviado",
                    message = "Has comentado en la publicación: ${post.name}",
                    type = NotificationType.COMMENT
                )
            )
        }
    }
}
