package com.vitizen.app.data.repository

import com.vitizen.app.data.remote.dto.ChatRequest
import com.vitizen.app.data.remote.dto.Input
import com.vitizen.app.data.remote.service.ChatService
import com.vitizen.app.domain.model.ChatMessage
import com.vitizen.app.domain.repository.IChatRepository
import javax.inject.Inject


class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService
) : IChatRepository {

    override suspend fun sendMessage(message: String): Result<ChatMessage> {
        return try {
            val request = ChatRequest(Input(message))
            val response = chatService.sendMessage(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Réponse vide du serveur"))
            } else {
                Result.failure(Exception("Erreur ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
