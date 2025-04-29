package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.model.User
import com.vitizen.app.domain.repository.AuthRepository
import com.vitizen.app.data.util.network.ConnectivityChecker
import com.vitizen.app.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val connectivityChecker: ConnectivityChecker,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Flow<Result<User>> = flow {
        // Vérification de la connexion
        if (!connectivityChecker.isConnected()) {
            emit(Result.Error(Exception("Pas de connexion internet")))
            return@flow
        }

        // Validation des champs
        when (val emailValidation = validateEmailUseCase(email)) {
            is ValidationResult.Error -> {
                emit(Result.Error(Exception(emailValidation.message)))
                return@flow
            }
            ValidationResult.Success -> {}
        }

        when (val passwordValidation = validatePasswordUseCase(password)) {
            is ValidationResult.Error -> {
                emit(Result.Error(Exception(passwordValidation.message)))
                return@flow
            }
            ValidationResult.Success -> {}
        }

        if (password != confirmPassword) {
            emit(Result.Error(Exception("Les mots de passe ne correspondent pas")))
            return@flow
        }

        try {
            authRepository.signUp(email, password).collect { result ->
                emit(result)
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
} 