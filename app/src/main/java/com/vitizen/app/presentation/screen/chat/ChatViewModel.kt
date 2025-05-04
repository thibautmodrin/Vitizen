package com.vitizen.app.presentation.screen.chat

import androidx.lifecycle.viewModelScope
import com.vitizen.app.data.remote.websocket.WebSocketMessage
import com.vitizen.app.domain.model.ChatMessage
import com.vitizen.app.domain.model.SpeechRecognitionModel
import com.vitizen.app.domain.repository.ISpeechRepository
import com.vitizen.app.domain.usecase.*
import com.vitizen.app.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendChatMessage: SendChatMessageUseCase,
    private val connectToChat: ConnectToChatUseCase,
    private val disconnectChat: DisconnectChatUseCase,
    private val appendBotMessage: AppendBotMessageUseCase,
    private val startSpeechRecognition: StartSpeechRecognitionUseCase,
    private val stopSpeechRecognition: StopSpeechRecognitionUseCase,
    private val speechRepository: ISpeechRepository
) : BaseViewModel<ChatState, ChatEvent>() {

    private val _state = MutableStateFlow(ChatState.Initial)
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        connect()
        observeSpeechRecognition()
    }

    private fun observeSpeechRecognition() {
        viewModelScope.launch {
            speechRepository.getRecognitionResults().collect { result ->
                if (result.isFinal && result.text.isNotBlank()) {
                    _state.update { it.copy(currentInput = result.text) }
                    sendMessage()
                }
            }
        }

        viewModelScope.launch {
            speechRepository.isListening().collect { isListening ->
                _state.update { it.copy(isListening = isListening) }
            }
        }
    }

    fun toggleVoiceInput() {
        viewModelScope.launch {
            if (_state.value.isListening) {
                stopSpeechRecognition()
            } else {
                startSpeechRecognition()
            }
        }
    }

    private fun connect() {
        setState(ChatState.Initial.copy(connectionStatus = ChatState.ConnectionStatus.Connecting))
        
        connectToChat(
            onMessage = { message ->
                viewModelScope.launch {
                    when (message) {
                        is WebSocketMessage.Start -> {
                            appendBotMessage.reset()
                            _state.update { it.copy(
                                isTyping = true,
                                isLoading = true,
                                connectionStatus = ChatState.ConnectionStatus.Connected
                            )}
                            setEvent(ChatEvent.TypingStarted)
                        }
                        is WebSocketMessage.Content -> {
                            if (appendBotMessage.isFirst()) {
                                appendBotMessage.markStarted()
                                val updatedMessages = _state.value.messages + ChatMessage(
                                    message = message.content,
                                    isUser = false
                                )
                                _state.update { it.copy(
                                    messages = updatedMessages,
                                    isTyping = false,
                                    lastMessageContent = message.content
                                )}
                                appendBotMessage.append(message.content)
                                setEvent(ChatEvent.MessageReceived(message.content))
                            } else {
                                val newContent = appendBotMessage.append(message.content)
                                updateBotMessage(newContent)
                            }
                        }
                        is WebSocketMessage.End -> {
                            finishTyping()
                        }
                        is WebSocketMessage.ConnectionEstablished -> {
                            _state.update { it.copy(
                                connectionStatus = ChatState.ConnectionStatus.Connected,
                                isConnected = true
                            )}
                            setEvent(ChatEvent.ConnectionEstablished)
                        }
                        is WebSocketMessage.ConnectionClosed -> {
                            _state.update { it.copy(
                                connectionStatus = ChatState.ConnectionStatus.Disconnected,
                                isConnected = false,
                                isTyping = false,
                                isLoading = false
                            )}
                            setEvent(ChatEvent.ConnectionLost)
                        }
                        is WebSocketMessage.ConnectionError -> {
                            _state.update { it.copy(
                                connectionStatus = ChatState.ConnectionStatus.Error(message.error),
                                isConnected = false,
                                isTyping = false,
                                isLoading = false,
                                error = message.error
                            )}
                            setEvent(ChatEvent.Error(message.error))
                        }
                    }
                }
            },
            onError = { error ->
                _state.update { it.copy(
                    error = error,
                    isLoading = false,
                    isTyping = false,
                    connectionStatus = ChatState.ConnectionStatus.Error(error)
                )}
                setEvent(ChatEvent.Error(error))
                appendBotMessage.reset()
            }
        )
    }

    private fun updateBotMessage(content: String) {
        _state.update { currentState ->
            val updatedMessages = currentState.messages.toMutableList()
            if (updatedMessages.isNotEmpty() && !updatedMessages.last().isUser) {
                updatedMessages[updatedMessages.lastIndex] = ChatMessage(
                    message = content,
                    isUser = false
                )
                currentState.copy(
                    messages = updatedMessages,
                    lastMessageContent = content
                )
            } else {
                currentState
            }
        }
    }

    private fun finishTyping() {
        _state.update { it.copy(isLoading = false) }
        setEvent(ChatEvent.TypingFinished)
    }

    fun onMessageChanged(text: String) {
        _state.update { it.copy(currentInput = text) }
    }

    fun sendMessage() {
        val msg = _state.value.currentInput.trim()
        if (msg.isEmpty()) return

        viewModelScope.launch {
            val updatedMessages = _state.value.messages + ChatMessage("user", msg, isUser = true)
            _state.update { it.copy(
                messages = updatedMessages,
                currentInput = "",
                lastMessageContent = msg
            )}

            appendBotMessage.reset()
            setEvent(ChatEvent.MessageSent(msg))

            delay(400)
            _state.update { it.copy(isTyping = true, isLoading = true) }
            setEvent(ChatEvent.TypingStarted)

            sendChatMessage(msg)
        }
    }

    fun resetChat() {
        disconnectChat()
        _state.update { it.copy(
            messages = emptyList(),
            currentInput = "",
            isLoading = false,
            error = null,
            isTyping = false,
            connectionStatus = ChatState.ConnectionStatus.Disconnected
        )}
        appendBotMessage.reset()
        setEvent(ChatEvent.ChatReset)
        connect()
    }

    override fun onCleared() {
        super.onCleared()
        disconnectChat()
    }
}
