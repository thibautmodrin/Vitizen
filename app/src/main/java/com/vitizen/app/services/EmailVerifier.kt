package com.vitizen.app.services

import javax.inject.Inject

class EmailVerifier @Inject constructor() {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
} 