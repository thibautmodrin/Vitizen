package com.vitizen.app.domain.usecase

import com.vitizen.app.domain.model.User
import com.vitizen.app.domain.repository.AuthRepository
import com.vitizen.app.data.util.network.ConnectivityChecker
import com.vitizen.app.domain.common.Result
import com.vitizen.app.presentation.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val connectivityChecker: ConnectivityChecker
) {
    operator fun invoke(): Flow<Result<User>> = flow {
        emit(Result.Loading)

        try {
            // Vérifier d'abord la session locale
            val localUser = sessionManager.getCurrentUser()
            if (localUser != null) {
                emit(Result.Success(localUser))

                // Si nous avons une connexion internet, vérifions l'état d'authentification en ligne
                if (connectivityChecker.isConnected()) {
                    val onlineUser = authRepository.getCurrentUser()
                    val onlineFlow = when (onlineUser) {
                        null -> flowOf(Result.Error(Exception("Utilisateur non trouvé")) as Result<User>)
                        else -> flowOf(Result.Success(onlineUser))
                    }
                    val onlineResult = validateAndProcessUser(onlineFlow)
                    emit(onlineResult)
                }
            } else {
                // Pas de session locale, vérifier l'état d'authentification en ligne si possible
                if (connectivityChecker.isConnected()) {
                    val onlineUser = authRepository.getCurrentUser()
                    val onlineFlow = when (onlineUser) {
                        null -> flowOf(Result.Error(Exception("Utilisateur non trouvé")) as Result<User>)
                        else -> flowOf(Result.Success(onlineUser))
                    }
                    val onlineResult = validateAndProcessUser(onlineFlow)
                    emit(onlineResult)
                } else {
                    emit(Result.Error(Exception("Pas de connexion internet et pas de session locale")))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    private suspend fun validateAndProcessUser(userFlow: Flow<Result<User>>): Result<User> {
        return try {
            val result = userFlow.first()
            when (result) {
                is Result.Success -> {
                    if (result.data.isEmailVerified) {
                        sessionManager.saveSession(result.data)
                        Result.Success(result.data)
                    } else {
                        sessionManager.clearSession()
                        Result.Error(Exception("L'email n'est pas vérifié"))
                    }
                }
                is Result.Error -> {
                    sessionManager.clearSession()
                    result
                }
                is Result.Loading -> result
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}


