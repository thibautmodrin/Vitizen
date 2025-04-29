package com.vitizen.app.presentation.event

import androidx.compose.material3.SnackbarDuration
import com.vitizen.app.presentation.components.MessageType

sealed class UiEvent {
    object Idle : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class ShowSnackbar(
        val message: String, 
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val messageType: MessageType = MessageType.INFO
    ) : UiEvent()
    data class AuthSuccess(
        val message: String,
        val route: String? = null
    ) : UiEvent()
    data class AuthError(
        val message: String,
        val errorType: ErrorType
    ) : UiEvent()
    data class ShowToast(
        val message: String,
        val duration: ToastDuration = ToastDuration.Short
    ) : UiEvent()
}

enum class ErrorType {
    EMAIL_NOT_VERIFIED,
    NETWORK_ERROR,
    AUTHENTICATION_ERROR,
    VALIDATION_ERROR
}

enum class ToastDuration {
    Short,
    Long
} 