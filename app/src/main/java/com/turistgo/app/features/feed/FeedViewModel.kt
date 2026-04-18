package com.turistgo.app.features.feed

import androidx.lifecycle.ViewModel
import com.turistgo.app.data.datastore.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val sessionManager: com.turistgo.app.data.datastore.UserSessionManager,
    private val repository: com.turistgo.app.domain.repository.AppDataRepository
) : ViewModel() {
    val userSession = sessionManager.userSession
    val destinations = repository.getPosts(com.turistgo.app.domain.model.PostStatus.APPROVED)
}
