package com.turistgo.app.di

import com.turistgo.app.data.repository.FirestoreRepository
import com.turistgo.app.data.repository.FirestoreChatRepository
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAppDataRepository(
        firestoreRepository: FirestoreRepository
    ): AppDataRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        firestoreChatRepository: FirestoreChatRepository
    ): ChatRepository
}
