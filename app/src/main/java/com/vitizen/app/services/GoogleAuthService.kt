package com.vitizen.app.services

import android.content.Context
import android.content.Intent
import com.vitizen.app.domain.model.User
import com.vitizen.app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthService @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun handleGoogleResult(intentData: Intent): Result<User> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intentData)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            
            authResult.user?.let { firebaseUser ->
                Result.Success(
                    User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        isEmailVerified = firebaseUser.isEmailVerified
                    )
                )
            } ?: Result.Error(Exception("Échec de la connexion Google"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
} 