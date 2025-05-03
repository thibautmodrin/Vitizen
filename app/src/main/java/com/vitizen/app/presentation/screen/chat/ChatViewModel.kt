package com.vitizen.app.presentation.screen.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.domain.model.ChatMessage
import com.vitizen.app.domain.repository.IChatRepository
import com.vitizen.app.data.remote.websocket.WebSocketMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: IChatRepository
) : ViewModel() {

    var state by mutableStateOf(ChatState(
        messages = listOf(
            ChatMessage(
                "bot",
                "Bonjour ! Je suis Vitizen, votre assistant viticole. Je peux vous aider à répondre à vos questions techniques sur :\n\n" +
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

    private var currentBotMessage = StringBuilder()
    private var isFirstContent = true

    init {
        connect()
    }

    private fun connect() {
        chatRepository.connect(
            onMessage = { message ->
                Log.d("ChatVM", "🧠 Message reçu: $message")

                viewModelScope.launch {
                    when (message) {
                        is WebSocketMessage.Start -> {
                            currentBotMessage.clear()
                            isFirstContent = true
                            state = state.copy(
                                isTyping = true,
                                isLoading = true
                            )
                        }
                        is WebSocketMessage.Content -> {
                            if (isFirstContent) {
                                isFirstContent = false
                                state = state.copy(isTyping = false)
                                val updatedMessages = state.messages.toMutableList()
                                updatedMessages.add(ChatMessage("bot", message.content, isUser = false))
                                state = state.copy(messages = updatedMessages)
                                currentBotMessage.append(message.content)
                            } else {
                                currentBotMessage.append(message.content)
                                updateBotMessage(currentBotMessage.toString())
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
                currentBotMessage.clear()
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
            // Afficher d'abord le message de l'utilisateur
            val updatedMessages = state.messages + ChatMessage("user", msg, isUser = true)
            state = state.copy(
                messages = updatedMessages,
                currentInput = ""
            )

            currentBotMessage.clear()

            // Attendre 800ms avant d'afficher l'indicateur de chargement
            delay(400)
            
            // Afficher l'indicateur de chargement
            state = state.copy(
                isTyping = true,
                isLoading = true
            )

            val jsonMessage = JSONObject().apply {
                put("message", msg)
            }.toString()

            chatRepository.sendMessage(jsonMessage)
        }
    }

    fun resetChat() {
        // Déconnecter le WebSocket actuel
        chatRepository.disconnect()
        
        // Réinitialiser l'état
        state = state.copy(
            messages = emptyList(),
            currentInput = "",
            isLoading = false,
            error = null,
            isTyping = false
        )
        currentBotMessage.clear()
        isFirstContent = true
        
        // Reconnecter le WebSocket
        connect()
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.disconnect()
    }
}