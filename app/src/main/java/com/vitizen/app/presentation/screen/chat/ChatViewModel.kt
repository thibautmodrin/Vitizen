package com.vitizen.app.presentation.screen.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.data.remote.websocket.WebSocketMessage
import com.vitizen.app.domain.model.ChatMessage
import com.vitizen.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendChatMessage: SendChatMessageUseCase,
    private val connectToChat: ConnectToChatUseCase,
    private val disconnectChat: DisconnectChatUseCase,
    private val appendBotMessage: AppendBotMessageUseCase
) : ViewModel() {

    var state by mutableStateOf(ChatState(
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
    ))
        private set

    init {
        connect()
    }

    private fun connect() {
        connectToChat(
            onMessage = { message ->
                viewModelScope.launch {
                    when (message) {
                        is WebSocketMessage.Start -> {
                            appendBotMessage.reset()
                            state = state.copy(isTyping = true, isLoading = true)
                        }
                        is WebSocketMessage.Content -> {
                            if (appendBotMessage.isFirst()) {
                                appendBotMessage.markStarted()
                                val updatedMessages = state.messages + ChatMessage("bot", message.content, isUser = false)
                                state = state.copy(messages = updatedMessages, isTyping = false)
                                appendBotMessage.append(message.content)
                            } else {
                                val newContent = appendBotMessage.append(message.content)
                                updateBotMessage(newContent)
                            }
                        }
                        is WebSocketMessage.End -> {
                            finishTyping()
                        }
                    }
                }
            },
            onError = { error ->
                state = state.copy(error = error, isLoading = false, isTyping = false)
                appendBotMessage.reset()
            }
        )
    }

    private fun updateBotMessage(content: String) {
        val updatedMessages = state.messages.toMutableList()
        if (updatedMessages.isNotEmpty() && !updatedMessages.last().isUser) {
            updatedMessages[updatedMessages.lastIndex] =
                ChatMessage("bot", content, isUser = false)
            state = state.copy(messages = updatedMessages)
        }
    }

    private fun finishTyping() {
        state = state.copy(isLoading = false)
    }

    fun onMessageChanged(text: String) {
        state = state.copy(currentInput = text)
    }

    fun sendMessage() {
        val msg = state.currentInput.trim()
        if (msg.isEmpty()) return

        viewModelScope.launch {
            val updatedMessages = state.messages + ChatMessage("user", msg, isUser = true)
            state = state.copy(messages = updatedMessages, currentInput = "")

            appendBotMessage.reset()

            delay(400)
            state = state.copy(isTyping = true, isLoading = true)

            sendChatMessage(msg)
        }
    }

    fun resetChat() {
        disconnectChat()
        state = state.copy(
            messages = emptyList(),
            currentInput = "",
            isLoading = false,
            error = null,
            isTyping = false
        )
        appendBotMessage.reset()
        connect()
    }

    override fun onCleared() {
        super.onCleared()
        disconnectChat()
    }
}
