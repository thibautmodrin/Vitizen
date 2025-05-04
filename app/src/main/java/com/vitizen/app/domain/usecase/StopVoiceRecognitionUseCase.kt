package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.repository.IVoiceRecognitionRepository
import javax.inject.Inject

class StopVoiceRecognitionUseCase @Inject constructor(
    private val repository: IVoiceRecognitionRepository
) {
    suspend operator fun invoke(): ByteArray {
        return repository.stopRecording()
    }
} 