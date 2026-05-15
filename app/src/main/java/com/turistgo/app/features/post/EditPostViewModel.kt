package com.turistgo.app.features.post

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostEditState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val category: String = "General",
    val imageUrl: String = ""
)

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val repository: AppDataRepository
) : ViewModel() {
    private val _uiState = mutableStateOf(PostEditState())
    val uiState: State<PostEditState> = _uiState
    private var currentPostId: String? = null

    fun loadPost(postId: String?) {
        if (postId == null || postId == currentPostId) return
        currentPostId = postId
        
        viewModelScope.launch {
            val post = repository.getPostById(postId)
            post?.let {
                _uiState.value = PostEditState(
                    title = it.name,
                    description = it.description,
                    location = it.location,
                    category = it.categories.firstOrNull() ?: "General",
                    imageUrl = it.imageUrl
                )
            }
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        val id = currentPostId ?: return
        viewModelScope.launch {
            val post = repository.getPostById(id)
            post?.let {
                repository.savePost(it.copy(
                    name = _uiState.value.title,
                    description = _uiState.value.description,
                    location = _uiState.value.location,
                    categories = listOf(_uiState.value.category)
                ))
                onSuccess()
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle)
    }

    fun updateDescription(newDescription: String) {
        _uiState.value = _uiState.value.copy(description = newDescription)
    }

    fun updateLocation(newLocation: String) {
        _uiState.value = _uiState.value.copy(location = newLocation)
    }

    fun updateCategory(newCategory: String) {
        _uiState.value = _uiState.value.copy(category = newCategory)
    }
}
