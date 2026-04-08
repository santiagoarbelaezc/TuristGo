package com.turistgo.app.features.moderator

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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

class ModeratorViewModel : ViewModel() {
    private val _posts = mutableStateListOf(
        ModeratorPost("1", "Aventura en el Cañón del Chicamocha", "Santiago Arbelaez", "26/02/2026", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/celebre-la-semana-santa-en-estos-cuatro-lugares-turisticos-de-colombia-1229852_ckbgrw.jpg"),
        ModeratorPost("2", "Cascada Escondida", "Maria Lopez", "25/02/2026", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/destinos-naturales-en-colombia-sin-turismo-masivo_ei0akp.jpg"),
        ModeratorPost("3", "Restaurante Típico", "Carlos Ruiz", "24/02/2026", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/SL3RJGIFWRCQDGAMA2XYX4QYRQ_dtneeb.jpg"),
        ModeratorPost("4", "Aventura en el Nevado", "Ana Gomez", "23/02/2026", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036016/Nevado_del_Ruiz_by_Edgar_mi099q.png")
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
