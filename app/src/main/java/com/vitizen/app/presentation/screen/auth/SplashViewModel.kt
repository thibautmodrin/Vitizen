package com.vitizen.app.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    fun checkAuthState() {
        viewModelScope.launch {
            try {
                val user = sessionManager.getCurrentUser()
                _state.value = if (user != null) {
                    SplashState.Authenticated
                } else {
                    SplashState.Unauthenticated
                }
            } catch (e: Exception) {
                _state.value = SplashState.Unauthenticated
            }
        }
    }

    sealed class SplashState {
        object Loading : SplashState()
        object Authenticated : SplashState()
        object Unauthenticated : SplashState()
    }
}