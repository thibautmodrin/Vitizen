package com.vitizen.app.domain.usecase.validation

import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult.Error("Le mot de passe ne peut pas être vide")
        }
        if (password.length < 8) {
            return ValidationResult.Error("Le mot de passe doit contenir au moins 8 caractères")
        }
        if (!password.any { it.isDigit() }) {
            return ValidationResult.Error("Le mot de passe doit contenir au moins un chiffre")
        }
        if (!password.any { it.isUpperCase() }) {
            return ValidationResult.Error("Le mot de passe doit contenir au moins une majuscule")
        }
        if (!password.any { it.isLowerCase() }) {
            return ValidationResult.Error("Le mot de passe doit contenir au moins une minuscule")
        }
        return ValidationResult.Success
    }
} 