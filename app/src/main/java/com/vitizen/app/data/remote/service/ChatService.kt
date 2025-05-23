package com.vitizen.app.data.remote.service

import com.vitizen.app.data.remote.dto.ChatRequest
import com.vitizen.app.data.remote.dto.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatService {
    @POST("chat/invoke")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>
}