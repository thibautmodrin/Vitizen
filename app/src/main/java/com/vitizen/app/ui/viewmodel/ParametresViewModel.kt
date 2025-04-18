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
        val notificationsEnabled: Boolean = true
    )
} 