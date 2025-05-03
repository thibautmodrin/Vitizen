package com.vitizen.app.domain.usecase

import javax.inject.Inject

class AppendBotMessageUseCase @Inject constructor() {

    private val buffer = StringBuilder()
    private var isFirstChunk = true

    fun reset() {
        buffer.clear()
        isFirstChunk = true
    }

    fun isFirst(): Boolean = isFirstChunk

    fun markStarted() {
        isFirstChunk = false
    }

    fun append(content: String): String {
        buffer.append(content)
        return buffer.toString()
    }

    fun current(): String = buffer.toString()
}
