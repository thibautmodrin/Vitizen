package com.vitizen.app.presentation.screen.auth

sealed class SignInEvent {
    data class UpdateEmail(val email: String) : SignInEvent()
    data class UpdatePassword(val password: String) : SignInEvent()
    object SignIn : SignInEvent()
    object NavigateToSignUp : SignInEvent()
    object ResetNavigation : SignInEvent()
} 