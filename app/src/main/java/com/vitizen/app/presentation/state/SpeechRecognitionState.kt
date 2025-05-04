package com.vitizen.app.presentation.state

data class SpeechRecognitionState(
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val error: String? = null,
    val confidence: Float = 0f
)

sealed class SpeechRecognitionEvent {
    object StartListening : SpeechRecognitionEvent()
    object StopListening : SpeechRecognitionEvent()
    data class TextRecognized(val text: String, val confidence: Float) : SpeechRecognitionEvent()
    data class Error(val message: String) : SpeechRecognitionEvent()
} 