package com.vitizen.app.data.repository

import com.vitizen.app.data.local.datasource.RoomDataSource
import com.vitizen.app.data.local.datasource.UserPreferencesManager
import com.vitizen.app.domain.model.User
import com.vitizen.app.domain.repository.AuthRepository
import com.vitizen.app.domain.repository.FirebaseAuthService
import com.vitizen.app.domain.common.Result
import com.vitizen.app.data.util.logging.Logger
import com.vitizen.app.data.util.validation.EmailVerifier
import com.vitizen.app.presentation.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val roomDataSource: RoomDataSource,
    private val userPreferencesManager: UserPreferencesManager,
    private val logger: Logger,
    private val emailVerifier: EmailVerifier,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            if (!emailVerifier.isValidEmail(email)) {
                emit(Result.Error(Exception("Email invalide")))
                return@flow
            }

            val result = firebaseAuthService.signIn(email, password)
            when (result) {
                is Result.Success -> {
                    val user = User(
                        uid = result.data.uid,
                        email = result.data.email ?: "",
                        isEmailVerified = result.data.isEmailVerified
                    )
                    sessionManager.saveSession(user)
                    logger.i("AuthRepository", "Utilisateur connecté avec succès: ${user.email}")
                    emit(Result.Success(user))
                }
                is Result.Error -> emit(Result.Error(result.exception))
                is Result.Loading -> emit(Result.Loading)
            }
        } catch (e: Exception) {
            logger.e("AuthRepository", "Erreur lors de la connexion", e)
            emit(Result.Error(e))
        }
    }

    override suspend fun signUp(email: String, password: String): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            if (!emailVerifier.isValidEmail(email)) {
                emit(Result.Error(Exception("Email invalide")))
                return@flow
            }

            // Création de l'utilisateur
            val result = firebaseAuthService.signUp(email, password)
            when (result) {
                is Result.Success -> {
                    val user = User(
                        uid = result.data.uid,
                        email = result.data.email ?: "",
                        isEmailVerified = result.data.isEmailVerified
                    )
                    
                    // Envoi de l'email de vérification
                    val verificationResult = firebaseAuthService.sendEmailVerification()
                    when (verificationResult) {
                        is Result.Success -> {
                            // On ne sauvegarde pas la session car l'email n'est pas vérifié
                            logger.i("AuthRepository", "Utilisateur enregistré avec succès: ${user.email}")
                            emit(Result.Success(user))
                        }
                        is Result.Error -> {
                            // Suppression de l'utilisateur si l'email de vérification n'a pas pu être envoyé
                            firebaseAuthService.deleteUser()
                            emit(Result.Error(verificationResult.exception))
                        }
                        is Result.Loading -> emit(Result.Loading)
                    }
                }
                is Result.Error -> emit(Result.Error(result.exception))
                is Result.Loading -> emit(Result.Loading)
            }
        } catch (e: Exception) {
            logger.e("AuthRepository", "Erreur lors de l'enregistrement", e)
            emit(Result.Error(e))
        }
    }

    override suspend fun signOut(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val result = firebaseAuthService.signOut()
            sessionManager.clearSession()
            logger.i("AuthRepository", "Utilisateur déconnecté avec succès")
            emit(result)
        } catch (e: Exception) {
            logger.e("AuthRepository", "Erreur lors de la déconnexion", e)
            emit(Result.Error(e))
        }
    }

    override suspend fun sendEmailVerification(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val result = firebaseAuthService.sendEmailVerification()
            if (result is Result.Success) {
                logger.i("AuthRepository", "Email de vérification envoyé avec succès")
            } else {
                logger.e("AuthRepository", "Échec de l'envoi de l'email de vérification", (result as Result.Error).exception)
            }
            emit(result)
        } catch (e: Exception) {
            logger.e("AuthRepository", "Erreur lors de l'envoi de l'email de vérification", e)
            emit(Result.Error(e))
        }
    }

    override suspend fun deleteUser(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val result = firebaseAuthService.deleteUser()
            if (result is Result.Success) {
                sessionManager.clearSession()
                logger.i("AuthRepository", "Utilisateur supprimé avec succès")
                emit(Result.Success(Unit))
            } else {
                logger.e("AuthRepository", "Échec de la suppression de l'utilisateur", (result as Result.Error).exception)
                emit(result)
            }
        } catch (e: Exception) {
            logger.e("AuthRepository", "Erreur lors de la suppression de l'utilisateur", e)
            emit(Result.Error(e))
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuthService.getCurrentUser()
        return firebaseUser?.let {
            User(
                uid = it.uid,
                email = it.email ?: "",
                isEmailVerified = it.isEmailVerified
            )
        }
    }

    override suspend fun logout() {
        try {
            firebaseAuthService.signOut()
            sessionManager.clearSession()
            logger.i("AuthRepository", "Utilisateur déconnecté avec succès")
        } catch (e: Exception) {
            logger.e("AuthRepository", "Erreur lors de la déconnexion", e)
        }
    }

    override suspend fun tryLocalSignIn(): Result<User> {
        return sessionManager.tryLocalSignIn()
    }
} 