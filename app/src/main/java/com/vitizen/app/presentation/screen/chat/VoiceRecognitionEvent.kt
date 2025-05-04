package com.vitizen.app.presentation.screen.chat

sealed class VoiceRecognitionEvent {
    object RecordingStarted : VoiceRecognitionEvent()
    object RecordingStopped : VoiceRecognitionEvent()
    data class TranscriptionComplete(
        val text: String,
        val segments: List<WhisperSegment>? = null,
        val words: List<WhisperWord>? = null,
        val confidence: Double? = null
    ) : VoiceRecognitionEvent()
    data class Error(val message: String) : VoiceRecognitionEvent()
    data class AudioLevelUpdated(val level: Float) : VoiceRecognitionEvent()
    data class PermissionResult(val granted: Boolean) : VoiceRecognitionEvent()
    data class ProcessingProgress(val progress: Float) : VoiceRecognitionEvent()
}

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