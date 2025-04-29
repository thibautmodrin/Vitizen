package com.vitizen.app.presentation.screen.home

import androidx.lifecycle.viewModelScope
import com.vitizen.app.domain.common.Result
import com.vitizen.app.presentation.session.SessionManager
import com.vitizen.app.presentation.viewmodel.BaseViewModel
import com.vitizen.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : BaseViewModel<User?, HomeViewModel.UiEvent>() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                setLoading(true)
                // Essayer d'abord la connexion locale
                when (val result = sessionManager.tryLocalSignIn()) {
                    is Result.Success<User> -> {
                        _user.value = result.data
                        setState(result.data)
                    }
                    is Result.Error -> {
                        // Si la connexion locale échoue, essayer de récupérer l'utilisateur courant
                        val currentUser = sessionManager.getCurrentUser()
                        if (currentUser != null) {
                            _user.value = currentUser
                            setState(currentUser)
                        } else {
                            setEvent(UiEvent.NavigateToSignIn)
                        }
                    }
                    is Result.Loading -> {
                        // Ne rien faire pendant le chargement
                    }
                }
            } catch (e: Exception) {
                setEvent(UiEvent.NavigateToSignIn)
            } finally {
                setLoading(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                sessionManager.clearSession()
                _user.value = null
                setEvent(UiEvent.NavigateToSignIn)
            } catch (e: Exception) {
                setError("Erreur lors de la déconnexion")
            }
        }
    }

    sealed class UiEvent {
        object NavigateToSignIn : UiEvent()
    }
}