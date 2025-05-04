package com.vitizen.app.data.repository

import com.vitizen.app.data.remote.speech.VoskSpeechRecognizer
import com.vitizen.app.domain.model.SpeechRecognitionModel
import com.vitizen.app.domain.repository.ISpeechRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SpeechRepositoryImpl @Inject constructor(
    private val voskRecognizer: VoskSpeechRecognizer,
    private val coroutineScope: CoroutineScope
) : ISpeechRepository {

    override suspend fun initialize() {
        voskRecognizer.initialize()
    }

    override suspend fun startRecognition() {
        voskRecognizer.startRecognition()
    }

    override suspend fun stopRecognition() {
        voskRecognizer.stopRecognition()
    }

    override fun getRecognitionResults(): Flow<SpeechRecognitionModel> {
        return voskRecognizer.recognitionResults.map { result ->
            SpeechRecognitionModel(
                text = result.text,
                confidence = result.confidence,
                isFinal = result.isFinal
            )
        }
    }

    override fun isListening(): Flow<Boolean> {
        return voskRecognizer.isListening
    }

    override suspend fun release() {
        voskRecognizer.release()
    }
} 