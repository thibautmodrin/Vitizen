package com.vitizen.app.domain.repository

import com.vitizen.app.domain.model.ChatMessage

interface IChatRepository {
    suspend fun sendMessage(message: String): Result<ChatMessage>
}
