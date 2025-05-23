package com.vitizen.app.presentation.event

sealed class BaseEvent {
    object NavigateBack : BaseEvent()
    data class ShowError(val message: String) : BaseEvent()
    data class ShowSuccess(val message: String) : BaseEvent()
} 