package com.vitizen.app.di

import android.content.Context
import com.vitizen.app.data.remote.speech.VoskSpeechRecognizer
import com.vitizen.app.data.repository.SpeechRepositoryImpl
import com.vitizen.app.domain.repository.ISpeechRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

    @Provides
    @Singleton
    fun provideVoskSpeechRecognizer(
        @ApplicationContext context: Context,
        coroutineScope: CoroutineScope
    ): VoskSpeechRecognizer {
        return VoskSpeechRecognizer(context, coroutineScope)
    }

    @Provides
    @Singleton
    fun provideSpeechRepository(
        voskRecognizer: VoskSpeechRecognizer,
        coroutineScope: CoroutineScope
    ): ISpeechRepository {
        return SpeechRepositoryImpl(voskRecognizer, coroutineScope)
    }
} 