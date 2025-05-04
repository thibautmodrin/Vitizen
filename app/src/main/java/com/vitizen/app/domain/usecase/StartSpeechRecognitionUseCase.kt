package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.repository.ISpeechRepository
import javax.inject.Inject

class StartSpeechRecognitionUseCase @Inject constructor(
    private val speechRepository: ISpeechRepository
) {
    suspend operator fun invoke() {
        speechRepository.startRecognition()
    }
} 