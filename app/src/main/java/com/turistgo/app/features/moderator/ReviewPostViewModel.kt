package com.turistgo.app.features.moderator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.PostStatus
import com.turistgo.app.domain.model.User
import com.turistgo.app.data.GeminiService
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewPostViewModel @Inject constructor(
    private val repository: AppDataRepository
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()
    
    private val _author = MutableStateFlow<User?>(null)
    val author: StateFlow<User?> = _author.asStateFlow()

    private val _aiAnalysis = MutableStateFlow<String?>(null)
    val aiAnalysis: StateFlow<String?> = _aiAnalysis.asStateFlow()

    private val _isAiAnalyzing = MutableStateFlow(false)
    val isAiAnalyzing: StateFlow<Boolean> = _isAiAnalyzing.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            val loadedPost = repository.getPostById(postId)
            _post.value = loadedPost
            
            if (loadedPost != null) {
                // 1. Fetch Author Details
                _author.value = repository.getUserById(loadedPost.authorId)
                
                // 2. Trigger AI Analysis
                analyzePostWithAi(loadedPost)
            }
        }
    }

    private fun analyzePostWithAi(post: Post) {
        viewModelScope.launch {
            _isAiAnalyzing.value = true
            try {
                val analysis = GeminiService.generateModeratorSummary(
                    title = post.name,
                    description = post.description,
                    category = post.categories.firstOrNull() ?: "General",
                    author = post.authorName
                )
                _aiAnalysis.value = analysis
            } catch (e: Exception) {
                _aiAnalysis.value = "Error al generar análisis: ${e.message}"
            } finally {
                _isAiAnalyzing.value = false
            }
        }
    }

    fun approvePost(onSuccess: () -> Unit) {
        val post = _post.value ?: return
        viewModelScope.launch {
            repository.updatePostStatus(post.id, PostStatus.APPROVED)
            
            // Notification for author
            repository.addNotification(
                com.turistgo.app.domain.model.Notification(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = post.authorId,
                    title = "Publicación Aprobada",
                    message = "¡Tu publicación '${post.name}' ha sido aprobada y ya está visible para todos!",
                    type = com.turistgo.app.domain.model.NotificationType.POST_APPROVED
                )
            )
            onSuccess()
        }
    }

    fun rejectPost(onSuccess: () -> Unit) {
        val post = _post.value ?: return
        viewModelScope.launch {
            repository.updatePostStatus(post.id, PostStatus.REJECTED)
            
            // Notification for author
            repository.addNotification(
                com.turistgo.app.domain.model.Notification(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = post.authorId,
                    title = "Publicación Rechazada",
                    message = "Lo sentimos, tu publicación '${post.name}' no cumple con nuestras normas y ha sido rechazada.",
                    type = com.turistgo.app.domain.model.NotificationType.POST_REJECTED
                )
            )
            onSuccess()
        }
    }
}
