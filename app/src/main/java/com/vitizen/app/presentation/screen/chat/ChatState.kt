package com.vitizen.app.presentation.screen.chat

import com.vitizen.app.domain.model.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isTyping: Boolean = false  // 👈 ajout
)

