package com.aps.geetai.di

import com.aps.geetai.repository.ChatRepository
import com.aps.geetai.repository.ChatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DI LAYER
 *
 * Tells Hilt: "whenever someone asks for ChatRepository, inject ChatRepositoryImpl."
 * Nothing else lives here — keep DI modules thin.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository
}