package com.turistgo.app.di

import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.data.repository.InMemoryRepository
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.turistgo.app.data.repository.InMemoryChatRepository
import com.turistgo.app.domain.repository.ChatRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAppDataRepository(
        inMemoryRepository: InMemoryRepository
    ): AppDataRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        inMemoryChatRepository: InMemoryChatRepository
    ): ChatRepository
}
