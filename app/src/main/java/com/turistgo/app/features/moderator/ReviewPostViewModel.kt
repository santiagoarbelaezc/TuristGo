package com.turistgo.app.features.moderator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.PostStatus
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

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _post.value = repository.getPostById(postId)
        }
    }

    fun approvePost(onSuccess: () -> Unit) {
        val postId = _post.value?.id ?: return
        viewModelScope.launch {
            repository.updatePostStatus(postId, PostStatus.APPROVED)
            onSuccess()
        }
    }

    fun rejectPost(onSuccess: () -> Unit) {
        val postId = _post.value?.id ?: return
        viewModelScope.launch {
            repository.updatePostStatus(postId, PostStatus.REJECTED)
            onSuccess()
        }
    }
}
