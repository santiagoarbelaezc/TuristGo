package com.turistgo.app.features.moderator

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PostStatus {
    PENDING, VERIFIED, REJECTED, RESOLVED
}

data class ModeratorPost(
    val id: String,
    val title: String,
    val author: String,
    val date: String,
    val imageUrl: String,
    val status: PostStatus = PostStatus.PENDING,
    val rejectionReason: String? = null
)

@HiltViewModel
class ModeratorViewModel @Inject constructor() : ViewModel() {
    private val _posts = mutableStateListOf(
        ModeratorPost("1", "Santuario de Las Lajas", "Santiago Arbelaez", "26/02/2026", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/iglesia_s29dbh.jpg"),
        ModeratorPost("2", "Parque Tayrona", "Maria Lopez", "25/02/2026", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/tayrona_oim4nu.jpg"),
        ModeratorPost("3", "Piedra del Peñol", "Carlos Ruiz", "24/02/2026", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/pe%C3%B1ol_jlujxo.jpg"),
        ModeratorPost("4", "Nevado del Ruiz", "Ana Gomez", "23/02/2026", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/nevadoruiz_rc301x.jpg")
    )
    val posts: List<ModeratorPost> = _posts

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun approvePost(postId: String) {
        val index = _posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            _posts[index] = _posts[index].copy(status = PostStatus.VERIFIED)
        }
    }

    fun rejectPost(postId: String, reason: String) {
        val index = _posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            _posts[index] = _posts[index].copy(status = PostStatus.REJECTED, rejectionReason = reason)
        }
    }

    fun resolvePost(postId: String) {
        val index = _posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            _posts[index] = _posts[index].copy(status = PostStatus.RESOLVED)
        }
    }

    fun getPostById(postId: String): ModeratorPost? {
        return _posts.find { it.id == postId }
    }
}
