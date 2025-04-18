package com.vitizen.app.domain.model

data class User(
    val uid: String,
    val email: String,
    val role: String = "user",
    val isEmailVerified: Boolean = false
) 