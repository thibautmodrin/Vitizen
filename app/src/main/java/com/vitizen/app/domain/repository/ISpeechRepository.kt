package com.vitizen.app.domain.repository

import com.vitizen.app.domain.model.SpeechRecognitionModel
import kotlinx.coroutines.flow.Flow

interface ISpeechRepository {
    suspend fun initialize()
    suspend fun startRecognition()
    suspend fun stopRecognition()
    fun getRecognitionResults(): Flow<SpeechRecognitionModel>
    fun isListening(): Flow<Boolean>
    suspend fun release()
} 