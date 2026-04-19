package com.turistgo.app.domain.repository

import com.turistgo.app.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(): Flow<List<ChatMessage>>
    suspend fun saveMessages(messages: List<ChatMessage>)
    suspend fun clearMessages()
}
