package com.vitizen.app.presentation.screen.auth

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.vitizen.app.data.util.network.ConnectivityChecker
import com.vitizen.app.data.util.logging.Logger
import com.vitizen.app.domain.common.Result
import com.vitizen.app.data.local.security.SecureCredentialsManager
import com.vitizen.app.presentation.session.SessionManager
import com.vitizen.app.presentation.event.ErrorType
import com.vitizen.app.presentation.event.ToastDuration
import com.vitizen.app.presentation.event.UiEvent
import com.vitizen.app.presentation.navigation.NavigationRoutes
import com.vitizen.app.presentation.viewmodel.BaseViewModel
import com.vitizen.app.domain.repository.FirebaseAuthService
import com.vitizen.app.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val sessionManager: SessionManager,
    private val logger: Logger,
    private val secureCredentialsManager: SecureCredentialsManager,
    private val connectivityChecker: ConnectivityChecker,
    private val firebaseAuthService: FirebaseAuthService
) : BaseViewModel<AuthState, UiEvent>() {

    fun signIn(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            try {
                setLoading(true)
                clearError()
                clearEvent()

                // Validation de l'email vide
                if (email.isBlank()) {
                    setError("Veuillez renseigner votre adresse email")
                    setEvent(
                        UiEvent.AuthError(
                        message = "Veuillez renseigner votre adresse email",
                        errorType = ErrorType.VALIDATION_ERROR
                    ))
                    setLoading(false)
                    return@launch
                }

                // Validation du mot de passe vide
                if (password.isBlank()) {
                    setError("Veuillez indiquer votre mot de passe")
                    setEvent(
                        UiEvent.AuthError(
                        message = "Veuillez indiquer votre mot de passe",
                        errorType = ErrorType.VALIDATION_ERROR
                    ))
                    setLoading(false)
                    return@launch
                }

                signInUseCase(email, password).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val user = result.data
                            sessionManager.saveSession(user, rememberMe)
                            setState(AuthState.Authenticated(user))
                            setEvent(
                                UiEvent.AuthSuccess(
                                message = "Connexion réussie",
                                route = NavigationRoutes.HOME
                            ))
                        }
                        is Result.Error -> {
                            val errorMessage = getErrorMessage(result.exception)
                            val errorType = getErrorType(result.exception)
                            setState(AuthState.Error(errorMessage))
                            setError(errorMessage)
                            setEvent(
                                UiEvent.AuthError(
                                message = errorMessage,
                                errorType = errorType
                            ))
                        }
                        is Result.Loading -> {
                            // Ne pas mettre à jour l'état ici pour éviter les conflits
                        }
                    }
                }
            } catch (e: Exception) {
                logger.e("SignInViewModel", "Erreur lors de la connexion", e)
                val errorMessage = getErrorMessage(e)
                val errorType = getErrorType(e)
                setState(AuthState.Error(errorMessage))
                setError(errorMessage)
                setEvent(
                    UiEvent.AuthError(
                    message = errorMessage,
                    errorType = errorType
                ))
            } finally {
                setLoading(false)
            }
        }
    }

    fun tryLocalSignIn() {
        viewModelScope.launch {
            try {
                setLoading(true)
                clearError()
                clearEvent()

                when (val result = sessionManager.tryLocalSignIn()) {
                    is Result.Success -> {
                        setState(AuthState.Authenticated(result.data))
                        setEvent(
                            UiEvent.AuthSuccess(
                            message = "Connexion locale réussie",
                            route = NavigationRoutes.HOME
                        ))
                    }
                    is Result.Error -> {
                        val errorMessage = getErrorMessage(result.exception)
                        val errorType = getErrorType(result.exception)
                        setState(AuthState.Error(errorMessage))
                        setError(errorMessage)
                        setEvent(
                            UiEvent.AuthError(
                            message = errorMessage,
                            errorType = errorType
                        ))
                    }
                    is Result.Loading -> {
                        // Ne pas mettre à jour l'état ici
                    }
                }
            } catch (e: Exception) {
                logger.e("SignInViewModel", "Erreur lors de la connexion locale", e)
                val errorMessage = getErrorMessage(e)
                val errorType = getErrorType(e)
                setState(AuthState.Error(errorMessage))
                setError(errorMessage)
                setEvent(
                    UiEvent.AuthError(
                    message = errorMessage,
                    errorType = errorType
                ))
            } finally {
                setLoading(false)
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                setLoading(true)
                clearError()
                clearEvent()

                if (email.isBlank()) {
                    setError("Veuillez renseigner votre adresse email")
                    setEvent(
                        UiEvent.AuthError(
                        message = "Veuillez renseigner votre adresse email",
                        errorType = ErrorType.VALIDATION_ERROR
                    ))
                    return@launch
                }

                when (val result = firebaseAuthService.sendPasswordResetEmail(email)) {
                    is Result.Success -> {
                        setEvent(
                            UiEvent.AuthSuccess(
                            message = "Un email de réinitialisation a été envoyé à votre adresse",
                            route = null
                        ))
                        setEvent(
                            UiEvent.ShowToast(
                            message = "Email de réinitialisation envoyé avec succès",
                            duration = ToastDuration.Short
                        ))
                    }
                    is Result.Error -> {
                        val errorMessage = getErrorMessage(result.exception)
                        val errorType = getErrorType(result.exception)
                        setError(errorMessage)
                        setEvent(
                            UiEvent.AuthError(
                            message = errorMessage,
                            errorType = errorType
                        ))
                    }
                    is Result.Loading -> {
                        // Ne rien faire ici
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "Une erreur s'est produite lors de l'envoi de l'email de réinitialisation"
                setError(errorMessage)
                setEvent(
                    UiEvent.AuthError(
                    message = errorMessage,
                    errorType = ErrorType.AUTHENTICATION_ERROR
                ))
            } finally {
                setLoading(false)
            }
        }
    }

    private fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> "Mot de passe incorrect"
                    "ERROR_USER_NOT_FOUND" -> "Aucun compte trouvé avec cette adresse email"
                    "ERROR_USER_DISABLED" -> "Ce compte a été désactivé"
                    "ERROR_TOO_MANY_REQUESTS" -> "Trop de tentatives de connexion. Veuillez réessayer plus tard"
                    "ERROR_OPERATION_NOT_ALLOWED" -> "La connexion avec email/mot de passe n'est pas activée"
                    "ERROR_INVALID_EMAIL" -> "L'adresse email n'est pas valide"
                    else -> "Erreur d'authentification : ${exception.message}"
                }
            }
            else -> exception.message ?: "Une erreur inattendue s'est produite"
        }
    }

    private fun getErrorType(exception: Exception): ErrorType {
        return when (exception) {
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> ErrorType.AUTHENTICATION_ERROR
                    "ERROR_USER_NOT_FOUND" -> ErrorType.AUTHENTICATION_ERROR
                    "ERROR_USER_DISABLED" -> ErrorType.AUTHENTICATION_ERROR
                    "ERROR_TOO_MANY_REQUESTS" -> ErrorType.AUTHENTICATION_ERROR
                    "ERROR_OPERATION_NOT_ALLOWED" -> ErrorType.AUTHENTICATION_ERROR
                    "ERROR_INVALID_EMAIL" -> ErrorType.VALIDATION_ERROR
                    else -> ErrorType.AUTHENTICATION_ERROR
                }
            }
            else -> ErrorType.AUTHENTICATION_ERROR
        }
    }
}