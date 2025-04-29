package com.vitizen.app.presentation.screen.auth

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val shouldNavigateToSignUp: Boolean = false
)