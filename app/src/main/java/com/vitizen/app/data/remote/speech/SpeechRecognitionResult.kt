package com.vitizen.app.data.remote.speech

data class SpeechRecognitionResult(
    val text: String,
    val isFinal: Boolean,
    val confidence: Float,
    val error: String? = null
) 