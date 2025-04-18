package com.vitizen.app.data.repository

import com.vitizen.app.domain.repository.FirebaseAuthService
import com.vitizen.app.services.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthService {

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                Result.Success(user)
            } ?: Result.Error(Exception("Aucun utilisateur trouvé"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                Result.Success(user)
            } ?: Result.Error(Exception("Échec de la création de l'utilisateur"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.let { user ->
                user.sendEmailVerification().await()
                Result.Success(Unit)
            } ?: Result.Error(Exception("Aucun utilisateur connecté"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteUser(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.let { user ->
                user.delete().await()
                Result.Success(Unit)
            } ?: Result.Error(Exception("Aucun utilisateur connecté"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}