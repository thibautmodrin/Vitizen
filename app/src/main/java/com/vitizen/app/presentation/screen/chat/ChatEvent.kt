package com.vitizen.app.presentation.screen.chat

sealed class ChatEvent {
    object Idle : ChatEvent()
    data class MessageSent(val message: String) : ChatEvent()
    data class MessageReceived(val message: String) : ChatEvent()
    data class Error(val message: String) : ChatEvent()
    object ChatReset : ChatEvent()
    object ConnectionEstablished : ChatEvent()
    object ConnectionLost : ChatEvent()
    object TypingStarted : ChatEvent()
    object TypingFinished : ChatEvent()
} 