package com.vitizen.app.data.repository

import com.vitizen.app.data.remote.websocket.ChatWebSocketListener
import com.vitizen.app.data.remote.websocket.WebSocketMessage
import com.vitizen.app.domain.repository.IChatRepository
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.util.Log

class ChatRepositoryImpl @Inject constructor() : IChatRepository {

    private var webSocket: WebSocket? = null
    private lateinit var listener: ChatWebSocketListener
    private var reconnectAttempts = 0
    private val MAX_RECONNECT_ATTEMPTS = 3
    private val RECONNECT_DELAY = 2000L // 2 secondes

    override fun connect(onMessage: (WebSocketMessage) -> Unit, onError: (String) -> Unit) {
        val request = Request.Builder()
            .url("wss://thibautmodrin-vitizen-chat.hf.space/chat/ws")
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .pingInterval(15, TimeUnit.SECONDS) // Ping toutes les 15 secondes
            .retryOnConnectionFailure(true)
            .build()

        listener = ChatWebSocketListener(
            onMessage = onMessage,
            onError = { error ->
                if (error.contains("timeout") && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    reconnectAttempts++
                    Log.w("WebSocket", "Tentative de reconnexion $reconnectAttempts/$MAX_RECONNECT_ATTEMPTS")
                    Thread.sleep(RECONNECT_DELAY)
                    connect(onMessage, onError)
                } else {
                    onError(error)
                }
            }
        )
        webSocket = client.newWebSocket(request, listener)
    }

    override fun sendMessage(message: String) {
        if (webSocket == null) {
            Log.e("WebSocket", "Tentative d'envoi de message sans connexion active")
            return
        }
        
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
        reconnectAttempts = 0
        webSocket?.close(1000, "Closed by client")
        webSocket = null
    }
}

