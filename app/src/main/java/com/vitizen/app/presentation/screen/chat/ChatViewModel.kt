package com.vitizen.app.presentation.screen.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.domain.model.ChatMessage
import com.vitizen.app.domain.repository.IChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.plus

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: IChatRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Ajouter le message de l'utilisateur
                _messages.value = _messages.value + ChatMessage(message = message, isUser = true)

                Log.d("ChatViewModel", "Envoi du message: $message")
                chatRepository.sendMessage(message)
                    .onSuccess { response ->
                        Log.d("ChatViewModel", "Réponse reçue: ${response.message}")
                        _messages.value = _messages.value + ChatMessage(
                            message = response.message,
                            isUser = false
                        )
                    }
                    .onFailure { error ->
                        Log.e("ChatViewModel", "Erreur lors de l'envoi du message", error)
                        _error.value = error.message ?: "Une erreur est survenue"
                    }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception lors de l'envoi du message", e)
                _error.value = e.message ?: "Une erreur est survenue"
            } finally {
                _isLoading.value = false
            }
        }
    }
}