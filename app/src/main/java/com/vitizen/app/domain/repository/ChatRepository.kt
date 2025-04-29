package com.vitizen.app.domain.repository

import android.util.Log
import com.vitizen.app.data.remote.service.ChatService
import com.vitizen.app.domain.model.ChatMessage
import com.vitizen.app.data.remote.dto.ChatRequest
import com.vitizen.app.data.remote.dto.Input
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor() {
    private val TAG = "ChatRepository"
    private val BASE_URL = "https://thibautmodrin-vitizen-chat.hf.space/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val chatService = retrofit.create(ChatService::class.java)

    suspend fun sendMessage(message: String): Result<ChatMessage> {
        return try {
            Log.d(TAG, "Envoi du message: $message")
            Log.d(TAG, "URL de l'API: ${BASE_URL}chat")

            val request = ChatRequest(Input(message))
            Log.d(TAG, "Corps de la requête: $request")

            val response = chatService.sendMessage(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Réponse vide du serveur"))
            } else {
                Result.failure(Exception("Erreur ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception lors de l'envoi du message", e)
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }
}