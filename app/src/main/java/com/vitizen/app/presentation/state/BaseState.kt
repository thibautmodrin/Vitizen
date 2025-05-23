package com.vitizen.app.presentation.state

sealed class BaseState {
    object Initial : BaseState()
    object Loading : BaseState()
    data class Error(val message: String) : BaseState()
    object Success : BaseState()
}