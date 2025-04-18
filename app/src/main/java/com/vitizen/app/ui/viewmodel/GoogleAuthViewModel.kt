package com.vitizen.app.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.services.GoogleAuthService
import com.vitizen.app.services.Result
import com.vitizen.app.ui.state.AuthState
import com.vitizen.app.ui.event.UiEvent
import com.vitizen.app.domain.repository.AuthRepository
import com.vitizen.app.services.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val googleAuthService: GoogleAuthService,
    private val authRepository: AuthRepository,
    private val logger: Logger
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _uiEvent = MutableStateFlow<UiEvent?>(null)
    val uiEvent: StateFlow<UiEvent?> = _uiEvent.asStateFlow()

    suspend fun getSignInIntent(): Intent {
        return googleAuthService.getSignInIntent()
    }

    fun handleGoogleResult(intent: Intent) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = googleAuthService.handleGoogleResult(intent)
                when (result) {
                    is Result.Success -> {
                        _authState.value = AuthState.Authenticated(result.data)
                        _uiEvent.value = UiEvent.Navigate("home")
                    }
                    is Result.Error -> {
                        _authState.value = AuthState.Error(result.exception.message ?: "Erreur inconnue")
                        _uiEvent.value = UiEvent.ShowSnackbar(result.exception.message ?: "Erreur inconnue")
                    }
                    is Result.Loading -> {
                        _authState.value = AuthState.Loading
                    }
                }
            } catch (e: Exception) {
                logger.e("GoogleAuthViewModel", "Erreur lors de la connexion Google", e)
                _authState.value = AuthState.Error(e.message ?: "Erreur inconnue")
                _uiEvent.value = UiEvent.ShowSnackbar(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun clearUiEvent() {
        _uiEvent.value = null
    }
} 