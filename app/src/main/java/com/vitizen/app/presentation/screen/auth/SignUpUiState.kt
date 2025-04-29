package com.vitizen.app.presentation.screen.auth

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val telephone: String = "",
    val rgpdAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)