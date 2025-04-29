package com.vitizen.app.presentation.screen.auth

import com.vitizen.app.domain.model.User

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class EmailVerificationRequired(val email: String) : AuthState()
    data class Error(val message: String) : AuthState()
}