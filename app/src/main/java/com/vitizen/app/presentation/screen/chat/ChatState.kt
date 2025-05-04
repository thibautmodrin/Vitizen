package com.vitizen.app.presentation.screen.chat

import com.vitizen.app.domain.model.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val error: String? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected,
    val isConnected: Boolean = false,
    val lastMessageContent: String? = null,
    val isListening: Boolean = false
) {
    companion object {
        val Initial = ChatState()
    }

    sealed class ConnectionStatus {
        object Connected : ConnectionStatus()
        object Connecting : ConnectionStatus()
        object Disconnected : ConnectionStatus()
        data class Error(val message: String) : ConnectionStatus()
    }
}

