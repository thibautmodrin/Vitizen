package com.vitizen.app.data.remote.dto

data class WhisperResponse(
    val text: String,
    val language: String,
    val duration: Double? = null,
    val segments: List<WhisperSegment>? = null,
    val words: List<WhisperWord>? = null
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