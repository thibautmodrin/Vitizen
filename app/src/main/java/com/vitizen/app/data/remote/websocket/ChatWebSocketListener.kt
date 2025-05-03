package com.vitizen.app.data.remote.websocket

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed class WebSocketMessage {
    data class Start(val message: String = "") : WebSocketMessage()
    data class Content(val content: String) : WebSocketMessage()
    data class End(val message: String = "") : WebSocketMessage()
    data class ConnectionEstablished(val response: Response) : WebSocketMessage()
    data class ConnectionClosed(val code: Int, val reason: String) : WebSocketMessage()
    data class ConnectionError(val error: String) : WebSocketMessage()
}

class ChatWebSocketListener(
    private val onMessage: (WebSocketMessage) -> Unit,
    private val onError: (String) -> Unit
) : WebSocketListener() {

    private val buffer = StringBuilder()
    private var isReceivingMessage = false
    private var lastSendTime = System.currentTimeMillis()
    private val SEND_INTERVAL = 50L // Réduit à 50ms pour un défilement plus fluide

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.i("WebSocket", "✅ Connexion WebSocket ouverte")
        onMessage(WebSocketMessage.ConnectionEstablished(response))
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "📥 Message reçu: $text")

        try {
            when (text.trim()) {
                "[START]" -> {
                    Log.d("WebSocket", "🚀 Début du message détecté")
                    isReceivingMessage = true
                    buffer.clear()
                    lastSendTime = System.currentTimeMillis()
                    onMessage(WebSocketMessage.Start())
                }
                "[STOP]" -> {
                    Log.d("WebSocket", "🛑 Fin du message détectée")
                    if (isReceivingMessage) {
                        val finalContent = buffer.toString().trim()
                        if (finalContent.isNotEmpty()) {
                            Log.d("WebSocket", "📤 Envoi du contenu final: $finalContent")
                            onMessage(WebSocketMessage.Content(finalContent))
                        }
                        onMessage(WebSocketMessage.End())
                        buffer.clear()
                        isReceivingMessage = false
                    }
                }
                else -> {
                    if (isReceivingMessage && text.isNotBlank()) {
                        Log.d("WebSocket", "📝 Ajout au buffer: $text")
                        buffer.append(text)
                        
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastSendTime >= SEND_INTERVAL) {
                            val content = buffer.toString()
                            if (content.isNotEmpty()) {
                                Log.d("WebSocket", "📤 Envoi du contenu accumulé: $content")
                                onMessage(WebSocketMessage.Content(content))
                                buffer.clear()
                                lastSendTime = currentTime
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "❌ Erreur lors du traitement du message", e)
            onError("Erreur lors du traitement du message: ${e.localizedMessage}")
            buffer.clear()
            isReceivingMessage = false
            onMessage(WebSocketMessage.End())
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.w("WebSocket", "🛑 Fermeture WebSocket en cours: $reason ($code)")
        if (isReceivingMessage) {
            onMessage(WebSocketMessage.End())
            isReceivingMessage = false
        }
        onMessage(WebSocketMessage.ConnectionClosed(code, reason))
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.i("WebSocket", "✅ WebSocket fermée proprement: $reason ($code)")
        onMessage(WebSocketMessage.ConnectionClosed(code, reason))
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        val errorMsg = "❌ Erreur WebSocket: ${t.localizedMessage}"
        Log.e("WebSocket", errorMsg, t)
        onError(errorMsg)
        if (isReceivingMessage) {
            onMessage(WebSocketMessage.End())
            isReceivingMessage = false
        }
        onMessage(WebSocketMessage.ConnectionError(errorMsg))
    }
}
