package com.turistgo.app.data.repository

import com.turistgo.app.domain.model.ChatMessage
import com.turistgo.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryChatRepository @Inject constructor() : ChatRepository {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    
    override fun getMessages(): Flow<List<ChatMessage>> = _messages
    
    override suspend fun saveMessages(messages: List<ChatMessage>) {
        _messages.value = messages
    }
    
    override suspend fun clearMessages() {
        _messages.value = emptyList()
    }
}
