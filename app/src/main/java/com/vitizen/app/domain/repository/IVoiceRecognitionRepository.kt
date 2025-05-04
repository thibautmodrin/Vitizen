package com.vitizen.app.domain.repository

interface IVoiceRecognitionRepository {
    suspend fun startRecording()
    suspend fun stopRecording(): ByteArray
    suspend fun transcribeAudio(audioData: ByteArray): String
    fun isRecording(): Boolean
    fun getAudioLevel(): Float
    suspend fun checkPermissions(): Boolean
} 