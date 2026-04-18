package com.turistgo.app.features.profile

import androidx.lifecycle.ViewModel
import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val repository: AppDataRepository
) : ViewModel() {
    val userSession = sessionManager.userSession

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile: Flow<User?> = userSession.flatMapLatest { session ->
        val userId = session.userId
        if (userId != null) {
            flow {
                emit(repository.getUserById(userId))
            }
        } else {
            flowOf(null)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val myPosts = userSession.flatMapLatest { session ->
        val userId = session.userId
        if (userId != null) {
            repository.getPostsByAuthor(userId)
        } else {
            flowOf(emptyList())
        }
    }
}
