package com.vitizen.app.di

import android.content.Context
import com.vitizen.app.data.remote.api.WhisperApi
import com.vitizen.app.data.repository.VoiceRecognitionRepositoryImpl
import com.vitizen.app.domain.repository.IVoiceRecognitionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VoiceRecognitionModule {

    @Provides
    @Singleton
    fun provideWhisperApi(okHttpClient: OkHttpClient): WhisperApi {
        println("Configuration de l'API Whisper...")
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        println("API Whisper configurée avec succès")
        return retrofit.create(WhisperApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVoiceRecognitionRepository(
        whisperApi: WhisperApi,
        @ApplicationContext context: Context
    ): IVoiceRecognitionRepository {
        println("Création du repository de reconnaissance vocale...")
        return VoiceRecognitionRepositoryImpl(whisperApi, context)
    }
} 