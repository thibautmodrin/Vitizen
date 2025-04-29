package com.vitizen.app.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.vitizen.app.domain.common.Result

interface FirebaseAuthService {
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signUp(email: String, password: String): Result<FirebaseUser>
    suspend fun signOut(): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun deleteUser(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun getCurrentUser(): FirebaseUser?
    fun isEmailVerified(): Boolean
} 