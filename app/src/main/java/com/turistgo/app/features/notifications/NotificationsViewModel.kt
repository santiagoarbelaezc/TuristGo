package com.turistgo.app.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.domain.model.Notification
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifications: StateFlow<List<Notification>> = sessionManager.userSession
        .flatMapLatest { session ->
            val userId = session.userId
            if (userId != null) repository.getNotifications(userId)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val unreadCount: StateFlow<Int> = notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _navigationEvent = MutableSharedFlow<NotificationNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onNotificationClick(notification: Notification) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notification.id)
            
            // Navigate based on type
            when (notification.type) {
                com.turistgo.app.domain.model.NotificationType.COMMENT -> {
                    notification.postId?.let {
                        _navigationEvent.emit(NotificationNavigationEvent.ToPostDetail(it))
                    }
                }
                com.turistgo.app.domain.model.NotificationType.NEW_POST -> {
                    notification.postId?.let {
                        _navigationEvent.emit(NotificationNavigationEvent.ToPostDetail(it))
                    }
                }
                com.turistgo.app.domain.model.NotificationType.FOLLOW_REQUEST,
                com.turistgo.app.domain.model.NotificationType.FOLLOW_ACCEPTED -> {
                    notification.senderId?.let {
                        _navigationEvent.emit(NotificationNavigationEvent.ToUserProfile(it))
                    }
                }
                else -> { /* Other types may not have specific navigation */ }
            }
        }
    }

    fun acceptFollowRequest(notificationId: String) {
        viewModelScope.launch {
            repository.handleFollowRequest(notificationId, accepted = true)
        }
    }

    fun rejectFollowRequest(notificationId: String) {
        viewModelScope.launch {
            repository.handleFollowRequest(notificationId, accepted = false)
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val current = notifications.value
            current.forEach { 
                if (!it.isRead) repository.markNotificationAsRead(it.id)
            }
        }
    }
}

sealed class NotificationNavigationEvent {
    data class ToPostDetail(val postId: String) : NotificationNavigationEvent()
    data class ToUserProfile(val userId: String) : NotificationNavigationEvent()
}
