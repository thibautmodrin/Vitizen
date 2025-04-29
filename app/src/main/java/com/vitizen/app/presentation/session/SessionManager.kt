package com.vitizen.app.presentation.session

import com.vitizen.app.data.local.datasource.RoomDataSource
import com.vitizen.app.data.local.datasource.UserPreferencesManager
import com.vitizen.app.data.util.network.ConnectivityChecker
import com.vitizen.app.domain.common.Result
import com.vitizen.app.domain.model.User
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val roomDataSource: RoomDataSource,
    private val connectivityChecker: ConnectivityChecker
) {
    suspend fun saveSession(user: User, rememberMe: Boolean = false) {
        if (user.uid.isBlank()) {
            throw Exception("UID utilisateur invalide")
        }
        if (!user.isEmailVerified) {
            throw Exception("L'email n'est pas vérifié")
        }

        // Sauvegarder toujours dans Room pour la session active
        roomDataSource.insertUser(user)

        // Sauvegarder l'UID dans les préférences seulement si rememberMe est true
        if (rememberMe) {
            userPreferencesManager.saveUserUid(user.uid)
        } else {
            // Ne pas effacer les préférences, juste ne pas sauvegarder l'UID
            // Cela permet de garder la session active même sans rememberMe
        }
    }

    suspend fun clearSession() {
        // Supprimer les préférences utilisateur
        userPreferencesManager.clearUser()
        // Supprimer les données de la base locale
        roomDataSource.clearUsers()
    }

    suspend fun getCurrentUser(): User? {
        return try {
            // Essayer d'abord de récupérer l'utilisateur depuis Room
            val lastUser = roomDataSource.getLastUser()
            if (lastUser != null) {
                return lastUser
            }

            // Si pas dans Room, essayer les préférences
            val savedUser = userPreferencesManager.getUser()
            if (savedUser != null) {
                return savedUser
            }

            null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun tryLocalSignIn(): Result<User> {
        return try {
            // Essayer d'abord de récupérer l'utilisateur depuis Room
            val lastUser = roomDataSource.getLastUser()
            if (lastUser != null) {
                if (connectivityChecker.isConnected()) {
                    // Si on a une connexion, on vérifie que l'email est vérifié
                    if (lastUser.isEmailVerified) {
                        Result.Success(lastUser)
                    } else {
                        Result.Error(Exception("L'email n'est pas vérifié"))
                    }
                } else {
                    // Si pas de connexion, on permet la connexion locale
                    Result.Success(lastUser)
                }
            } else {
                // Si pas dans Room, essayer les préférences
                val savedUser = userPreferencesManager.getUser()
                if (savedUser != null) {
                    Result.Success(savedUser)
                } else {
                    Result.Error(Exception("Aucun utilisateur trouvé"))
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}