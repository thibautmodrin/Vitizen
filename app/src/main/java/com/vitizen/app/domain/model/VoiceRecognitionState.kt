package com.vitizen.app.domain.model

data class VoiceRecognitionState(
    val isRecording: Boolean = false,
    val transcribedText: String? = null,
    val error: String? = null,
    val isProcessing: Boolean = false,
    val audioLevel: Float = 0f,
    val permissionGranted: Boolean = false,
    val segments: List<WhisperSegment>? = null,
    val words: List<WhisperWord>? = null,
    val confidence: Double? = null
)

data class WhisperSegment(
    val id: Int,
    val start: Double,
    val end: Double,
    val text: String,
    val confidence: Double,
    val words: List<WhisperWord>? = null
)

data class WhisperWord(
    val word: String,
    val start: Double,
    val end: Double,
    val confidence: Double
) 