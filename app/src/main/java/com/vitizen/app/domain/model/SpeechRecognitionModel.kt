package com.vitizen.app.domain.model

data class SpeechRecognitionModel(
    val text: String,
    val confidence: Float,
    val isFinal: Boolean
) 