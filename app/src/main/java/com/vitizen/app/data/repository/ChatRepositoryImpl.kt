package com.vitizen.app.data.repository

import com.vitizen.app.data.remote.websocket.ChatWebSocketListener
import com.vitizen.app.data.remote.websocket.WebSocketMessage
import com.vitizen.app.domain.repository.IChatRepository
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor() : IChatRepository {

    private var webSocket: WebSocket? = null
    private lateinit var listener: ChatWebSocketListener

    override fun connect(onMessage: (WebSocketMessage) -> Unit, onError: (String) -> Unit) {
        val request = Request.Builder()
            .url("wss://thibautmodrin-vitizen-chat.hf.space/chat/ws")
            .build()
        val client = OkHttpClient.Builder()
//            .pingInterval(0, TimeUnit.SECONDS) // ping régulier pour maintenir la connexion
//            .retryOnConnectionFailure(true)
            .build()

        listener = ChatWebSocketListener(onMessage, onError)
        webSocket = client.newWebSocket(request, listener)
    }

    override fun sendMessage(message: String) {
        // Vérification : message doit déjà être un JSON du type { "message": "..." }
        if (message.trim().startsWith("{")) {
            webSocket?.send(message)
        } else {
            // Auto-formattage en JSON si jamais un string brut est envoyé
            val json = """{ "message": "${message.replace("\"", "\\\"")}" }"""
            webSocket?.send(json)
        }
    }

    override fun disconnect() {
        webSocket?.close(1000, "Closed by client")
    }
}

