package com.vitizen.app.domain.repository

import com.vitizen.app.data.remote.websocket.WebSocketMessage

interface IChatRepository {
    fun connect(onMessage: (WebSocketMessage) -> Unit, onError: (String) -> Unit)
    fun sendMessage(message: String)
    fun disconnect()
}

