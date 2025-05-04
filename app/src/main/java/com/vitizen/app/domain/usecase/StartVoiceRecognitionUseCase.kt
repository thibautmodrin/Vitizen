package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.repository.IVoiceRecognitionRepository
import javax.inject.Inject

class StartVoiceRecognitionUseCase @Inject constructor(
    private val repository: IVoiceRecognitionRepository
) {
    suspend operator fun invoke() {
        if (!repository.checkPermissions()) {
            throw SecurityException("Permission d'enregistrement audio non accordée")
        }
        repository.startRecording()
    }
} 