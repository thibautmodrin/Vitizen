package com.vitizen.app.data.util.validation

import android.util.Patterns
import javax.inject.Inject

class EmailVerifier @Inject constructor() {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}