package com.turistgo.app.features.feed

import androidx.lifecycle.ViewModel
import com.turistgo.app.data.datastore.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val sessionManager: UserSessionManager
) : ViewModel() {
    val userSession = sessionManager.userSession
}
