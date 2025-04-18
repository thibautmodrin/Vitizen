package com.vitizen.app.ui.event

sealed class SignUpEvent {
    data class UpdateName(val name: String) : SignUpEvent()
    data class UpdateEmail(val email: String) : SignUpEvent()
    data class UpdatePassword(val password: String) : SignUpEvent()
    data class UpdateTelephone(val telephone: String) : SignUpEvent()
    data class UpdateRgpdAccepted(val accepted: Boolean) : SignUpEvent()
    object SignUp : SignUpEvent()
    object NavigateToSignIn : SignUpEvent()
} 