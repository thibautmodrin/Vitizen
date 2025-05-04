package com.vitizen.app.presentation.screen.chat

import com.vitizen.app.domain.model.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isTyping: Boolean = false,
    val isConnected: Boolean = false,
    val lastMessageContent: String? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected
) {
    sealed class ConnectionStatus {
        object Connected : ConnectionStatus()
        object Disconnected : ConnectionStatus()
        object Connecting : ConnectionStatus()
        data class Error(val message: String) : ConnectionStatus()
    }

    companion object {
        val Initial = ChatState(
            messages = listOf(
                ChatMessage(
                    message = "Bonjour ! Je suis Vitizen, votre assistant viticole. Je peux vous aider à répondre à vos questions techniques sur :\n\n" +
                            "• Les traitements phytosanitaires\n" +
                            "• Le matériel et les équipements\n" +
                            "• Les produits et leur utilisation\n" +
                            "• Les réglages et la maintenance\n" +
                            "• Les bonnes pratiques viticoles\n\n" +
                            "N'hésitez pas à me poser vos questions !",
                    isUser = false
                )
            )
        )
    }
}

