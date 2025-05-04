package com.vitizen.app.data.remote.dto

data class WhisperRequest(
    val audio: ByteArray,
    val model: String = "whisper-1",
    val language: String = "fr"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WhisperRequest

        if (!audio.contentEquals(other.audio)) return false
        if (model != other.model) return false
        if (language != other.language) return false

        return true
    }

    override fun hashCode(): Int {
        var result = audio.contentHashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + language.hashCode()
        return result
    }
} 