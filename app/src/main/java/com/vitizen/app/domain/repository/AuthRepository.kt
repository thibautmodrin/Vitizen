package com.vitizen.app.domain.repository

import com.vitizen.app.domain.model.User
import com.vitizen.app.services.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Flow<Result<User>>
    suspend fun signUp(email: String, password: String): Flow<Result<User>>
    suspend fun signOut(): Flow<Result<Unit>>
    suspend fun sendEmailVerification(): Flow<Result<Unit>>
    suspend fun deleteUser(): Flow<Result<Unit>>
    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun tryLocalSignIn(): Result<User>
} 