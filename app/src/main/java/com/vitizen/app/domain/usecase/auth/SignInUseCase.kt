package com.vitizen.app.domain.usecase.auth

import com.vitizen.app.domain.model.User
import com.vitizen.app.domain.repository.AuthRepository
import com.vitizen.app.domain.usecase.validation.ValidateEmailUseCase
import com.vitizen.app.domain.usecase.validation.ValidationResult
import com.vitizen.app.services.ConnectivityChecker
import com.vitizen.app.services.Result
import com.vitizen.app.services.SessionManager
import com.vitizen.app.services.SecureCredentialsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val validateEmail: ValidateEmailUseCase,
    private val connectivityChecker: ConnectivityChecker,
    private val secureCredentialsManager: SecureCredentialsManager
) {
    operator fun invoke(
        email: String,
        password: String
    ): Flow<Result<User>> = flow {
        // Valider l'email
        when (val emailResult = validateEmail(email)) {
            is ValidationResult.Error -> {
                emit(Result.Error(Exception(emailResult.message)))
                return@flow
            }
            ValidationResult.Success -> {}
        }

        // Vérifier d'abord les identifiants sauvegardés
        val (savedEmail, savedPassword) = secureCredentialsManager.getCredentials()
        if (savedEmail.isNotEmpty() && savedPassword.isNotEmpty() && 
            savedEmail == email && savedPassword == password) {
            // Si pas de connexion internet et identifiants valides, essayer la connexion locale
            if (!connectivityChecker.isConnected()) {
                val localResult = sessionManager.tryLocalSignIn()
                when (localResult) {
                    is Result.Success -> {
                        // Restaurer la session avec l'utilisateur local
                        sessionManager.saveSession(localResult.data)
                        emit(Result.Success(localResult.data))
                        return@flow
                    }
                    is Result.Error -> {
                        emit(Result.Error(Exception("Pas de connexion internet")))
                        return@flow
                    }
                    is Result.Loading -> {
                        emit(Result.Loading)
                        return@flow
                    }
                }
            }
        }

        // Si pas de connexion internet et pas d'identifiants valides
        if (!connectivityChecker.isConnected()) {
            emit(Result.Error(Exception("Pas de connexion internet et identifiants invalides")))
            return@flow
        }

        // Tenter la connexion en ligne
        emit(Result.Loading)
        authRepository.signIn(email, password).collect { result ->
            when (result) {
                is Result.Success -> {
                    if (result.data.isEmailVerified) {
                        sessionManager.saveSession(result.data)
                        secureCredentialsManager.saveCredentials(email, password)
                        emit(Result.Success(result.data))
                    } else {
                        emit(Result.Error(Exception("L'email n'est pas vérifié")))
                    }
                }
                is Result.Error -> emit(result)
                is Result.Loading -> emit(result)
            }
        }
    }.catch { throwable ->
        emit(Result.Error(Exception(throwable.message ?: "Une erreur inconnue s'est produite")))
    }
}