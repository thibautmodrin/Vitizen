package com.vitizen.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.vitizen.app.domain.model.User
import com.vitizen.app.services.SessionManager
import com.vitizen.app.services.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : BaseViewModel<User?, HomeViewModel.UiEvent>() {

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
                        setState(result.data)
                    }
                    is Result.Error -> {
                        // Si la connexion locale échoue, essayer de récupérer l'utilisateur courant
                        val currentUser = sessionManager.getCurrentUser()
                        if (currentUser != null) {
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