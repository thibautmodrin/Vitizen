package com.vitizen.app.domain.usecase.validation

import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult.Error("L'email ne peut pas être vide")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult.Error("Format d'email invalide")
        }
        return ValidationResult.Success
    }
} 