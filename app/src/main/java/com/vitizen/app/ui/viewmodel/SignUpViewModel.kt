package com.vitizen.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.vitizen.app.domain.usecase.auth.SignUpUseCase
import com.vitizen.app.services.Result
import com.vitizen.app.ui.state.AuthState
import com.vitizen.app.ui.event.UiEvent
import com.vitizen.app.ui.event.ErrorType
import com.vitizen.app.ui.navigation.NavigationRoutes
import com.vitizen.app.services.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuthException

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val logger: Logger
) : BaseViewModel<AuthState, UiEvent>() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _rgpdAccepted = MutableStateFlow(false)
    val rgpdAccepted: StateFlow<Boolean> = _rgpdAccepted.asStateFlow()

    private val _justSignedUp = MutableStateFlow(false)
    val justSignedUp: StateFlow<Boolean> = _justSignedUp.asStateFlow()

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun updateRgpdAccepted(value: Boolean) {
        _rgpdAccepted.value = value
    }

    fun resetForm() {
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _rgpdAccepted.value = false
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                setLoading(true)
                clearError()
                clearEvent()
                
                signUpUseCase(email, password, confirmPassword).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _justSignedUp.value = true
                            setState(AuthState.EmailVerificationRequired(email))
                            setEvent(UiEvent.AuthSuccess(
                                message = "Un email de confirmation a été envoyé à votre adresse. Veuillez vérifier votre boîte de réception.",
                                route = NavigationRoutes.SIGN_IN
                            ))
                        }
                        is Result.Error -> {
                            val errorMessage = getErrorMessage(result.exception)
                            val errorType = getErrorType(result.exception)
                            setState(AuthState.Error(errorMessage))
                            setError(errorMessage)
                            setEvent(UiEvent.AuthError(
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
                logger.e("SignUpViewModel", "Erreur lors de l'inscription", e)
                val errorMessage = "Une erreur inattendue s'est produite"
                setState(AuthState.Error(errorMessage))
                setError(errorMessage)
                setEvent(UiEvent.AuthError(
                    message = errorMessage,
                    errorType = ErrorType.AUTHENTICATION_ERROR
                ))
            } finally {
                setLoading(false)
            }
        }
    }

    fun clearJustSignedUp() {
        _justSignedUp.value = false
    }

    private fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Cette adresse email est déjà utilisée"
                    "ERROR_WEAK_PASSWORD" -> "Le mot de passe est trop faible"
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
                    "ERROR_EMAIL_ALREADY_IN_USE" -> ErrorType.VALIDATION_ERROR
                    "ERROR_WEAK_PASSWORD" -> ErrorType.VALIDATION_ERROR
                    "ERROR_INVALID_EMAIL" -> ErrorType.VALIDATION_ERROR
                    else -> ErrorType.AUTHENTICATION_ERROR
                }
            }
            else -> ErrorType.AUTHENTICATION_ERROR
        }
    }
} 