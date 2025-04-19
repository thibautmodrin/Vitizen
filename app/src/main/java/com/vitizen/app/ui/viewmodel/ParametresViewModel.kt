package com.vitizen.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametresViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ParametresUiState())
    val uiState: StateFlow<ParametresUiState> = _uiState.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    fun startEditing() {
        _isEditing.value = true
    }

    fun saveParametres(
        domaine: String,
        fonction: String,
        surfaceBlanc: String,
        surfaceRouge: String,
        pulverisateur: String,
        typeTraitement: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                domaine = domaine,
                fonction = fonction,
                surfaceBlanc = surfaceBlanc,
                surfaceRouge = surfaceRouge,
                pulverisateur = pulverisateur,
                typeTraitement = typeTraitement
            )
            _isEditing.value = false
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: Implémenter la déconnexion
        }
    }

    data class ParametresUiState(
        val notificationsEnabled: Boolean = true,
        val domaine: String = "",
        val fonction: String = "",
        val surfaceBlanc: String = "",
        val surfaceRouge: String = "",
        val pulverisateur: String = "",
        val typeTraitement: String = ""
    )
} 