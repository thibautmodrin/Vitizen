package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.repository.IVoiceRecognitionRepository
import javax.inject.Inject

class TranscribeAudioUseCase @Inject constructor(
    private val repository: IVoiceRecognitionRepository
) {
    suspend operator fun invoke(audioData: ByteArray): String {
        return repository.transcribeAudio(audioData)
    }
} 