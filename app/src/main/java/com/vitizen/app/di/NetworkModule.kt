package com.vitizen.app.di

import com.vitizen.app.data.remote.service.ChatService
import com.vitizen.app.data.repository.ChatRepositoryImpl
import com.vitizen.app.domain.repository.IChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideChatService(): ChatService {
        return Retrofit.Builder()
            .baseUrl("https://thibautmodrin-vitizen-chat.hf.space/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatService: ChatService): IChatRepository {
        return ChatRepositoryImpl(chatService)
    }
}